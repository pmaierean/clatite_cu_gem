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
package com.maiereni.modeling.web.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Petre Maierean
 *
 */
@Component("modelingApplication")
@Path("/modeling")
@Api(value = "/modeling", description = "This is the base application")
@Produces("application/json")
public class ModelingApplication {
	private static final Logger logger = LoggerFactory.getLogger(ModelingApplication.class);
	
	@GET
	@Path("ping")
    @ApiOperation(
        value = "To be used for a ping",
        notes = "To be used for a ping",
        response = String.class,
        responseContainer = "A string"
    )
	@Produces("text/plain")
	public String ping() {
		logger.debug("Do ping");
		return "pong";
	}

	
}
