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
package com.maiereni.osgi.felix.utils.model;

import com.maiereni.sling.info.Bundle;
import com.maiereni.sling.info.Component;
import com.maiereni.sling.info.Model;
import com.maiereni.sling.info.Service;

/**
 * @author Petre Maierean
 *
 */
class ModelLookup {
	protected Model model;
	public ModelLookup(final Model model) {
		this.model = model;
	}
	/**
	 * Find a bundle by id
	 * @param id
	 * @return
	 */
	public Bundle getBundle(final String id) {
		Bundle ret = null;
		for(Bundle bundle: model.getBundles()) {
			if (bundle.getId().equals(id)) {
				ret = bundle;
				break;
			}
		}
		return ret;
	}
	
	/**
	 * Final a component by id
	 * @param id
	 * @return
	 */
	public Component getComponent(final String id) {
		Component ret = null;
		for(Bundle bundle: model.getBundles()) {
			if (bundle.getComponents() != null && bundle.getComponents().getComponent() != null) {
				for(Component component : bundle.getComponents().getComponent()) {
					if (component.getId().equals(id)) {
						ret = component;
						break;
					}
				}
				if (ret != null) {
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * Final a service by id
	 * @param id
	 * @return
	 */
	public Service getService(final String id) {
		Service ret = null;
		for(Bundle bundle: model.getBundles()) {
			if (bundle.getComponents() != null && bundle.getComponents().getComponent() != null) {
				for(Component component : bundle.getComponents().getComponent()) {
					if (component.getServices() != null && component.getServices().getService() != null) {
						for(Service service: component.getServices().getService()) {
							if (service.getId().equals(id)) {
								ret = service;
								break;
							}
						}
						if (ret != null) {
							break;
						}
 					}
				}
				if (ret != null) {
					break;
				}
			}
			if (ret == null && bundle.getServices() != null && bundle.getServices().getService() != null) {
				for(Service service: bundle.getServices().getService()) {
					if (service.getId().equals(id)) {
						ret = service;
						break;
					}
				}
			}
			if (ret != null) {
				break;
			}
		}	
		return ret;
	}

	/**
	 * Get the component that has a service with the id
	 * @param id
	 * @return
	 */
	public Component getComponentByServiceId(final String id) {
		Component ret = null;
		for(Bundle bundle: model.getBundles()) {
			if (bundle.getComponents() != null && bundle.getComponents().getComponent() != null) {
				for(Component component : bundle.getComponents().getComponent()) {
					if (component.getServices() != null && component.getServices().getService() != null) {
						for(Service service: component.getServices().getService()) {
							if (service.getId().equals(id)) {
								ret = component;
								break;
							}
						}
 					}
					if (ret != null) {
						break;
					}
				}
			}
			if (ret != null) {
				break;
			}
		}
		return ret;
	}
	
	public Component getComponentByServiceName(final String name) {
		Component ret = null;
		for(Bundle bundle: model.getBundles()) {
			if (bundle.getComponents() != null && bundle.getComponents().getComponent() != null) {
				for(Component component : bundle.getComponents().getComponent()) {
					if (component.getServices() != null && component.getServices().getService() != null) {
						for(Service service: component.getServices().getService()) {
							if (service.getType() != null) {
								for(String type: service.getType()) {
									if (type.equals(name)) {
										ret = component;
										break;
									}
								}
								if (ret != null) {
									break;
								}
							}
						}
						if (ret != null) {
							break;
						}
 					}
				}
			}
			if (ret != null) {
				break;
			}
		}
		return ret;	
	}
	
	/**
	 * Gets the bundle that has the component with the id
	 * @param id
	 * @return
	 */
	public Bundle getBundleByComponentId(final String id) {
		Bundle ret = null;
		for(Bundle bundle: model.getBundles()) {
			if (bundle.getComponents() != null && bundle.getComponents().getComponent() != null) {
				for(Component component : bundle.getComponents().getComponent()) {
					if (component.getId().equals(id)) {
						ret = bundle;
						break;
					}
				}
				if (ret != null) {
					break;
				}
			}
		}
		
		return ret;
	}

	/**
	 * Gets the bundle that has a service with the specified id
	 * @param id
	 * @return
	 */
	public Bundle getBundleByServiceId(final String id) {
		Bundle ret = null;
		for(Bundle bundle: model.getBundles()) {
			if (bundle.getServices() != null && bundle.getServices().getService() != null) {
				for(Service service: bundle.getServices().getService()) {
					if (service.getId().equals(id)) {
						ret = bundle;
						break;
					}
				}
				if (ret != null) {
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * Gets the bundle that has a service with the specified id
	 * @param id
	 * @return
	 */
	public Bundle getBundleByServiceName(final String name) {
		Bundle ret = null;
		for(Bundle bundle: model.getBundles()) {
			if (bundle.getServices() != null && bundle.getServices().getService() != null) {
				for(Service service: bundle.getServices().getService()) {
					if (service.getType() != null) {
						for(String type: service.getType()) {
							if (type.equals(name)) {
								ret = bundle;
								break;
							}
						}
						if (ret != null) {
							break;
						}
					}
				}
				if (ret != null) {
					break;
				}
			}
		}
		return ret;
	}

}
