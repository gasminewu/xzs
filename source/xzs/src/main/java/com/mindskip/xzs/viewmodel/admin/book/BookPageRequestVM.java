package com.mindskip.xzs.viewmodel.admin.book;

import com.mindskip.xzs.base.BasePage;



public class BookPageRequestVM extends BasePage {
	private Integer id;
	private Integer level;
	private Integer subjectId;
	private Integer pinyin;
	private Integer status;
	private Integer priority;
	private String title;
	/**
	 * 任务重置
	 */
	private String taskFresh;
	
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}
	public Integer getPinyin() {
		return pinyin;
	}
	public void setPinyin(Integer pinyin) {
		this.pinyin = pinyin;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getTaskFresh() {
		return taskFresh;
	}
	public void setTaskFresh(String taskFresh) {
		this.taskFresh = taskFresh;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

  
}
