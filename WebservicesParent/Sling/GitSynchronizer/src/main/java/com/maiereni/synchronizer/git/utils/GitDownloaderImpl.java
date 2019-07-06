/**
 * ================================================================
 *  Copyright (c) 2017-2019 Maiereni Software and Consulting Inc
 * ================================================================
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maiereni.synchronizer.git.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maiereni.synchronizer.git.service.GitDownloader;
import com.maiereni.synchronizer.git.service.bo.Change;
import com.maiereni.synchronizer.git.service.bo.ChangeType;
import com.maiereni.synchronizer.git.service.bo.GitProperties;
import com.maiereni.synchronizer.git.service.bo.GitResults;
import com.maiereni.synchronizer.git.service.bo.LayoutRule;
import com.maiereni.synchronizer.git.service.bo.LayoutRules;

/**
 * An implementation of the GitDownloader
 * 
 * @author Petre Maierean
 *
 */
public class GitDownloaderImpl implements GitDownloader {
	private static final String REF_HEADS = "refs/heads/";
	private static final String MASTER_BRANCH = "master";
	private static final String REF_HEADS_MASTER = REF_HEADS + MASTER_BRANCH;
	private static final String LOCAL_BRANCH = "local";
	private static final String REF_HEADS_LOCAL = REF_HEADS + LOCAL_BRANCH;
	private static final Logger logger = LoggerFactory.getLogger(GitDownloaderImpl.class);
	public static final String LAYOUT_RULES_FILE = "layoutRules.json";
	private ObjectMapper objectMapper;

	public GitDownloaderImpl() {
		objectMapper = new ObjectMapper();
	}
	/**
	 * Downloads artifacts from the Git Repository defined by the argument
	 * @param properties
	 * @return the result
	 * @throws Exception
	 */
	@Override
	public GitResults download(@Nonnull final GitProperties properties) throws Exception {
		Git git = null;
		if (StringUtils.isBlank(properties.getLocalRepo())) {
			throw new Exception("The local repository cannot be null");
		}
		File fRepo = new File(properties.getLocalRepo());
		if (fRepo.isFile()) {
			throw new Exception("The local repo setting points to a file. It needs to be a directory");			
		}
		boolean isCreated = false;
		if (isRepository(fRepo)) {
			git = Git.open(fRepo);
		}
		else {
			git = createRepository(fRepo, properties);
			isCreated = true;
		}
		GitResults res = downloadProject(git, properties);
		res.setContentPath(new File(fRepo, properties.getContentPath()).toString());
		res.setCreated(isCreated);
		LayoutRules layoutRules = getLayoutRules(res);
		res.setLayoutRules(layoutRules);
		git.close();
		return res;
	}

	LayoutRules getLayoutRules(final File layoutRulesFile) throws Exception {
		LayoutRules ret = null;		
		if (layoutRulesFile.exists()) {
			ret = objectMapper.readValue(layoutRulesFile, LayoutRules.class);
			logger.debug("Read the layout file " + layoutRulesFile.getPath());
		}
		else {
			logger.error("Cannot find a layoutRules file in the repository");
			ret = new LayoutRules();
		}	
		return ret;
	}
	
	private LayoutRules getLayoutRules(final GitResults gitResults) throws Exception {
		File layoutRulesFile = new File(gitResults.getContentPath(), LAYOUT_RULES_FILE);
		return getLayoutRules(layoutRulesFile);
	}
	
