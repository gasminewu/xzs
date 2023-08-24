package com.mindskip.xzs.viewmodel.admin.task;

import java.util.Date;

import com.mindskip.xzs.base.BasePage;



public class TaskPageRequestVM extends BasePage {
    private Integer gradeLevel;
    private Integer status;
    /**
     * 优先级（普通，较高，最高）
     */
    private Integer priority;
    /**
     * 任务类别
     */
    private Integer tasktype;
    private Integer timetype;
    private Date nowTime;

    public Integer getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(Integer gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getTasktype() {
		return tasktype;
	}

	public void setTasktype(Integer tasktype) {
		this.tasktype = tasktype;
	}

	public Integer getTimetype() {
		return timetype;
	}

	public void setTimetype(Integer timetype) {
		this.timetype = timetype;
	}

	public Date getNowTime() {
		return new Date();
	}

	public void setNowTime(Date nowTime) {
		this.nowTime = nowTime;
	}
	
}
