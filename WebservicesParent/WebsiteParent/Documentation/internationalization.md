## Internationalization

There are two aspects to consider for the application

- the method to detect the locale of the user request
- the method of providing the messages set for a given locale

With Spring Framework MVC, one can choose on of the implementations of the [org.springframework.web.servlet.LocaleResolver](https://docs.spring.io/spring-framework/docs/current/javadoc-api/index.html?org/springframework/web/servlet/view/XmlViewResolver.html). In this sample application, I am using the [org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver](https://docs.spring.io/spring-framework/docs/current/javadoc-api/index.html?org/springframework/web/servlet/view/XmlViewResolver.html) for it is easy to pass the [Accept_Language](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Language) header from the client code running in the user browser

