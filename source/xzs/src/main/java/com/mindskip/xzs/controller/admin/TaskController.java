package com.mindskip.xzs.controller.admin;


import com.mindskip.xzs.base.BaseApiController;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.domain.TaskExam;
import com.mindskip.xzs.service.TaskExamService;
import com.mindskip.xzs.utility.DateTimeUtil;
import com.mindskip.xzs.utility.PageInfoHelper;
import com.mindskip.xzs.viewmodel.admin.book.BookPageRequestVM;
import com.mindskip.xzs.viewmodel.admin.task.TaskPageRequestVM;
import com.mindskip.xzs.viewmodel.admin.task.TaskPageResponseVM;
import com.mindskip.xzs.viewmodel.admin.task.TaskRequestVM;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController("AdminTaskController")
@RequestMapping(value = "/api/admin/task")
public class TaskController extends BaseApiController {

    private final TaskExamService taskExamService;

    @Autowired
    public TaskController(TaskExamService taskExamService) {
        this.taskExamService = taskExamService;
    }

    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public RestResponse<PageInfo<TaskPageResponseVM>> pageList(@RequestBody TaskPageRequestVM model) {
        PageInfo<TaskExam> pageInfo = taskExamService.page(model);
        PageInfo<TaskPageResponseVM> page = PageInfoHelper.copyMap(pageInfo, m -> {
            TaskPageResponseVM vm = modelMapper.map(m, TaskPageResponseVM.class);
            vm.setCreateTime(DateTimeUtil.dateShortFormat(m.getCreateTime()));
            vm.setTasktimestart(DateTimeUtil.dateShortFormat(m.getTasktimestart()));
            vm.setTasktimeend(DateTimeUtil.dateShortFormat(m.getTasktimeend()));
            vm.setFinishtime(DateTimeUtil.dateShortFormat(m.getFinishtime()));
            
            String now=DateTimeUtil.dateShortFormat(new Date());
            int kn=DateTimeUtil.DayBettew(now,DateTimeUtil.dateShortFormat(m.getTasktimestart()));
            int en=DateTimeUtil.DayBettew(now,DateTimeUtil.dateShortFormat(m.getTasktimeend()));
            if(kn>0) {
            	vm.setTime("距离"+kn+"天");
            }else if(en>=0) {
            	vm.setTime("剩余"+en+"天");
            }else {
            	vm.setTime("超期"+(-en)+"天");
            }
            return vm;
        });
        return RestResponse.ok(page);
    }
    
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public RestResponse edit(@RequestBody @Valid TaskRequestVM model) {
        taskExamService.edit(model, getCurrentUser());
        TaskRequestVM vm = taskExamService.taskExamToVM(model.getId());
        return RestResponse.ok(vm);
    }


    @RequestMapping(value = "/select/{id}", method = RequestMethod.POST)
    public RestResponse<TaskRequestVM> select(@PathVariable Integer id) {
        TaskRequestVM vm = taskExamService.taskExamToVM(id);
        return RestResponse.ok(vm);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public RestResponse delete(@PathVariable Integer id) {
        TaskExam taskExam = taskExamService.selectById(id);
        taskExam.setDeleted(true);
        taskExamService.updateByIdFilter(taskExam);
        return RestResponse.ok();
    }
    /**
     * 导出附件
     * @param model
     * @param response
     * @return
     */
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public RestResponse export(@RequestBody TaskPageRequestVM model,HttpServletResponse response) {
        taskExamService.exportExcel(model,response);
        return RestResponse.ok();
    }
    /**
     * 任务批量修改状态
     * @return
     */
    @RequestMapping(value = "/updateSelectionStatus", method = RequestMethod.POST)
    public RestResponse<?> updateSelectionStatus(@RequestBody List<TaskRequestVM> requestVM) {
        taskExamService.updateSelectionStatus(getCurrentUser(),requestVM);
        return RestResponse.ok();
    }
}
