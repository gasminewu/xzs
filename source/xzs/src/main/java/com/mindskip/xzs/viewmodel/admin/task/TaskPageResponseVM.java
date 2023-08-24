package com.mindskip.xzs.viewmodel.admin.task;



import lombok.Data;


@Data
public class TaskPageResponseVM {

    private Integer id;

    private String title;

    private Integer gradeLevel;

    private String createUserName;

    private String createTime;

    private Boolean deleted;
    /**
     * 创建时间
     */
    private String tasktimestart;
    /**
     * 任务时间（计划时间）开始
     */
    private String tasktimeend;
    /**
     * 任务时间（计划时间）结束
     */
    private String finishtime;
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
    
    /**
     * 时间计算，剩余n天，超期n天 
     */
    private String time;
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
        this.title = title;
    }

    public Integer getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(Integer gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
