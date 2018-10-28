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
package com.maiereni.web.jaxrs.application;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maiereni.web.jaxrs.service.RetirementService;

import io.swagger.annotations.Api;

/**
 * The main implementation of a JAX_RS resource 
 * 
 * @author Petre Maierean
 *
 */
@Path("/")
@Produces("application/json")
@Component("retirement")
@Api(value = "This is the base application")
public class Retirement  {
	private static final Logger logger = LoggerFactory.getLogger(Retirement.class);

	@Autowired
	private RetirementService retirementService;
	
	@GET
	@Path("ping")
	@Produces("text/plain")
	public String ping() {
		logger.debug("Calling ping");
		return retirementService.getServiceName();
	}

}
