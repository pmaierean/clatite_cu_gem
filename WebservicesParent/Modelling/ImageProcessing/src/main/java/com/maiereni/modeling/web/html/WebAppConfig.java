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
package com.maiereni.modeling.web.html;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

/**
 * The Web application configuration class
 * @author Petre Maierean
 *
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.maiereni.modeling.web.html")
public class WebAppConfig {
	private static final Logger logger = LoggerFactory.getLogger(WebAppConfig.class);
	/**
	 * Constructs the tiles configuration
	 * @return
	 */
    @Bean
    public TilesConfigurer getTilesConfigurer(){
        TilesConfigurer tilesConfigurer = new TilesConfigurer();
        tilesConfigurer.setDefinitions(new String[] {"/WEB-INF/tiles.xml"});
        tilesConfigurer.setCheckRefresh(true);
        logger.debug("Get the tiles configurer");
        return tilesConfigurer;
    }
    /**
     * Constructs the tiles view resolver
     * @return
     */
    @Bean
    public TilesViewResolver getTilesViewResolver() {
    	TilesViewResolver ret = new TilesViewResolver();
        logger.debug("Get the tiles view resolver");    	
    	return ret;
    }
}
