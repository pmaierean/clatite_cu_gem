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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Petre Maierean
 *
 */
@Entity
@Table(name="\"CommonEntries\"", schema="\"slingLogs\"")
public class CommonEntry implements Serializable {
	private static final long serialVersionUID = -3891536505917492731L;
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="\"COMMON_ID\"")
	private Integer id;
	@Column(name="\"DESCRIPTION\"")
	private String description;
	@Column(name="\"ENTRY_ID\"")
	private Integer entryID;
	@Column(name="\"ASSOC_ENTRY_ID\"")
	private Integer assocEntryID;
	@Column(name="\"INDEX\"")
	private Integer index;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getEntryID() {
		return entryID;
	}
	public void setEntryID(Integer entryID) {
		this.entryID = entryID;
	}
	public Integer getAssocEntryID() {
		return assocEntryID;
	}
	public void setAssocEntryID(Integer assocEntryID) {
		this.assocEntryID = assocEntryID;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
}
