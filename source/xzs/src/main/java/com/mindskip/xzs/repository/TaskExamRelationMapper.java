package com.mindskip.xzs.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mindskip.xzs.domain.TaskExamRelation;

@Mapper
public interface TaskExamRelationMapper extends BaseMapper<TaskExamRelation> {

    void deleteByTaskExamId(Integer taskExamId);

    int insertList(List<TaskExamRelation> list);
    
    List<TaskExamRelation> selectListByTaskExamId(Integer taskExamId);

}
