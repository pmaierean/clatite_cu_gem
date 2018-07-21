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
package com.maiereni.sample.hibernate.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.maiereni.sample.hibernate.SampleDao;
import com.maiereni.sample.hibernate.model.User;

/**
 * @author Petre Maierean
 *
 */
@Repository
public class SampleDaoImpl implements SampleDao {
	private static final Logger logger = LoggerFactory.getLogger(SampleDaoImpl.class);
	
	private EntityManager entityManager;
	
	@Transactional
	@Override
	public List<User> getUsers() throws Exception {
		logger.debug("Get all users");
		TypedQuery<User> query = entityManager.createQuery("from User", User.class);
		return query.getResultList();
	}

}
