package com.mindskip.xzs.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 书籍 绘本等
 *
 */
@Data
public class Book implements Serializable {

    private static final long serialVersionUID = 8826266720383164363L;

    private Integer id;
   
    /**
     * 阶段(0-3岁，3-6岁，小学，一年级）
     */
    private Integer gradeLevel;
    /**
     * 模块
     */
    private Integer subjectId;

    /**
     * 标题
     */
    private String title;
    /**
     * 难度
     */
    private Integer difficult;

    /**
     * 喜爱程度
     */
    private Integer lovel;
    /**
     * 拼音
     */
    private Integer pinyin;
    /**
     * 国家
     */
    private String nation;
   

    /**
     * 题目 填空、 题干、解析、答案等信息
     */
    private Integer infoTextContentId;
    /**
     * 1.正常，2.归档，3.计划购买
     */
    private Integer status;
    /**
     * 拥有方式1.购买，2借阅
     */
    private Integer buy;
    
    /**
     * 空 单本，0套装
     */
    private  Integer parentid;
    /**
     * 顺序号
     */
    private Integer sn;
    /**
     * 创建人
     */
    private Integer createUser;

   

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 归档时间
     */
    private Date finishTime;

    private Boolean deleted;
    
    /**
     * 任务的时间（开始时间或者结束时间）
     */
    private String taskTimeType;
    /**
     * 进度
     */
    private Integer priority;
}
