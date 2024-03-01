package com.mindskip.xzs.repository;

import com.mindskip.xzs.domain.TaskExam;
import com.mindskip.xzs.viewmodel.admin.task.TaskPageRequestVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskExamMapper extends BaseMapper<TaskExam> {

    List<TaskExam> page(TaskPageRequestVM requestVM);

    List<TaskExam> getByGradeLevel(Integer gradeLevel);
    /**
     * 根据书籍主键，查询所有的任务
     * @param relationid
     * @return
     */
    List<TaskExam> selectListByBookId(Integer relationid);
    void deleteByBookId(Integer relationid);
}
