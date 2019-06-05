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
package com.maiereni.jaas.handler;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.jcr.Session;
import javax.security.auth.spi.LoginModule;

import org.apache.felix.jaas.LoginModuleFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Activator of the authentication framework customization 
 * 
 * @author Petre Maierean
 *
 */
@Component(name = "com.maiereni.jaas.handler.SampleLoginModuleActivator")
public class SampleLoginModuleActivator {
	private static final Logger log = LoggerFactory.getLogger(SampleLoginModuleActivator.class);
	
	@Reference
	private SlingRepository slingRepository;
	
	//private Session session;
	//private AuthenticationTokenEventListener eventListener;
	private volatile Thread activationThread;
	
	private LocalSemaphore localSemaphore = new LocalSemaphore();
	
	@Activate
	protected void activate(final BundleContext bundleContext) {
		activationThread = new Thread(new Runnable() {
			@Override
			public void run() {
				for(int count = 0; count<1000 && !localSemaphore.isAbort(); count++) {
					log.debug("Attempt to register the login module");
					try {
			        	//String workspace = slingRepository.getDefaultWorkspace();
			        	//session = slingRepository.loginService("observation", workspace);
			        	
			        	//registerOakAuthenticationListener(bundleContext);
			        	registerCustomLoginModule(bundleContext);
			        	break;
					} catch (Exception e) {
			            log.error("Unable yet to create an register the SSO login module factory", e);
			        }
		            try {
		                Thread.sleep(1000L);
		            } catch (InterruptedException e) {
		                Thread.currentThread().interrupt();
		            }
				}
			}
		});
		activationThread.setDaemon(true);
		activationThread.run();
	}
	
	@Deactivate
	protected void deactive() {
		localSemaphore.setAbort(true);
	}
	
	
	/*
	private void registerOakAuthenticationListener(final BundleContext bundleContext) throws Exception {
		log.info("Register an Oak listener");
		ObservationManager mgr = session.getWorkspace().getObservationManager();
		if ( mgr instanceof JackrabbitObservationManager ) {
        	eventListener = new AuthenticationTokenEventListener();
			JackrabbitObservationManager observationManager = (JackrabbitObservationManager)mgr;
			OakEventFilter filter = getEventFilter();
			
			String[] globArray = new String[0];
			filter.withIncludeGlobPaths(globArray);
			
			String[] pathArray = new String[0];
			filter.setAdditionalPaths(pathArray);
		
			filter.setIsDeep(true);
            /*
            final Set<String> excludePaths = config.getExcludedPaths().toStringSet();
            if ( !excludePaths.isEmpty() ) {
                filter.setExcludedPaths(excludePaths.toArray(new String[excludePaths.size()]));
            }

            // external
            filter.setNoExternal(!config.includeExternal());

            // types
            filter.setEventTypes(this.getTypes(config));

            // nt:file handling
            filter.withNodeTypeAggregate(new String[] {"nt:file"}, new String[] {"", "jcr:content"});

            // ancestors remove
            filter.withIncludeAncestorsRemove();
			
			observationManager.addEventListener(eventListener, filter);
		}
	}
	
	private OakEventFilter getEventFilter() {
		OakEventFilter filter = FilterFactory.wrap(new JackrabbitEventFilter());

		
		return filter;
	}
	*/
	
	private void registerCustomLoginModule(final BundleContext bundleContext) throws Exception {
    	log.info("Registering the SimpleLoginModule");
        Dictionary<String, Object> props = new Hashtable<String,Object>();
        final String desc = "LoginModule Support for FormAuthenticationHandler";
        props.put(Constants.SERVICE_DESCRIPTION, desc);
        props.put(Constants.SERVICE_VENDOR, "Maiereni Software and Consulting Inc");

        props.put(LoginModuleFactory.JAAS_RANKING, 10000);
        props.put(LoginModuleFactory.JAAS_CONTROL_FLAG, "optional");
        props.put(LoginModuleFactory.JAAS_REALM_NAME, "");
        ServiceRegistration reg = bundleContext.registerService(LoginModuleFactory.class.getName(),
                new LoginModuleFactory() {
                    public LoginModule createLoginModule() {
                        return new SampleLoginModule();
                    }

                    @Override
                    public String toString() {
                        return desc + " (" +SampleLoginModule.class.getName() + ")";
                    }
                },
                props
        );
        
        log.info("Registered SampleLoginModule. Service is " + reg.getReference());
	}
	
	private static class LocalSemaphore {
		private boolean abort = false;
		public synchronized boolean isAbort() {
			return abort;
		}
		public synchronized void setAbort(boolean abort) {
			this.abort = abort;
		}
	}
}
