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
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Petre Maierean
 *
 */
@Entity
@Table(name="\"Entry\"", schema="\"slingLogs\"")
public class Entry implements Serializable {
	private static final long serialVersionUID = 5641404616482666315L;
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="\"ENTRY_ID\"")
	private Integer id;
	@Column(name="\"LINE_NUMBER\"")
	private Integer lineNumber;
	@Column(name="\"THREAD_NAME\"")
	private String threadName;
	@OneToMany(mappedBy = "entry", cascade = CascadeType.ALL)
	private Set<Record> records;
	@Column(name="\"TOP_INDEX\"")
	private Integer topIndex;
	@Column(name="\"DESCRIPTION\"")
	private String description;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Set<Record> getRecords() {
		return records;
	}
	public void setRecords(Set<Record> records) {
		this.records = records;
	}
	public String getThreadName() {
		return threadName;
	}
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	public Integer getTopIndex() {
		return topIndex;
	}
	public void setTopIndex(Integer topIndex) {
		this.topIndex = topIndex;
	}
	public Integer getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
