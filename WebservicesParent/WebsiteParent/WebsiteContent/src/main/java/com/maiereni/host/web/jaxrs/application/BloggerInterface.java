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
package com.maiereni.host.web.jaxrs.application;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.maiereni.host.web.bo.BlogPosting;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
/**
 * @author Petre Maierean
 *
 */
@Path("/")
@Api(value = "/blogger", description = "This is the base application")
@Produces("application/json")
public interface BloggerInterface {
	@GET
	@Path("/ping")
    @ApiOperation(
            value = "To be used for a ping",
            notes = "To be used for a ping",
            response = String.class,
            responseContainer = "A string"
        )	
	String ping();
	
	@GET
	@Path("/posting/{id}")
    @ApiOperation(
        value = "Get a posting of a given id",
        notes = "Get a posting of a given id",
        response = BlogPosting.class,
        responseContainer = "A string"
    )
	BlogPosting getPosting(@ApiParam(value = "id", required = true) @PathParam("id") String id);
}
