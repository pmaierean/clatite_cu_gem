## Internationalization

There are two aspects to consider for the application

- the method to detect the locale of the user request
- the method of providing the messages set for a given locale

With Spring Framework MVC, one can choose on of the implementations of the [org.springframework.web.servlet.LocaleResolver](https://docs.spring.io/spring-framework/docs/current/javadoc-api/index.html?org/springframework/web/servlet/LocaleResolver.html). In this sample application, I am using the [org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver](https://docs.spring.io/spring-framework/docs/current/javadoc-api/index.html?org/springframework/web/servlet/i18n/AcceptHeaderLocaleResolver.html) for it is easy to pass the [Accept_Language](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Language) header from the client code running in the user browser. See [localResolver](https://github.com/pmaierean/clatite_cu_gem/blob/master/WebservicesParent/WebsiteParent/WebsiteContent/src/main/webapp/WEB-INF/webApplication.xml)

The set of localized messages can be provided with an implementation of the [org.springframework.context.MessageSource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/index.html?org/springframework/context/MessageSource.html). I am using the [ResourceBundleMessageSource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/index.html?org/springframework/context/support/ResourceBundleMessageSource.html) because it loads and caches message bundle objects from language based properties file located relative to the classpath. The obvious drawback of the method is its static nature. One needs to rebuild and re-deploy the entire application on any message change. If time permits, it is my intention to provide a better alternative of a message source with [Jackrabbit Oak](https://jackrabbit.apache.org/oak/) to allow for a dynamic website building

