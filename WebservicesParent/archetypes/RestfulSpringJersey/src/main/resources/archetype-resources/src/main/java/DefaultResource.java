/**
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
package ${package};

import java.util.Calendar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ${package}.bo.CallResponse;
import ${package}.service.BloggerService;

@Path("sampling")
@Component
@Api(
	value="The resources class"
)
public class DefaultResource {
	private static final Logger logger = LoggerFactory.getLogger(DefaultResource.class);
	
	@Autowired
	private BloggerService bloggerService;
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(
		value = "ping", notes = "A very simple ping-pong operation"
	)
	public String getPing() {
		logger.debug("This is a ping call");
	    return "pong";
	}
    
	@GET
	@Path("/response")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(
		value = "response", notes = "A very operation that gets a JSON response"
	)
	public CallResponse getResponse() {
		logger.debug("This is a call for a response");
		CallResponse response = new CallResponse();
		response.setText(bloggerService.getServiceName());
		response.setDate(Calendar.getInstance());
		return response;
	}
   
	@GET
	@Path("/posting/{id}")
    @ApiOperation(
        value = "Get a posting of a given id",
        notes = "Get a posting of a given id",
        response = CallResponse.class,
        responseContainer = "A string"
    )
	public CallResponse getPosting(@ApiParam(value = "id", required = true) @PathParam("id") String id) {
		CallResponse posting = new CallResponse();
		posting.setId(id);
		try {
			String message = bloggerService.getPosting(id);
			posting.setText(message);
			posting.setDate(Calendar.getInstance());
		}
		catch(Exception e) {
			logger.error("There was an error getting the posting", e);
			posting.setError(e.getMessage());
		}
		return posting;
	}
}