	private boolean isRepository(final File dir) {
		boolean ret = dir.exists();
		if (ret) {
			File[] children = dir.listFiles();
			if (children == null || children.length == 0) {
				ret = false;
			}
		}
		return ret;
	}
	private GitResults downloadProject(final Git git, final GitProperties properties) throws Exception {
		GitResults ret = new GitResults();
		String refName = REF_HEADS + properties.getBranchName();
		if (StringUtils.isBlank(properties.getBranchName())) {
			refName = REF_HEADS_MASTER;
		}
		Ref tagRef = null;
		if (StringUtils.isNotBlank(properties.getTagName())) {
			String compTagName = "refs/tags/" + properties.getTagName();
			List<Ref> tags = git.tagList().call();
			for(Ref tag : tags) {
				String tn = tag.getName();
				if (tn.equals(compTagName)) {
					logger.debug("Download repository from: " + tag.getName());
					refName = tag.getName();
					tagRef = tag;
					break;
				}
			}
		}
		List<Ref> branches = git.branchList().call();
		Ref localBranch = null;
		for(Ref branch: branches) {
			String bn = branch.getName();
			if (bn.equals(REF_HEADS_LOCAL)) {
				localBranch = branch;
				break;
			}
		}
		if (localBranch == null) {
			localBranch = git.checkout().
					setCreateBranch(true).
			        setName(LOCAL_BRANCH).
			        setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).
			        setStartPoint(refName).
			        call();
			logger.debug("Created a local clone of the project repository ");
			ret.setCreated(true);
		}
		else {
			List<Change> updates = update(git, properties, localBranch, tagRef);
			ret.setChanges(updates);
		}
		return ret;
	}
	
	private List<Change> update(final Git git, final GitProperties properties, final Ref localBranch, final Ref tagRef) throws Exception {
		logger.debug("Fetch from remote");
		List<Change> ret = null;
		FetchCommand cmd = git.fetch();
		if (StringUtils.isNotBlank(properties.getUserName()) && StringUtils.isNotBlank( properties.getPassword())) {
			logger.debug("Set credentials");
			cmd.setCredentialsProvider( new UsernamePasswordCredentialsProvider(properties.getUserName(), properties.getPassword()));
		}
		if (tagRef != null) {
			RefSpec spec = new RefSpec().setSourceDestination(localBranch.getName(), tagRef.getName());
			List<RefSpec> specs=new ArrayList<RefSpec>();
			specs.add(spec);
			cmd.setRefSpecs(specs);
		}
		FetchResult fr = cmd.call(); 
		Collection<Ref> refs = fr.getAdvertisedRefs();
		for(Ref ref : refs) {
			if (ref.getName().equals("HEAD")) {
				ret = checkDifferences(git, localBranch, ref);
				logger.debug("Rebase on HEAD");				
				RebaseResult rebaseResult = git.rebase().setUpstream(ref.getObjectId()).call();
				if (rebaseResult.getStatus().isSuccessful()) {
					
				}
			}
		}
		return ret;
	}

	private List<Change> checkDifferences(final Git git, final Ref localBranch, final Ref head) throws Exception {
		List<Change> ret = new ArrayList<Change>();
	    Repository repository = git.getRepository();
	    
        AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, localBranch);
        AbstractTreeIterator newTreeParser = prepareTreeParser(repository, head);
        List<DiffEntry> diffs = git.diff().setOldTree(oldTreeParser).setNewTree(newTreeParser).call();
	   
	    for(DiffEntry diff : diffs) {
	    	Change change = new Change();
	    	change.setPathNew(diff.getNewPath());
	    	change.setPathOld(diff.getOldPath());
	    	change.setChangeType(ChangeType.valueOf(diff.getChangeType().name()));
	    	ret.add(change);
	    }
	    return ret;
	}
	
    private AbstractTreeIterator prepareTreeParser(Repository repository, Ref ref) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(ref.getObjectId());
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
            	treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }
	
	private Git createRepository(final File fRepo, final GitProperties properties) throws Exception {
		if (StringUtils.isEmpty(properties.getRemote())) {
			throw new Exception("The remote URL of Git cannot be null");
		}
		logger.debug("Create repository at " + fRepo.getPath());
		CloneCommand cloneCommand = Git.cloneRepository();
		cloneCommand.setURI( properties.getRemote() );
		if (StringUtils.isNotBlank(properties.getUserName()) && StringUtils.isNotBlank( properties.getPassword())) {
			logger.debug("Set credentials");
			cloneCommand.setCredentialsProvider( new UsernamePasswordCredentialsProvider(properties.getUserName(), properties.getPassword()));
		}
		cloneCommand.setDirectory(fRepo);
		cloneCommand.setBranch(properties.getBranchName());
		logger.debug("Clone the repository");
		return cloneCommand.call();
	}
	
	public static void main(String[] args) {
		try {
			GitDownloaderImpl impl = new GitDownloaderImpl();
			LayoutRules rules = new LayoutRules();
			rules.setLayouts(new ArrayList<LayoutRule>());
			LayoutRule r = new LayoutRule();
			r.setName("content");
			r.setInclusionPattern("content[\\x2F|\\x5C].*");
			rules.getLayouts().add(r);
			String s = impl.objectMapper.writeValueAsString(rules);
			System.out.println("Result\r\n" + s);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
