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
package com.maiereni.sling.logs.interpreter.persistence.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

/**
 * @author Petre Maierean
 *
 */
@Entity
@NamedNativeQueries({
	@NamedNativeQuery(name=EntryIndex.GET, query= EntryIndex.QUERY_STMT),
	@NamedNativeQuery(name=EntryIndex.GET_ASSOCIATED, query= EntryIndex.QUERY_ASSOCIATED),	
})
public class EntryIndex implements Serializable {
	private static final long serialVersionUID = -764615141376649895L;
	public static final String GET = "EntryIndex.Get";
	public static final String QUERY_STMT = "SELECT e.\"ENTRY_ID\", max(r.\"INDEX\") as \"INDEX\" FROM \"slingLogs\".\"Entry\" e INNER JOIN \"slingLogs\".\"Record\" r ON r.\"ENTRY_ID\" = e.\"ENTRY_ID\" GROUP BY e.\"ENTRY_ID\" ORDER BY e.\"ENTRY_ID\" asc";
	public static final String GET_ASSOCIATED = "EntryIndex.GetAssociated";
	public static final String QUERY_ASSOCIATED = "SELECT ri.\"ENTRY_ID\" FROM \"slingLogs\".\"Record\" r INNER JOIN \"slingLogs\".\"Record\" ri ON ri.\"CLASS_NAME\" = r.\"CLASS_NAME\" AND ri.\"INDEX\" = r.\"INDEX\" AND ri.\"LINE_NUMBER\" = r.\"LINE_NUMBER\" WHERE r.\"ENTRY_ID\" = :entityId AND ri.\"INDEX\" = :ix";
	
	@Id
	@Column(name="\"ENTRY_ID\"")
	private Integer id;
	@Column(name="\"INDEX\"")
	private Integer maxIndex;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getMaxIndex() {
		return maxIndex;
	}
	public void setMaxIndex(Integer maxIndex) {
		this.maxIndex = maxIndex;
	}
}
