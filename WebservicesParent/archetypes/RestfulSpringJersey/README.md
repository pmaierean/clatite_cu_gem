The purpose of the archetype is to facilitate the creation of a Java bases micro-service with the following technology stack:

-	Jersey 2.26 (reference link: https://jersey.github.io/documentation/2.26/index.html)
-	FasterXML  (reference link: https://github.com/FasterXML/jackson-jaxrs-providers )
-	Spring Framework 4.3.12 (reference link: https://docs.spring.io/spring/docs/4.3.12.RELEASE/spring-framework-reference/htmlsingle/ )
-	Swagger API 1.5.17 (reference link: https://github.com/swagger-api/swagger-core) 
-	AspectJ  1.8.13 (reference link: https://www.eclipse.org/aspectj/docs.php) 

To use the archetype run the following command:

mvn archetype:generate -DarchetypeGroupId=com.maiereni.archetypes -DarchetypeArtifactId=RestfulSpringJersey -DarchetypeVersion=1.0.0 -DgroupId=com.maiereni.samples -DartifactId=MyRestful

The resulting prototype WAR can run on any web container implementing Servlet 3.0 and JavaServer Pages 2.2. Assuming that the web container binds to the port 8080 when running the resulting prototype WAR:
- its Swagger endpoint can be reached at http://localhost:8080/${artifactId}/swagger.json
- sample ping at http://localhost:8080/${artifactId}/sampling

