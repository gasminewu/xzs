package com.mindskip.xzs.domain;

import java.io.Serializable;

import lombok.Data;

/**
 * 任务关联的对象，
 *
 */
@Data
public class TaskExamRelation implements Serializable {

    private static final long serialVersionUID = -7014704644631536195L;

    private Integer id;
    /**
     * 任务Id
     */
    private Integer taskExamId;
    /**
     * 关联对象主键
     */
    private Integer relationid;

   
}
