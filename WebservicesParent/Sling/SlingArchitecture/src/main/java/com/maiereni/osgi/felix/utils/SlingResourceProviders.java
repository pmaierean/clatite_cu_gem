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
package com.maiereni.osgi.felix.utils;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maiereni.osgi.felix.utils.bo.Property;
import com.maiereni.osgi.felix.utils.bo.Service;
import com.maiereni.osgi.felix.utils.bo.ServiceDescription;
import com.maiereni.osgi.felix.utils.bo.ValidationResponse;
import com.maiereni.osgi.felix.utils.sling.GetRequest;
import com.maiereni.osgi.felix.utils.sling.PostRequest;
import com.maiereni.osgi.felix.utils.sling.SlingInfoDownloader;

/**
 * @author Petre Maierean
 *
 */
public class SlingResourceProviders extends SlingInfoDownloader {
	private static final String TYPE_PROVIDER = ".*(\\x2c|\\x5B)org\\x2Eapache\\x2Esling\\x2Espi\\x2Eresource\\x2Eprovider\\x2EResourceProvider(\\x2c|\\x5D).*";
	private static final Logger logger = LoggerFactory.getLogger(SlingResourceProviders.class);
	private String host, user, pwd;
	private ObjectMapper mapper = new ObjectMapper();
	private PostRequest postRequest;
	private GetRequest getRequest;
	
	private List<Cookie> cookies;
	public SlingResourceProviders() throws Exception {
		mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		host = "localhost:8080";
		user = "admin";
		pwd = "admin";		
		cookies = login(host, user, pwd);
		postRequest = new PostRequest(host, mapper, cookies);
		getRequest = new GetRequest(host, mapper, cookies);
	}

	public void listResourceProviders(final String outputFile) throws Exception {
		logger.debug("List providers");
		List<Service> services = getServices(host, cookies);
		
		List<ServiceDescription> descriptions = new ArrayList<ServiceDescription>();
		for(Service service: services) {
			String types = service.getTypes();
			if (types.matches(TYPE_PROVIDER)) {
				ServiceDescription description = getServiceDescription(service);
				ValidationResponse vr = validateExternalResponse(description.getRoot());
				description.setValidationResponse(vr);
				descriptions.add(description);					
			}
		}
		descriptions.sort(new Comparator<ServiceDescription> () {
			@Override
			public int compare(ServiceDescription o1, ServiceDescription o2) {
				int ret = 0;
				if (o1.getBundleId() > o2.getBundleId())
					ret = 1;
				else if (o1.getBundleId() < o2.getBundleId())
					ret = -1;
				return ret;
			}
		});
		String s = asXML(descriptions, false);
		if (StringUtils.isNotBlank(outputFile)) {
			FileUtils.writeStringToFile(new File(outputFile), s);
		}
		else {
			System.out.println(s);
		}
	}

	private String asXML(final List<ServiceDescription> descriptions, boolean withError) throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.newDocument();
		Element root = document.createElement("resourceProviders");
		document.appendChild(root);
		Element av = document.createElement("available");
		Element nav = document.createElement("notAvailable");
		root.appendChild(av);
		root.appendChild(nav);
		for (ServiceDescription description: descriptions) {
			if (description.getValidationResponse().getStatus() != 0) {
				convert(nav, description);
			}
			else {
				convert(av, description);				
			}
		}
		try (StringWriter sw = new StringWriter()) {
		    Transformer transformer = TransformerFactory.newInstance().newTransformer();
		    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		    transformer.transform(new DOMSource(document), new StreamResult(sw));
		    return sw.toString();
		}	
	}
	
	private void convert(final Element root, final ServiceDescription description) throws Exception {
		Element el = root.getOwnerDocument().createElement("resourceProvider");
		el.setAttribute("serviceId", "" + description.getServiceId());
		el.setAttribute("bundleId", "" + description.getBundleId());
		el.setAttribute("authType", description.getAuthType());
		if (description.getName() != null)
			el.setAttribute("name", description.getName());
		if (description.isAdaptable())
			el.setAttribute("adaptable", "true");
		if (description.isAttributable())
			el.setAttribute("attributable", "true");
		if (description.isModifiable())
			el.setAttribute("modifiable", "true");
		if (description.isRefreshable())
			el.setAttribute("refreshable", "true");
		if (description.isUseResourceAccessSecurity())
			el.setAttribute("useResourceAccessSecurity", "true");
		Element txt = root.getOwnerDocument().createElement("bundleName");
		txt.setTextContent(description.getBundleSymbolicName());
		Element path = root.getOwnerDocument().createElement("path");
		path.setTextContent(description.getRoot());
		Element vr = root.getOwnerDocument().createElement("validation");
		vr.setAttribute("status", "" + description.getValidationResponse().getStatus());
		if (description.getValidationResponse().getStatus() != 0) {
			String error = description.getValidationResponse().getResponseBody();
			if (error.indexOf(EXCEPTION_PATH) > 0) {
				vr.setTextContent("See: " + EXCEPTION_PATH);
			}
			else
				vr.setTextContent(error);
		}
		
		path.appendChild(vr);
		root.appendChild(el);
		el.appendChild(txt);
		el.appendChild(path);
	}
	
	private static final String EXCEPTION_PATH = "org.apache.jackrabbit.oak.jcr.session.SessionImpl.getItem(SessionImpl.java:362)";
	
	private ValidationResponse validateExternalResponse(final String path) {
		ValidationResponse ret = null;
		if (path.toUpperCase().contains("POST")) {
			ret = postRequest.validate(path);
		}
		else {
			ret = getRequest.validate(path);
		}
		return ret;
	}
	

	
	private ServiceDescription getServiceDescription(final Service service) {
		ServiceDescription ret = new ServiceDescription();
		ret.setBundleId(service.getBundleId());
		ret.setServiceId(Integer.parseInt(service.getId()));
		ret.setBundleSymbolicName(service.getBundleSymbolicName());
		for(Property prop: service.getProps()) {
			String key = prop.getKey();
			if (prop.getValue() != null) {
				switch(ServiceDescription.PROPS.indexOf(key)) {
				case 0:
					ret.setRoot(prop.getValue().toString());
					break;
				case 1:
					ret.setName(prop.getValue().toString());
					break;
				case 2:
					ret.setUseResourceAccessSecurity((Boolean)prop.getValue());
					break;
				case 3:
					ret.setModifiable((Boolean)prop.getValue());
					break;
				case 4:
					ret.setAdaptable((Boolean)prop.getValue());
					break;
				case 5:
					ret.setRefreshable((Boolean)prop.getValue());
					break;
				case 6:
					ret.setAttributable((Boolean)prop.getValue());
					break;
				case 7:
					ret.setAuthType(prop.getValue().toString());
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SlingResourceProviders providers = new SlingResourceProviders();
			providers.listResourceProviders(args.length > 0? args[0]: null);
		}
		catch(Exception e) {
			logger.error("Failed to list providers", e);
		}
	}

}
