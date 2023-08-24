package com.mindskip.xzs.viewmodel.admin.task;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mindskip.xzs.viewmodel.admin.book.BookResponseVM;
import com.mindskip.xzs.viewmodel.admin.exam.ExamResponseVM;

import lombok.Data;


@Data
public class TaskRequestVM {

    private Integer id;

    @NotNull
    private Integer gradeLevel;

    @NotNull
    private String title;

    @Size(min = 0, message = "请添加试卷")
    @Valid
    private List<ExamResponseVM> paperItems;
    
    @Size(min = 0, message = "请添加书籍")
    @Valid
    private List<BookResponseVM> bookItems;
    /**
     * 任务时间
     */
    private List<String> limitDateTime;
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
     * 状态
     */
    private Integer status;
    /**
     * 任务描述
     */
    private String taskcontent;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ExamResponseVM> getPaperItems() {
        return paperItems;
    }

    public void setPaperItems(List<ExamResponseVM> paperItems) {
        this.paperItems = paperItems;
    }
}
