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
package com.maiereni.sling.logs.interpreter;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.maiereni.sling.logs.interpreter.bo.DatabaseProperties;

/**
 * Configuration factory
 * @author Petre Maierean
 *
 */
@Configuration
public class BeansFactory {
	private static final Logger logger = LoggerFactory.getLogger(BeansFactory.class);
	
	@Bean("databaseProperties")
	public DatabaseProperties getDatabaseProperties() throws Exception {
		DatabaseProperties props = new DatabaseProperties();
		props.setUrl(getSystemProperty("url"));
		props.setUserName(getSystemProperty("userName"));
		props.setPassword(getSystemProperty("password"));
		return props;
	}
	
	@Bean("dataSource")
	public DataSource getDataSource(final DatabaseProperties databaseProperties) {
		BasicDataSource ret = new BasicDataSource();
		ret.setUrl(databaseProperties.getUrl());
		ret.setUsername(databaseProperties.getUserName());
		ret.setPassword(databaseProperties.getPassword());
		ret.setDriverClassName(databaseProperties.getDriverClassName());
		return ret;
	}
	
	@Bean("entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean getEntityManagerFactory(final DatabaseProperties databaseProperties, final DataSource dataSource) {
		logger.debug("Create entity manager factory");
		LocalContainerEntityManagerFactoryBean ret = new LocalContainerEntityManagerFactoryBean();
		ret.setDataSource(dataSource);
		ret.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
		ret.setPackagesToScan(new String[] { "com.maiereni.sling.logs.interpreter.persistence" });
		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		ret.setJpaVendorAdapter(vendorAdapter);
		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", databaseProperties.getJpaDialect());
		//jpaProperties.setProperty("hibernate.show_sql", "true");
		ret.setJpaProperties(jpaProperties);
		return ret;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		logger.debug("Create transaction manager");
	    JpaTransactionManager transactionManager = new JpaTransactionManager();
	    transactionManager.setEntityManagerFactory(emf);
	    return transactionManager;
	}
	
	private String getSystemProperty(final String key) throws Exception {
		String ret = System.getProperty(key);
		if (StringUtils.isEmpty(ret)) {
			throw new Exception("Missing property " + key);
		}
		return ret;
	}	
}
