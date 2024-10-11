package com.mindskip.xzs.service.impl;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.*;
import com.deepoove.poi.data.style.BorderStyle;
import com.deepoove.poi.policy.AttachmentRenderPolicy;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mindskip.xzs.domain.*;
import com.mindskip.xzs.domain.book.BookObject;
import com.mindskip.xzs.domain.task.TaskItemObject;
import com.mindskip.xzs.domain.task.TaskObject;
import com.mindskip.xzs.repository.BookMapper;
import com.mindskip.xzs.repository.ExamPaperMapper;
import com.mindskip.xzs.repository.TaskExamMapper;
import com.mindskip.xzs.repository.TaskExamRelationMapper;
import com.mindskip.xzs.service.SubjectService;
import com.mindskip.xzs.service.TaskExamService;
import com.mindskip.xzs.service.TextContentService;
import com.mindskip.xzs.service.enums.ActionEnum;
import com.mindskip.xzs.utility.*;
import com.mindskip.xzs.utility.excelMulti.Column;
import com.mindskip.xzs.utility.excelMulti.ExcelTool;
import com.mindskip.xzs.utility.poi.*;
import com.mindskip.xzs.viewmodel.admin.book.BookPageRequestVM;
import com.mindskip.xzs.viewmodel.admin.book.BookResponseVM;
import com.mindskip.xzs.viewmodel.admin.exam.ExamResponseVM;
import com.mindskip.xzs.viewmodel.admin.task.TaskExportVM;
import com.mindskip.xzs.viewmodel.admin.task.TaskPageRequestVM;
import com.mindskip.xzs.viewmodel.admin.task.TaskRequestVM;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskExamServiceImpl extends BaseServiceImpl<TaskExam> implements TaskExamService {

    protected final static ModelMapper modelMapper = ModelMapperSingle.Instance();
    private final TaskExamMapper taskExamMapper;
    private final TextContentService textContentService;
    private final ExamPaperMapper examPaperMapper;
    private final BookMapper bookMapper;
    private final TaskExamRelationMapper taskExamRelationMapper;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    public TaskExamServiceImpl(TaskExamMapper taskExamMapper, TextContentService textContentService,
                               ExamPaperMapper examPaperMapper, BookMapper bookMapper, TaskExamRelationMapper taskExamRelationMapper) {
        super(taskExamMapper);
        this.taskExamMapper = taskExamMapper;
        this.textContentService = textContentService;
        this.examPaperMapper = examPaperMapper;
        this.bookMapper = bookMapper;
        this.taskExamRelationMapper = taskExamRelationMapper;
    }

    @Override
    public PageInfo<TaskExam> page(TaskPageRequestVM requestVM) {
        return PageHelper.startPage(requestVM.getPageIndex(), requestVM.getPageSize(), "grade_level asc,tasktimestart asc,sn asc")
                .doSelectPageInfo(() -> taskExamMapper.page(requestVM));
    }

    @Override
    @Transactional
    public void edit(TaskRequestVM model, User user) {
        ActionEnum actionEnum = (model.getId() == null) ? ActionEnum.ADD : ActionEnum.UPDATE;
        TaskExam taskExam = null;
        Date now = new Date();
        if (actionEnum == ActionEnum.ADD) {
            taskExam = modelMapper.map(model, TaskExam.class);
            taskExam.setCreateUser(user.getId());
            taskExam.setCreateUserName(user.getUserName());
            taskExam.setCreateTime(now);
            taskExam.setDeleted(false);
            taskExam.setStatus(1);

            // 开始结束时间处理
            List<String> dateTimes = model.getLimitDateTime();
            taskExam.setTasktimestart(DateTimeUtil.parse(dateTimes.get(0), DateTimeUtil.STANDER_SHORT_FORMAT));
            taskExam.setTasktimeend(DateTimeUtil.parse(dateTimes.get(1), DateTimeUtil.STANDER_SHORT_FORMAT));
            if (taskExam.getStatus().intValue() == 2) {
                taskExam.setFinishtime(now);
            }
            TaskObject taskObject = new TaskObject();
            TextContent textContent = new TextContent();
            // 保存任务结构
            if (model.getTasktype().intValue() == 3) {
                // 试卷
                textContent = textContentService.jsonConvertInsert(model.getPaperItems(), now, p -> {
                    TaskItemObject taskItemObject = new TaskItemObject();
                    taskItemObject.setExamPaperId(p.getId());
                    taskItemObject.setExamPaperName(p.getName());
                    return taskItemObject;
                });
                taskObject.setPapers(textContent.getContent());
            } else {
                taskObject.setPapers("");
            }
            taskObject.setTaskcontent(model.getTaskcontent());
            textContent.setContent(JsonUtil.toJsonStr(taskObject));
            textContentService.insertByFilter(textContent);
            taskExam.setFrameTextContentId(textContent.getId());
            taskExamMapper.insertSelective(taskExam);

        } else {

            taskExam = taskExamMapper.selectByPrimaryKey(model.getId());
            modelMapper.map(model, taskExam);
            // 开始结束时间处理
            List<String> dateTimes = model.getLimitDateTime();
            taskExam.setTasktimestart(DateTimeUtil.parse(dateTimes.get(0), DateTimeUtil.STANDER_SHORT_FORMAT));
            taskExam.setTasktimeend(DateTimeUtil.parse(dateTimes.get(1), DateTimeUtil.STANDER_SHORT_FORMAT));
            if (taskExam.getStatus().intValue() == 3) {
                taskExam.setFinishtime(now);
                // 把所关联的知识点，进度修改
            }
            TextContent textContent = textContentService.selectById(taskExam.getFrameTextContentId());
            // 清空试卷任务的试卷Id，后面会统一设置
//            List<Integer> paperIds = JsonUtil.toJsonListObject(textContent.getContent(), TaskItemObject.class)
//                    .stream()
//                    .map(d -> d.getExamPaperId())
//                    .collect(Collectors.toList());
//            examPaperMapper.clearTaskPaper(paperIds);

            TaskObject taskObject = new TaskObject();
            // 保存任务结构
            if (model.getTasktype().intValue() == 3) {
                // 试卷
                // 更新任务结构
                textContentService.jsonConvertUpdate(textContent, model.getPaperItems(), p -> {
                    TaskItemObject taskItemObject = new TaskItemObject();
                    taskItemObject.setExamPaperId(p.getId());
                    taskItemObject.setExamPaperName(p.getName());
                    return taskItemObject;
                });
                taskObject.setPapers(textContent.getContent());
            } else {
                taskObject.setPapers("");
            }
            taskObject.setTaskcontent(model.getTaskcontent());
            textContent.setContent(JsonUtil.toJsonStr(taskObject));
            textContentService.updateByIdFilter(textContent);
            taskExamMapper.updateByPrimaryKeySelective(taskExam);
        }
        Integer taskExamId = taskExam.getId();
        // 删除关联表
        taskExamRelationMapper.deleteByTaskExamId(taskExamId);
        if (model.getTasktype().intValue() == 1) {
            // 书籍,保存到关联对象表
            List<TaskExamRelation> rs = model.getBookItems().stream().map(s -> {
                TaskExamRelation r = new TaskExamRelation();
                r.setRelationid(s.getId());
                r.setTaskExamId(taskExamId);
                return r;
            }).collect(Collectors.toList());

            taskExamRelationMapper.insertList(rs);
        }

        // 更新试卷的taskId
//        List<Integer> paperIds = model.getPaperItems().stream().map(d -> d.getId()).collect(Collectors.toList());
//        examPaperMapper.updateTaskPaper(taskExam.getId(), paperIds);
        model.setId(taskExam.getId());
    }

    @Override
    public TaskRequestVM taskExamToVM(Integer id) {
        TaskExam taskExam = taskExamMapper.selectByPrimaryKey(id);
        TaskRequestVM vm = modelMapper.map(taskExam, TaskRequestVM.class);
        TextContent textContent = textContentService.selectById(taskExam.getFrameTextContentId());
        // 转为对象
        TaskObject taskObject = JsonUtil.toJsonObject(textContent.getContent(), TaskObject.class);
        // 试卷
        if (vm.getTasktype().intValue() == 3) {
            List<ExamResponseVM> examResponseVMS = JsonUtil
                    .toJsonListObject(taskObject.getPapers(), TaskItemObject.class).stream().map(tk -> {
                        ExamPaper examPaper = examPaperMapper.selectByPrimaryKey(tk.getExamPaperId());
                        ExamResponseVM examResponseVM = modelMapper.map(examPaper, ExamResponseVM.class);
                        examResponseVM.setCreateTime(DateTimeUtil.dateFormat(examPaper.getCreateTime()));
                        return examResponseVM;
                    }).collect(Collectors.toList());
            vm.setPaperItems(examResponseVMS);
        }
        // 书籍查询
        if (vm.getTasktype().intValue() == 1) {
            List<Book> rs = bookMapper.selectListByTaskExamId(id);
            List<BookResponseVM> bookItems = rs.stream().map(tk -> {
                BookResponseVM bookVm = modelMapper.map(tk, BookResponseVM.class);
                bookVm.setCreateTime(DateTimeUtil.dateFormat(tk.getCreateTime()));
                // 查询大字段内容
                TextContent questionInfoTextContent = textContentService.selectById(tk.getInfoTextContentId());
                // 转为对象
                BookObject bookObject = JsonUtil.toJsonObject(questionInfoTextContent.getContent(), BookObject.class);
                bookVm.setBz(bookObject.getBz());
                return bookVm;
            }).collect(Collectors.toList());
            vm.setBookItems(bookItems);
        }
        List<String> limitDateTime = Arrays.asList(DateTimeUtil.dateFormat(taskExam.getTasktimestart()),
                DateTimeUtil.dateFormat(taskExam.getTasktimeend()));
        vm.setLimitDateTime(limitDateTime);
        vm.setTaskcontent(taskObject.getTaskcontent());
        return vm;
    }

    @Override
    public List<TaskExam> getByGradeLevel(Integer gradeLevel) {
        return taskExamMapper.getByGradeLevel(gradeLevel);
    }

    @Override
    public void exportExcel(TaskPageRequestVM requestVM, HttpServletResponse response) {
        // 模板文件
        String templateFilePath = "/word_template/tasktemplate.docx";
		// 输出文件
        String outputFilePath = "E:\\04临时文件\\" + DateTimeUtil.dateFormatFull(new Date()) + ".docx";

        WordDO worDo = new WordDO();
        worDo.setHeader(DateTimeUtil.dateShortFormat(new Date()));
        // 查询要导出的数据
        List<TaskExam> tasks = taskExamMapper.page(requestVM);
        //行数据
        List<RowRenderData> goods = new ArrayList<>();
        Map<Integer,List<Integer>> mapMerge=new HashMap<>(5);
        Map<String,String> mapbooks=new LinkedMap<>(10);
        List<Integer> mergelist = new ArrayList<>();
        int  gradLvevel=tasks.get(0).getGradeLevel();
        int i = 0;
        for (TaskExam taskExam : tasks) {
            // 当前任务描述
            String tasktimestart = DateTimeUtil.dateShortFormat(taskExam.getTasktimestart());
            String taskms = tasktimestart + "-"
                    + DateTimeUtil.dateShortFormat(taskExam.getTasktimeend());
            // String taskms ="第"+DateTimeUtil.getWeek(taskExam.getTasktimestart())+"周" + " [" + tasktimestart + "-"
            // 		+ DateTimeUtil.dateFormatMD(taskExam.getTasktimeend())+" ]";
            // 任务详细
            TaskRequestVM taskRequestVM = this.taskExamToVM(taskExam.getId());
            if (taskExam.getTasktype().intValue() == 0) {
                int newgradLvevel=taskExam.getGradeLevel();
                RowRenderData good = Rows.of( retLevel(taskExam.getGradeLevel()),taskExam.getTitle(), "□ □", "").center().create();
                goods.add(good);
                if(gradLvevel!=newgradLvevel){
                    mergelist.add(goods.size()-1);
                }
            } else if (taskExam.getTasktype().intValue() == 3) {
                // 试卷

            } else if (taskExam.getTasktype().intValue() == 1) {
                // 知识点查询
                List<BookResponseVM> books = taskRequestVM.getBookItems();
                for (BookResponseVM book : books) {
                    // 模块点
                    StringBuilder taskxx= new StringBuilder("  ");
                    String subName=tasktimestart+"、"+retMk(book.getSubjectId()).getName();
                    i++;
                    if (StringUtils.isBlank(book.getBz())) {
                        taskxx.append(book.getTitle()).append("  ");
                    } else {
                        String clearHtml = HtmlUtil.clear(book.getBz());
                        taskxx.append(book.getTitle()).append("   ").append(clearHtml).append("  ");;
                    }
                    if(mapbooks.containsKey(subName)){
                        mapbooks.put(subName,taskxx.append(mapbooks.get(subName)).toString());
                    }else{
                        mapbooks.put(subName,taskxx.toString());
                    }

                }


            }
        }
        mapbooks.entrySet().forEach(entry -> {
            RowRenderData good = Rows.of(entry.getKey(),entry.getValue(), "□ □", "").center().create();
            goods.add(good);
        });

        mergelist.add(goods.size());
        mapMerge.put(0,mergelist);
        mapMerge.put(3,mergelist);
        /**
         * 第一个表格
         */
        UserApplicationTableDO detailTable = new UserApplicationTableDO();
        detailTable.setLabors(goods);
        worDo.setUserApplicationTableDO(detailTable);

        Configure config = Configure.builder()
                .bind("detail_table", new DetailTablePolicyMerge(4,null))
                .build();

        //生成附件
        PoitlUtils.generateWordFile(worDo, templateFilePath, outputFilePath,config);
    }

    @Override
    public void updateSelectionStatus(User currentUser, List<TaskRequestVM> requestVM) {
        List<Integer> bookIds = requestVM.stream().map(TaskRequestVM::getId).collect(Collectors.toList());
        this.taskExamMapper.updateStatus(bookIds,requestVM.get(0).getStatus());
    }

    /**
     * 返回模块详细
     *
     * @param subjectId
     * @return
     * @变更记录 2024年2月27日 下午5:00:29 武林林 创建
     */
    private String retLevel(Integer gradlevel) {
        HashMap<Integer, String> ret = new HashMap<Integer, String>() {{
            put(100, "运动");
            put(200, "语文");
            put(300, "英语");
            put(400, "数学");
        }};
        return ret.get(gradlevel);
    }

    /**
     * 返回模块详细
     *
     * @param subjectId
     * @return
     * @变更记录 2024年2月27日 下午5:00:29 武林林 创建
     */
    private Subject  retMk(Integer subjectId) {
        return subjectService.selectById(subjectId);
    }
}
