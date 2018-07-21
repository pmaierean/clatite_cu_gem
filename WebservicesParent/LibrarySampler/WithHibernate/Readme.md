## Sample Java application using Spring Framework with JPA (with Hibernate)

This is a very simple Java application that demonstrates a way to employ Spring Framework and JPA to query a database programmatically 
To configure the application pass the following JVM properties:

* database.url - the URL of the database
* database.user - the user name to connect to the database
* database.password - the password to connect to the database
* database.driverClass - the driver class to open a connection 
* database.dialect - the database dialect to be used by Hibernate when dealing with the database
* database.show_sql - true|false 

The application class com.maiereni.sample.hibernate.HibernateBootstrap bootstraps a Spring Framework context in the init() method. The com.maiereni.sample.hibernate.BeanFactory class create the datasource, the JPA entity manager factory bean, an the transaction manager to be used by the application. As part of the initialization process, the DAO implementation class com.maiereni.sample.hibernate.dao.SampleDaoImpl is also created as a singleton and then injected into the application class. Eventually, the application class gets the list of Users using the DAO and lists the names of all POJOs found