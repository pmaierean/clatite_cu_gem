/**
 * 
 */
package com.maiereni.osgi.felix;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Petre Maierean
 *
 */
public class Bootstrap {
	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
	private static final String OSGI_FW = "META-INF/services/org.osgi.framework.launch.FrameworkFactory";
	private  Map<String, Object> properties;
	
	public Bootstrap(final String[] args) {
	    properties = new HashMap<String, Object>();
	    properties.put("org.osgi.framework.storage","/org/local/sample/osgi/falix/bundle.cache");
	    properties.put("org.osgi.framework.storage.clean", "onFirstInit");
	    // org.osgi.framework.system.packages
	}
	
	public FrameworkFactory getFrameworkFactory() throws Exception {
		URL url = Bootstrap.class.getClassLoader().getResource(OSGI_FW);
		if (url == null) 
			throw new Exception("Could not locate " + OSGI_FW);
		try ( InputStream is = url.openStream();	
			  BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			for (String s = br.readLine(); s != null; s = br.readLine()) {
				s = s.trim();
				if (StringUtils.isNotEmpty(s) && (s.charAt(0) != '#')) {
					logger.debug("Detected framework: " + s);
					return (FrameworkFactory) Class.forName(s).newInstance();
				}
			}
		}
		throw new Exception("Cannot detect a framework factory");
	}
	
	public void bootstrap() throws Exception {
		FrameworkFactory frameworkFactory = getFrameworkFactory();
		Map<String, String> config = new HashMap<String, String>();
		config.put(Constants.FRAMEWORK_STORAGE, "/opt/local/osgi/storage");
		for(Object k : System.getProperties().keySet()) {
			String key = k.toString();
			String value = System.getProperty(key);
			config.put(key, value);
		}
		config.put(Constants.FRAMEWORK_STORAGE_CLEAN, "onFirstInit");
		Framework fwk = frameworkFactory.newFramework(config);
		fwk.start();
		logger.debug("The framework has been started");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new Bootstrap(args).bootstrap();
		}
		catch(Exception e) {
			logger.error("Failed to bootstrap Felix", e);
		}
	}

}
