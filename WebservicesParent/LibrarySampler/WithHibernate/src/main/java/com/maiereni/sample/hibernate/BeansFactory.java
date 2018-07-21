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
package com.maiereni.sample.hibernate;

import java.util.Properties;

import javax.annotation.Nonnull;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Petre Maierean
 *
 */
@Configuration
@EnableTransactionManagement
public class BeansFactory extends BaseFactory {
	public static final String PERSISTENCE_UNIT_NAME = "databasePUN";
	public static final String URL = "database.url";
	public static final String USER = "database.user";
	public static final String PASSWORD = "database.password";
	public static final String DRIVER = "database.driverClass";
	public static final String DIALECT = "database.dialect";
	public static final String SHOW_SQL = "database.show_sql";
	public static final String TRANSACTION_MANAGER_NAME = "transation_manager";
	
	@Bean(name="jpaProperties")
	public Properties getJpaProperties() throws Exception { 
		Properties properties = new Properties();
		properties.setProperty("hibernate.show_sql", getProperty(SHOW_SQL, "false"));
		properties.setProperty("hibernate.ddl-auto", "none");
		properties.setProperty("hibernate.naming_strategy", "org.hibernate.cfg.DefaultNamingStrategy");
		properties.setProperty("hibernate.hbm2ddl.auto", "none");	
		properties.setProperty("hibernate.format_sql", "false");
		properties.setProperty("hibernate.jdbc.use_get_generated_keys", "true");
		properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
		properties.setProperty("hibernate.dialect", getProperty(DIALECT));
		properties.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
		return properties;		
	}
	
	@Bean(name="dataSource")
	public DataSource dataSource() throws Exception {
		BasicDataSource ret = new BasicDataSource();
		ret.setDriverClassName(getProperty(DRIVER));
		ret.setUrl(getProperty(URL));
		ret.setUsername(getProperty(USER));
		ret.setPassword(getProperty(PASSWORD));
		return ret;
	}  
	
	@Bean(name="entityManager")
	public LocalContainerEntityManagerFactoryBean getSourceEntityManager(
		@Nonnull final DataSource dataSource,
		@Nonnull final Properties jpaProperties) {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(false);
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
		factory.setPackagesToScan("com.maiereni.sample.hibernate.model");
		factory.setDataSource(dataSource);
		factory.setJpaProperties(jpaProperties);
		factory.afterPropertiesSet();
		logger.debug("Create entityManagerFactory");

		return factory;
	}
	
	@Bean(name=TRANSACTION_MANAGER_NAME)
	public PlatformTransactionManager transactionManagerConfig(
		@Nonnull final EntityManagerFactory entityManagerFactory, 
		@Nonnull final DataSource dataSource) throws Exception {
		logger.debug("Create Transaction Manager");
		JpaTransactionManager ret = new JpaTransactionManager(entityManagerFactory);
		ret.setJpaProperties(getJpaProperties());
		ret.setDataSource(dataSource);
		ret.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
		ret.setValidateExistingTransaction(true);
		return ret;
	}
}
