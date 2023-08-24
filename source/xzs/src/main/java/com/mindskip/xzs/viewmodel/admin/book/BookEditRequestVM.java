package com.mindskip.xzs.viewmodel.admin.book;


import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import com.mindskip.xzs.viewmodel.admin.task.TaskPageResponseVM;

import lombok.Data;


/**
 * 保存的对象
 *
 */
@Data
public class BookEditRequestVM {

    private Integer id;
    @NotNull
    private Integer gradeLevel;
    @NotNull
    private Integer subjectId;
    @NotBlank
    private String title;
    @NotBlank
    private String autor;
    
    @Range(min = 1, max = 5, message = "请选择题目难度")
    private Integer difficult;
    @Range(min = 1, max = 5, message = "请选择题目难度")
    private Integer lovel;
    @NotNull
    private Integer pinyin;
    
    private String nation;
    @NotNull
    private Integer buy;
    @NotNull
    private Integer status;
    private Integer sn;
    private Integer parentid;
    private String bz;
    private String finishcontent;
    
    private List<TaskPageResponseVM> taskItems;
}
