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
package com.maiereni.sling.logs.interpreter.persistence.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maiereni.sling.logs.interpreter.persistence.InterpreterDao;
import com.maiereni.sling.logs.interpreter.persistence.pojo.CommonEntry;
import com.maiereni.sling.logs.interpreter.persistence.pojo.Entry;
import com.maiereni.sling.logs.interpreter.persistence.pojo.EntryIndex;
import com.maiereni.sling.logs.interpreter.persistence.pojo.Record;

/**
 * @author Petre Maierean
 *
 */
@Repository
public class InterpreterDaoImpl implements InterpreterDao {
	@PersistenceContext
	private EntityManager em;

	@Transactional
	@Override
	public Integer addEntry(final Entry entry) throws Exception {
		if (entry != null) {
			Entry e = new Entry();
			e.setThreadName(entry.getThreadName());
			e.setLineNumber(entry.getLineNumber());
			em.persist(e);		
			if (entry.getRecords() != null)
				for(Record r : entry.getRecords()) {
					r.setEntry(e);
					em.persist(r);
				}
		}
		return entry.getId();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public List<EntryIndex> getEntryIndex() throws Exception {
		Query query = em.createNativeQuery(EntryIndex.QUERY_STMT, EntryIndex.class);
		return query.getResultList();		
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public List<Integer> getAssociatedEntryIndexes(Integer entryId, int index) throws Exception {
		Query q = em.createNamedQuery(EntryIndex.GET_ASSOCIATED);
		q.setParameter("entityId", entryId);
		q.setParameter("ix", index);
		List<Integer> ret = q.getResultList();
		return ret;
	}
	
	@Transactional
	@Override
	public void addOrUpdateAssociates(Integer entryId, Integer assocEntryId, int index) throws Exception {
		updateTopIndex(entryId, index);
		if (assocEntryId != null) {
			TypedQuery<CommonEntry> query = em.createQuery("select ce from CommonEntry ce where ce.entryID = :entryId and ce.assocEntryID = :assocEntryId", CommonEntry.class);
			query.setParameter("entryId", entryId);
			query.setParameter("assocEntryId", assocEntryId);
			List<CommonEntry> ret = query.getResultList();
			if (ret.size() == 0) {
				CommonEntry ce = new CommonEntry();
				ce.setAssocEntryID(assocEntryId);
				ce.setEntryID(entryId);
				ce.setIndex(index);
				ce.setDescription(null);
				em.persist(ce);
			}
		}
	}

	private void updateTopIndex(Integer entryId, int index) throws Exception {
		TypedQuery<Entry> query = em.createQuery("select e from Entry e where e.id = :entryId", Entry.class);
		query.setParameter("entryId", entryId);
		List<Entry> r = query.getResultList();
		if (r.size() == 1) {
			Entry entry = r.get(0);
			entry.setTopIndex(index + 1);
		}
	}
}
