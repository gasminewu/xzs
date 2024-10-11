package com.mindskip.xzs.service;

import com.mindskip.xzs.domain.TaskExam;
import com.mindskip.xzs.domain.User;
import com.mindskip.xzs.viewmodel.admin.task.TaskPageRequestVM;
import com.mindskip.xzs.viewmodel.admin.task.TaskRequestVM;
import com.github.pagehelper.PageInfo;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

public interface TaskExamService extends BaseService<TaskExam> {

    PageInfo<TaskExam> page(TaskPageRequestVM requestVM);

    void edit(TaskRequestVM model, User user);

    TaskRequestVM taskExamToVM(Integer id);

    List<TaskExam> getByGradeLevel(Integer gradeLevel);
   
    /**
     * 导出
     * @param requestVM
     * @param response
     */
    void exportExcel(TaskPageRequestVM requestVM,HttpServletResponse response);

    void updateSelectionStatus(User currentUser, List<TaskRequestVM> requestVM);
}
