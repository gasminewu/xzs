package com.mindskip.xzs.viewmodel.admin.book;

import com.mindskip.xzs.base.BasePage;



public class BookPageRequestVM extends BasePage {
	private Integer id;
	private Integer level;
	private Integer subjectId;
	private Integer pinyin;
	private Integer status;
	private String title;
	
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

  
}
