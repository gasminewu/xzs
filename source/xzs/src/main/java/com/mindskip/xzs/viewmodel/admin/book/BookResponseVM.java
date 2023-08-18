package com.mindskip.xzs.viewmodel.admin.book;

import com.mindskip.xzs.viewmodel.BaseVM;

public class BookResponseVM extends BaseVM {

	    private Integer id;
	    private Integer gradeLevel;
	    private Integer subjectId;
	    private String title;
	    private String autor;
	    
	    private Integer difficult;
	    private Integer lovel;
	    private Integer pinyin;
	    
	    private String nation;
	    private Integer buy;
	    private Integer sn;
	    private String bz;
	    private String createTime;


	    private Integer createUser;


	    private Integer status;
	    private Integer parentid;
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public Integer getGradeLevel() {
			return gradeLevel;
		}
		public void setGradeLevel(Integer gradeLevel) {
			this.gradeLevel = gradeLevel;
		}
		public Integer getSubjectId() {
			return subjectId;
		}
		public void setSubjectId(Integer subjectId) {
			this.subjectId = subjectId;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getAutor() {
			return autor;
		}
		public void setAutor(String autor) {
			this.autor = autor;
		}
		public Integer getDifficult() {
			return difficult;
		}
		public void setDifficult(Integer difficult) {
			this.difficult = difficult;
		}
		public Integer getLovel() {
			return lovel;
		}
		public void setLovel(Integer lovel) {
			this.lovel = lovel;
		}
		public Integer getPinyin() {
			return pinyin;
		}
		public void setPinyin(Integer pinyin) {
			this.pinyin = pinyin;
		}
		public String getNation() {
			return nation;
		}
		public void setNation(String nation) {
			this.nation = nation;
		}
		public Integer getBuy() {
			return buy;
		}
		public void setBuy(Integer buy) {
			this.buy = buy;
		}
		public Integer getSn() {
			return sn;
		}
		public void setSn(Integer sn) {
			this.sn = sn;
		}
		public String getBz() {
			return bz;
		}
		public void setBz(String bz) {
			this.bz = bz;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public Integer getCreateUser() {
			return createUser;
		}
		public void setCreateUser(Integer createUser) {
			this.createUser = createUser;
		}
		public Integer getStatus() {
			return status;
		}
		public void setStatus(Integer status) {
			this.status = status;
		}
		public Integer getParentid() {
			return parentid;
		}
		public void setParentid(Integer parentid) {
			this.parentid = parentid;
		}

}
