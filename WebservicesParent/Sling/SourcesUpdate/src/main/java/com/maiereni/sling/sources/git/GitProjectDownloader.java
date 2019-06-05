/**
 * ================================================================
 *  Copyright (c) 2017-2018 Maiereni Software and Consulting Inc
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
package com.maiereni.sling.sources.git;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.notes.Note;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;

import com.maiereni.sling.sources.BaseProjectDownloader;
import com.maiereni.sling.sources.DownloaderListener;
import com.maiereni.sling.sources.bo.Repository;

/**
 * @author Petre Maierean
 *
 */
public class GitProjectDownloader extends BaseProjectDownloader {
	public GitProjectDownloader(final DownloaderListener listener, final File root) {
		super(listener, root);
	}

	@Override
	public void doDownloadProject(final Repository repository) throws Exception {
		logger.debug("Download repository: " + repository.getUrl());
		String branchName = "master";
		Git git = getProjectRepository(repository, branchName);
		//Repository repo = git.getRepository();
		List<Ref> branches = git.branchList().call();
		Ref localBranch = null;
		for(Ref branch: branches) {
			String bn = branch.getName();
			if (bn.equals("refs/heads/" + branchName)) {
				localBranch = branch;
				break;
			}
		}
		if (localBranch == null) {
			listener.notify(repository, "create", "Create branch");
			localBranch = git.checkout().
			        setCreateBranch(true).
			        setName("refs/heads/" + branchName).
			        setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).
			        setStartPoint("origin/" + branchName).
			        call();
			logger.debug("Created a local clone of the project repository ");
		}
		else {
			listener.notify(repository, "update", "Update existing local sources");
			update(git);
		}
	}
	
	private void update(final Git git) throws Exception {
		logger.debug("Fetch from remote");
		FetchResult fr = git.fetch().call(); // .setRemote(url)
		Collection<Ref> refs = fr.getAdvertisedRefs();
		Iterable<RevCommit> logs = git.log().call();
		for (RevCommit rev : logs) {
			PersonIdent authorIdent = rev.getAuthorIdent();
			Date date = authorIdent.getWhen();
			String authName = authorIdent.getName();
			logger.debug("Commit at " + SDF.format(date) + " by " + authName + ": " + rev.getId().name() + " text: " + rev.getShortMessage() );
		}
		List<Note> notes = git.notesList().call();
		for(Ref ref : refs) {
			if (ref.getName().equals("HEAD")) {
				git.rebase().setUpstream(ref.getObjectId()).call();
				logger.debug("Rebase on HEAD");
				for(Note note: notes) {
					if (note.getName().equals(ref.getObjectId().getName())) {
            			logger.debug("Found note: " + note + " for commit " + ref.getName());
            			// displaying the contents of the note is done via a simple blob-read
            			ObjectLoader loader = git.getRepository().open(note.getData());
            			loader.copyTo(System.out);							
					}
				}
			}
		}
	}
	
	private Git getProjectRepository(final Repository repository, final String branchName) 
		throws Exception {
		Git ret = null;
		File fRepo = new File(root, repository.getName());
		listener.notify(repository, "projectRoot", fRepo.getPath());

		if (fRepo.isDirectory()) {
			ret = Git.open(fRepo);
			listener.notify(repository, "update", "Update project");
		}
		else {
			if (!fRepo.mkdirs()) {
				throw new Exception("Cannot create folders at " + fRepo);
			}
			listener.notify(repository, "clone", "Clone remote repository to " + fRepo.getPath());
			ret = cloneRepository(fRepo, repository.getUrl(), branchName);
		}
		logger.debug("The repository is ready");
		return ret;
	}
	
	private Git cloneRepository(final File directory, final String url, final String branchName) 
		throws Exception {
		CloneCommand cloneCommand = Git.cloneRepository();
		cloneCommand.setURI( url );
		/*
		if (StringUtils.isNoneBlank(user, password)) {
			cloneCommand.setCredentialsProvider( new UsernamePasswordCredentialsProvider(user, password));
		}*/
		cloneCommand.setDirectory(directory);
		cloneCommand.setBranch(branchName);
		logger.debug("Clone the repository");
		return cloneCommand.call();
	}
}
