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
package com.maiereni.sling.sources.svn;

import java.io.File;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

import com.maiereni.sling.sources.BaseProjectDownloader;
import com.maiereni.sling.sources.DownloaderListener;
import com.maiereni.sling.sources.bo.Repository;

/**
 * API https://svnkit.com/javadoc/index.html
 * Samples https://wiki.svnkit.com/Managing_A_Working_Copy
 * 
 * @author Petre Maierean
 *
 */
public class SvnProjectDownloader extends BaseProjectDownloader {
	private SVNClientManager clientManager;

	public SvnProjectDownloader(DownloaderListener listener, final File root) {
		super(listener, root);
		ISVNOptions options = new DefaultSVNOptions();
		ISVNAuthenticationManager manager = null;
		clientManager = SVNClientManager.newInstance(options, manager);
		clientManager.setEventHandler(new UpdateEventHandler());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void doDownloadProject(final Repository repository) throws Exception {
		File projRepo = new File(root, repository.getName());
		listener.notify(repository, "projectRoot", projRepo.getPath());
		SVNRevision rev = SVNRevision.HEAD;
		if (!projRepo.isDirectory()) {
			if (!projRepo.mkdirs())
				throw new Exception("Cannot make folder at: " + projRepo.getPath());
			SVNURL url = SVNURL.parseURIDecoded(repository.getUrl());
			SVNDepth depth = SVNDepth.UNKNOWN;
			listener.notify(repository, "create", "Create local repository " + projRepo.getPath());
			long l = checkout(url, rev, depth, projRepo, true);
			logger.debug("The project has been checked out: " + l);
		}
		else {
			listener.notify(repository, "update", "Update local repository " + projRepo.getPath());		
			long l = update(projRepo, rev, true);
			logger.debug("The project has been updated out: " + l);
		}
	}
	
	@SuppressWarnings("deprecation")
	private long update( 
		final File wcPath, 
		final SVNRevision updateToRevision, 
		final boolean isRecursive) 
		throws Exception  {
		SVNUpdateClient updateClient = clientManager.getUpdateClient( );
		updateClient.setIgnoreExternals( false );
		return updateClient.doUpdate( wcPath , updateToRevision , isRecursive );
	}
	
	private long checkout(
		final SVNURL url, 
		final SVNRevision revision, 
		final SVNDepth depth,
		final File destPath , 
		final boolean allowUnversionedObstructions ) throws Exception {
		SVNUpdateClient updateClient = clientManager.getUpdateClient( );
		updateClient.setIgnoreExternals( false );
		return updateClient.doCheckout( url , destPath , revision, revision, depth, allowUnversionedObstructions );
     }
	
	public class UpdateEventHandler implements ISVNEventHandler {

	    public void handleEvent( SVNEvent event , double progress ) {
	    	String path = "";
	    	if (event.getFile() != null) {
	    		path = event.getFile().getPath();
	    	}
	    	/*
	         * Gets the current action. An action is represented by SVNEventAction.
	         * In case of an update an  action  can  be  determined  via  comparing 
	         * SVNEvent.getAction() and SVNEventAction.UPDATE_-like constants. 
	         */
	        SVNEventAction action = event.getAction( );
	        String pathChangeType = " ";
	        if ( action == SVNEventAction.UPDATE_ADD ) {
	            /*
	             * the item was added
	             */
	            pathChangeType = "A";
	        } else if ( action == SVNEventAction.UPDATE_DELETE ) {
	            /*
	             * the item was deleted
	             */
	            pathChangeType = "D";
	        } else if ( action == SVNEventAction.UPDATE_UPDATE ) {
	            /*
	             * Find out in details what  state the item is (after  having  been 
	             * updated).
	             * 
	             * Gets  the  status  of  file/directory  item   contents.  It   is 
	             * SVNStatusType  who contains information on the state of an item.
	             */
	            SVNStatusType contentsStatus = event.getContentsStatus( );
	            if ( contentsStatus == SVNStatusType.CHANGED ) {
	                /*
	                 * the  item  was  modified in the repository (got  the changes 
	                 * from the repository
	                 */
	                pathChangeType = "U";
	            } else if ( contentsStatus == SVNStatusType.CONFLICTED ) {
	                /*
	                 * The file item is in  a  state  of Conflict. That is, changes
	                 * received from the repository during an update, overlap  with 
	                 * local changes the user has in his working copy.
	                 */
	                pathChangeType = "C";
	            } else if ( contentsStatus == SVNStatusType.MERGED ) {
	                /*
	                 * The file item was merGed (those  changes that came from  the 
	                 * repository  did  not  overlap local changes and were  merged 
	                 * into the file).
	                 */
	                pathChangeType = "G";
	            }
	        } else if ( action == SVNEventAction.UPDATE_EXTERNAL ) {
	            /*for externals definitions*/
	            logger.debug("Fetching external item into '" + path + "'" );
	            logger.debug( "External at revision " + event.getRevision( ) );
	            return;
	        } else if ( action == SVNEventAction.UPDATE_COMPLETED ) {
	            /*
	             * Working copy update is completed. Prints out the revision.
	             */
	        	logger.debug( "At revision " + event.getRevision( ) );
	            return;
	        } else if ( action == SVNEventAction.ADD ) {
	        	logger.debug( "A     " + path );
	            return;
	        } else if ( action == SVNEventAction.DELETE ) {
	        	logger.debug( "D     " + path );
	            return;
	        } else if ( action == SVNEventAction.LOCKED ) {
	        	logger.debug( "L     " + path );
	            return;
	        } else if ( action == SVNEventAction.LOCK_FAILED ) {
	        	logger.debug( "failed to lock    " + path );
	            return;
	        }

	        /*
	         * Status of properties of an item. SVNStatusType  also
	         * contains information on the properties state.
	         */
	        SVNStatusType propertiesStatus = event.getPropertiesStatus( );
	        String propertiesChangeType = " ";
	        if ( propertiesStatus == SVNStatusType.CHANGED ) {
	            /*
	             * Properties were updated.
	             */
	            propertiesChangeType = "U";
	        } else if ( propertiesStatus == SVNStatusType.CONFLICTED ) {
	            /*
	             * Properties are in conflict with the repository.
	             */
	            propertiesChangeType = "C";
	        } else if ( propertiesStatus == SVNStatusType.MERGED ) {
	            /*
	             * Properties that came from the repository were  merged  with  the
	             * local ones.
	             */
	            propertiesChangeType = "G";
	        }

	        /*
	         * Gets the status of the lock.
	         */
	        String lockLabel = " ";
	        SVNStatusType lockType = event.getLockStatus();
	        
	        if ( lockType == SVNStatusType.LOCK_UNLOCKED ) {
	            /*
	             * The lock is broken by someone.
	             */
	            lockLabel = "B";
	        }
	        
	        logger.debug( "Updated: " +  pathChangeType + propertiesChangeType + lockLabel + "       " + path );
	    }

	    public void checkCancelled( ) throws SVNCancelException {
	    	//logger.debug("Check Cancelled");
	    }
	}
}
