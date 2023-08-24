package com.mindskip.xzs.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 任务基本信息
 *
 */
@Data
public class TaskExam implements Serializable {

    private static final long serialVersionUID = -7014704644631536195L;

    private Integer id;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 年级
     */
    private Integer gradeLevel;

    /**
     * 任务框架 内容为JSON
     */
    private Integer frameTextContentId;

    /**
     * 创建者
     */
    private Integer createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    private Boolean deleted;

    /**
     * 创建人用户名
     */
    private String createUserName;
    
    /**
     * 创建时间
     */
    private Date tasktimestart;
    /**
     * 任务时间（计划时间）开始
     */
    private Date tasktimeend;
    /**
     * 任务时间（计划时间）结束
     */
    private Date finishtime;
    /**
     * 任务状态
     */
    private Integer status;
    /**
     * 任务进度（百分比
     */
    private Integer process;
    /**
     * 优先级（普通，较高，最高）
     */
    private Integer priority;
    /**
     * 顺序号
     */
    private Integer sn;
    /**
     * 任务类别
     */
    private Integer tasktype;

    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public Integer getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(Integer gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public Integer getFrameTextContentId() {
        return frameTextContentId;
    }

    public void setFrameTextContentId(Integer frameTextContentId) {
        this.frameTextContentId = frameTextContentId;
    }

    public Integer getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Integer createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName == null ? null : createUserName.trim();
    }
}
