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
package com.maiereni.web.jaxrs2.application;

import java.util.Calendar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maiereni.web.bo.BlogPosting;
import com.maiereni.web.bo.Status;
import com.maiereni.web.jaxrs2.service.BloggerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * A sample implementation of a JAX_RS resource 
 * 
 * @author Petre Maierean
 *
 */
@Path("blogger2")
@Api(value = "blogger2", description = "This is the base application")
@Produces("application/json")
@Component
public class Blogger implements BloggerInterface {
	private static final Logger logger = LoggerFactory.getLogger(Blogger.class);

	@Autowired
	private BloggerService bloggerService;
	
	public Blogger() {
		logger.debug("My application");
	}
	
	@GET
	@Path("/ping")
    @ApiOperation(
            value = "To be used for a ping",
            notes = "To be used for a ping",
            response = String.class,
            responseContainer = "A string"
        )
	@Override
	public String ping() {
		logger.debug("Calling ping");
		return bloggerService.getServiceName();
	}
	
	@GET
	@Path("/posting/{id}")
    @ApiOperation(
        value = "Get a posting of a given id",
        notes = "Get a posting of a given id",
        response = BlogPosting.class,
        responseContainer = "A string"
    )
	@Override
	public BlogPosting getPosting(@ApiParam(value = "id", required = true) @PathParam("id") String id) {
		BlogPosting posting = new BlogPosting();
		posting.setId(id);
		try {
			String message = bloggerService.getPosting(id);
			posting.setMessage(message);
			posting.setCalendar(Calendar.getInstance());
		}
		catch(Exception e) {
			logger.error("There was an error getting the posting", e);
			posting.setStatus(Status.failure);
			posting.setMessage("Error: " + e.getMessage());
		}
		return posting;
	}
	
}
