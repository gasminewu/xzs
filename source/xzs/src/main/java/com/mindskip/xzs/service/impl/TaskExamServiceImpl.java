package com.mindskip.xzs.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mindskip.xzs.domain.Book;
import com.mindskip.xzs.domain.ExamPaper;
import com.mindskip.xzs.domain.Subject;
import com.mindskip.xzs.domain.TaskExam;
import com.mindskip.xzs.domain.TaskExamRelation;
import com.mindskip.xzs.domain.TextContent;
import com.mindskip.xzs.domain.User;
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
import com.mindskip.xzs.utility.DateTimeUtil;
import com.mindskip.xzs.utility.HtmlUtil;
import com.mindskip.xzs.utility.JsonUtil;
import com.mindskip.xzs.utility.ModelMapperSingle;
import com.mindskip.xzs.utility.excelMulti.Column;
import com.mindskip.xzs.utility.excelMulti.ExcelTool;
import com.mindskip.xzs.viewmodel.admin.book.BookResponseVM;
import com.mindskip.xzs.viewmodel.admin.exam.ExamResponseVM;
import com.mindskip.xzs.viewmodel.admin.task.TaskExportVM;
import com.mindskip.xzs.viewmodel.admin.task.TaskPageRequestVM;
import com.mindskip.xzs.viewmodel.admin.task.TaskRequestVM;

@Service
public class TaskExamServiceImpl extends BaseServiceImpl<TaskExam> implements TaskExamService {

	protected final static ModelMapper modelMapper = ModelMapperSingle.Instance();
	private final TaskExamMapper taskExamMapper;
	private final TextContentService textContentService;
	private final ExamPaperMapper examPaperMapper;
	private final BookMapper bookMapper;
	private final TaskExamRelationMapper taskExamRelationMapper;
	
	@Autowired
	private  SubjectService subjectService;

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
		return PageHelper.startPage(requestVM.getPageIndex(), requestVM.getPageSize(), "  tasktimestart desc,sn asc")
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
			taskExam.setTasktimestart(DateTimeUtil.parse(dateTimes.get(0), DateTimeUtil.STANDER_FORMAT));
			taskExam.setTasktimeend(DateTimeUtil.parse(dateTimes.get(1), DateTimeUtil.STANDER_FORMAT));
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
			taskExam.setTasktimestart(DateTimeUtil.parse(dateTimes.get(0), DateTimeUtil.STANDER_FORMAT));
			taskExam.setTasktimeend(DateTimeUtil.parse(dateTimes.get(1), DateTimeUtil.STANDER_FORMAT));
			if (taskExam.getStatus().intValue() == 2) {
				taskExam.setFinishtime(now);
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
				//查询大字段内容
		    	TextContent questionInfoTextContent = textContentService.selectById(tk.getInfoTextContentId());
		    	//转为对象
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

	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	@Override
	public void exportExcel(TaskPageRequestVM requestVM, HttpServletResponse response) {
		// 条件：需要导出的表头信息
		// 表头封装（采用树结构,使用pid判断是否有子集）
		List<Map<String, String>> titleList = new ArrayList<Map<String, String>>() {
			{
				add(new HashMap<String, String>() {
					{
						put("id", "4");
						put("content", "时间");
						put("fielName", "taskms");
						put("pid", "0");
					}
				});
				add(new HashMap<String, String>() {
					{
						put("id", "5");
						put("content", "模块");
						put("fielName", "mk");
						put("pid", "0");
					}
				});
				add(new HashMap<String, String>() {
					{
						put("id", "1");
						put("content", "序号");
						put("fielName", "sn");
						put("pid", "0");
					}
				});
				add(new HashMap<String, String>() {
					{
						put("id", "2");
						put("content", "任务");
						put("fielName", "taskxx");
						put("pid", "0");
					}
				});
				add(new HashMap<String, String>() {
					{
						put("id", "3");
						put("content", "是否完成");
						put("fielName", "sfwc");
						put("pid", "0");
					}
				});
			}
		};
		String sfwc = "[   ]   [   ]   [   ]";

		// 查询要导出的数据
		List<TaskExam> tasks = taskExamMapper.page(requestVM);
		List<TaskExportVM> list = new LinkedList<>();
		for (TaskExam taskExam : tasks) {
			//当前任务描述
			TaskExportVM vm = new TaskExportVM();
			String tasktimestart= DateTimeUtil.dateFormatMD(taskExam.getTasktimestart());
			String taskms ="第"+DateTimeUtil.getWeek(taskExam.getTasktimestart())+"周" + " [" + tasktimestart + "-"
					+ DateTimeUtil.dateFormatMD(taskExam.getTasktimeend())+" ]";
			//任务详细
			TaskRequestVM taskRequestVM = this.taskExamToVM(taskExam.getId());
			if (taskExam.getTasktype().intValue() == 0) {
				vm.setTaskms(taskms);
				vm.setMk(retLevel(taskExam.getGradeLevel()));
				vm.setSn(taskExam.getSn());
				vm.setTaskxx(taskExam.getTitle());
				vm.setSfwc(sfwc);
				list.add(vm);
			} else if (taskExam.getTasktype().intValue() == 3) {
				//试卷

			}
			else if (taskExam.getTasktype().intValue() == 1) {
				// 知识点查询
				List<BookResponseVM> books = taskRequestVM.getBookItems();
				int i = 0;
				for (BookResponseVM book : books) {
					i++;
					vm = new TaskExportVM();
					vm.setTaskms(taskms);
					vm.setMk(retMk(book.getSubjectId()));
					vm.setSn(i);
					 String clearHtml = HtmlUtil.clear(book.getBz());
					vm.setTaskxx(book.getTitle()+" ;"+clearHtml);
					vm.setSfwc(sfwc);
					list.add(vm);
					continue;

				}
			}
		}

		try {
			ExcelTool excelTool = new ExcelTool("rewr", 30, 20, DateTimeUtil.STANDER_SHORT_FORMAT);
			List<Column> titleData = excelTool.columnTransformer(titleList, "id", "pid", "content", "fielName", "0");
			excelTool.exportExcel(titleData, list, "E:\\04临时文件\\" + DateTimeUtil.dateFormatFull(new Date()) + ".xlsx",
					true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 返回模块详细
	 * 
	 * @param subjectId
	 * @return
	 *
	 * @变更记录 2024年2月27日 下午5:00:29 武林林 创建
	 *
	 */
	private String retLevel(Integer gradlevel) {
		HashMap<Integer, String > ret  = new HashMap<Integer, String>(){{  
			put(100,"运动");
			put(200,"语文");
			put(300,"英语");
			put(400,"数学");
		}}; 
    	return ret.get(gradlevel);
	}
	
	/**
	 * 返回模块详细
	 * 
	 * @param subjectId
	 * @return
	 *
	 * @变更记录 2024年2月27日 下午5:00:29 武林林 创建
	 *
	 */
	private String retMk(Integer subjectId) {
		Subject subject = subjectService.selectById(subjectId);
		return subject.getLevelName()+"-"+subject.getName();
	}
}
