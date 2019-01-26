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
package com.maiereni.sling.utils;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.sling.launchpad.app.Main;
import org.apache.sling.launchpad.base.shared.SharedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Petre Maierean
 *
 */
public class LoaderAggregator extends Main {
	private static final Logger logger = LoggerFactory.getLogger(LoaderAggregator.class);
    /**
     * The name of the configuration property indicating the socket to use for
     * the control connection. The value of this property is either just a port
     * number (in which case the host is assumed to be <code>localhost</code>)
     * or a host name (or IP address) and port number separated by a colon.
     */
    protected static final String PROP_CONTROL_SOCKET = "sling.control.socket";

    /** The Sling configuration property name setting the initial log level */
    private static final String PROP_LOG_LEVEL = "org.apache.sling.commons.log.level";

    /** The Sling configuration property name setting the initial log file */
    private static final String PROP_LOG_FILE = "org.apache.sling.commons.log.file";

    /**
     * The configuration property setting the port on which the HTTP service
     * listens
     */
    private static final String PROP_PORT = "org.osgi.service.http.port";

    /**
     * The configuration property setting the context path where the HTTP service
     * mounts itself.
     */
    private static final String PROP_CONTEXT_PATH = "org.apache.felix.http.context_path";

    /**
     * Host name or IP Address of the interface to listen on.
     */
    private static final String PROP_HOST = "org.apache.felix.http.host";

    /**
     * Name of the configuration property (or system property) indicating
     * whether the shutdown hook should be installed or not. If this property is
     * not set or set to {@code true} (case insensitive), the shutdown hook
     * properly shutting down the framework is installed on startup. Otherwise,
     * if this property is set to any value other than {@code true} (case
     * insensitive) the shutdown hook is not installed.
     * <p>
     * The respective command line option is {@code -n}.
     */
    private static final String PROP_SHUTDOWN_HOOK = "sling.shutdown.hook";
	
	protected LoaderAggregator(Map<String, String> args) {
		super(args);
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 2) {
				throw new Exception("Expected arguments: sling_home launchpad.jar");
			}
			Map<String, String> props = new HashMap<String, String>();
			//props.put(PROP_CONTROL_SOCKET, "8080");
			props.put(PROP_PORT, "8080");
			props.put(PROP_LOG_LEVEL, "4");
			props.put(SharedConstants.SLING_HOME, args[0]);
			props.put(SharedConstants.SLING_LAUNCHPAD, args[0]);
			props.put(PROP_HOST, "127.0.0.1");
			LoaderAggregator main = new LoaderAggregator(props);
			main.doStart(args[1]);
		}
		catch(Exception e) {
			logger.error("Failed to start the launchpad", e);
		}
	}

	 protected boolean doStart(final String s) throws Exception {
		 File f = new File(s);
		 URL u = new URL("jar:" + f.toURI().toString() + "!/resources/org.apache.sling.launchpad.base.jar"); // f.toURI().toURL();//
		 return doStart(u);
	 }
	
	
    public static Map<String, String> convertCommandLineArgs(Map<String, String> rawArgs) {
        final HashMap<String, String> props = new HashMap<String, String>();
        boolean errorArg = false;
        for (Entry<String, String> arg : rawArgs.entrySet()) {
            if (arg.getKey().length() == 1 || arg.getKey().startsWith("D")) {
                String value = arg.getValue();
                switch (arg.getKey().charAt(0)) {
                    case 'j':
                        if (value == arg.getKey()) {
                        	 logger.error("-j Missing host:port value");
                            errorArg = true;
                            continue;
                        }
                        props.put(PROP_CONTROL_SOCKET, value);
                        break;

                    case 'l':
                        if (value == arg.getKey()) {
                        	 logger.error("-l Missing log level value");
                            errorArg = true;
                            continue;
                        }
                        props.put(PROP_LOG_LEVEL, value);
                        break;

                    case 'f':
                        if (value == arg.getKey()) {
                        	 logger.error("-f Missing log file value");
                            errorArg = true;
                            continue;
                        } else if ("-".equals(value)) {
                            value = "";
                        }
                        props.put(PROP_LOG_FILE, value);
                        break;

                    case 'c':
                        if (value == arg.getKey()) {
                        	 logger.error("-c Missing directory value");
                            errorArg = true;
                            continue;
                        }
                        props.put(SharedConstants.SLING_HOME, value);
                        break;

                    case 'i':
                        if (value == arg.getKey()) {
                        	 logger.error("-i Missing launchpad directory value");
                            errorArg = true;
                            continue;
                        }
                        props.put(SharedConstants.SLING_LAUNCHPAD, value);
                        break;

                    case 'a':
                        if (value == arg.getKey()) {
                        	 logger.error("-a Missing address value");
                            errorArg = true;
                            continue;
                        }
                        props.put(PROP_HOST, value);
                        break;

                    case 'p':
                        if (value == arg.getKey()) {
                        	 logger.error("-p Missing port value");
                            errorArg = true;
                            continue;
                        }
                        try {
                            // just to verify it is a number
                            Integer.parseInt(value);
                            props.put(PROP_PORT, value);
                        } catch (RuntimeException e) {
                        	 logger.error("-p Bad port: " + value);
                            errorArg = true;
                        }
                        break;

                    case 'r':
                        if (value == arg.getKey()) {
                        	 logger.error("-r Missing root path value");
                            errorArg = true;
                            continue;
                        }
                        props.put(PROP_CONTEXT_PATH, value);
                        break;

                    case 'n':
                        props.put(PROP_SHUTDOWN_HOOK, Boolean.FALSE.toString());
                        break;

                    case 'D':
                        if (value == arg.getKey()) {
                            logger.error("-D  Missing property assignment");
                            errorArg = true;
                            continue;
                        }
                        if (arg.getKey().length() > 1) {
                            //Dfoo=bar arg.key=Dfoo and arg.value=bar
                            props.put(arg.getKey().substring(1), arg.getValue());
                        } else {
                            //D foo=bar arg.key=D and arg.value=foo=bar
                            String[] parts = value.split("=");
                            int valueIdx = (parts.length > 1) ? 1 : 0;
                            props.put(parts[0], parts[valueIdx]);
                        }
                        break;

                    default:
                    	 logger.error("-" + arg.getKey() + " Unrecognized option");
                        errorArg = true;
                        break;
                }
            } else if ("start".equals(arg.getKey())
                    || "stop".equals(arg.getKey())
                    || "status".equals(arg.getKey())
                    || "threads".equals(arg.getKey())) {
                props.put(PROP_CONTROL_ACTION, arg.getValue());
            } else {
            	 logger.error(arg.getKey() + " Unrecognized option");
                errorArg = true;
            }
        }
        return errorArg ? null : props;
    }
}
