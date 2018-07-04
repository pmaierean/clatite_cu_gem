## Using Apache Tiles for the view resolver with Spring MVC
[Spring MVCs](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html) comes with two class to help interface its view resolver mechanism with [Apache Tiles](https://tiles.apache.org/framework/tutorial/basic/pages.html) 

- [org.springframework.web.servlet.view.tiles3.TilesConfigurer](https://docs.spring.io/spring-framework/docs/current/javadoc-api/index.html?org/springframework/web/servlet/view/XmlViewResolver.html) is used to resolve the view name to a tiles definition
- [org.springframework.web.servlet.view.tiles3.TilesViewResolver](https://docs.spring.io/spring-framework/docs/current/javadoc-api/index.html?org/springframework/web/servlet/view/XmlViewResolver.html) a view resolver that acts a as bridge between Spring MVC and Apache Tiles

Note how both beans are specified explicitly in the [webApplication.xml]
The [tiles.xml] contains the definitions for the views referred thoughout the application