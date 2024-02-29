package com.mindskip.xzs.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mindskip.xzs.domain.Book;
import com.mindskip.xzs.domain.TaskExam;
import com.mindskip.xzs.domain.TextContent;
import com.mindskip.xzs.domain.User;
import com.mindskip.xzs.domain.book.BookObject;
import com.mindskip.xzs.domain.enums.BookStatusEnum;
import com.mindskip.xzs.repository.BookMapper;
import com.mindskip.xzs.repository.TaskExamMapper;
import com.mindskip.xzs.service.BookService;
import com.mindskip.xzs.service.TaskExamService;
import com.mindskip.xzs.service.TextContentService;
import com.mindskip.xzs.utility.DateTimeUtil;
import com.mindskip.xzs.utility.JsonUtil;
import com.mindskip.xzs.utility.ModelMapperSingle;
import com.mindskip.xzs.utility.poi.XLSXCovertCSVReader;
import com.mindskip.xzs.viewmodel.admin.book.BookEditRequestVM;
import com.mindskip.xzs.viewmodel.admin.book.BookPageRequestVM;
import com.mindskip.xzs.viewmodel.admin.book.BookResponseVM;
import com.mindskip.xzs.viewmodel.admin.task.TaskPageResponseVM;
import com.mindskip.xzs.viewmodel.admin.task.TaskRequestVM;

@Service
@Transactional
public class BookServiceImpl extends BaseServiceImpl<Book> implements BookService {

	protected final static ModelMapper modelMapper = ModelMapperSingle.Instance();
	private final BookMapper bookMapper;
	private final TextContentService textContentService;
	private final TaskExamMapper taskExamMapper;
	@Autowired
	private TaskExamService taskExamService;

	@Autowired
	public BookServiceImpl(BookMapper bookMapper, TextContentService textContentService,
			TaskExamMapper taskExamMapper) {
		super(bookMapper);
		this.textContentService = textContentService;
		this.bookMapper = bookMapper;
		this.taskExamMapper = taskExamMapper;
	}

	@Override
	public PageInfo<Book> page(BookPageRequestVM requestVM) {
		return PageHelper.startPage(requestVM.getPageIndex(), requestVM.getPageSize(), "sn desc")
				.doSelectPageInfo(() -> bookMapper.page(requestVM));
	}

	/**
	 * 插入或者更新
	 */
	@Override
	public Book insertFullBook(BookEditRequestVM model, Integer userId) {
		Book book = null;
		if (null == model.getId()) {
			// 新增
			book = new Book();
			book.setCreateTime(new Date());
			book.setCreateUser(userId);
			book.setDeleted(false);
			bookMapper.insertSelective(setBookInfoFromVM(book, true, model));
		} else {
			// 更新
			book = bookMapper.selectByPrimaryKey(model.getId());
			bookMapper.updateByPrimaryKeySelective(setBookInfoFromVM(book, false, model));

		}
		return book;
	}

	/**
	 * 书籍对象 ，以及作者，备注大字段内容拼接
	 */
	public Book setBookInfoFromVM(Book book, Boolean isnew, BookEditRequestVM model) {
		// 作者，备注等 插入
		TextContent infoTextContent = null;
		BookObject bookObject = new BookObject();
		bookObject.setAuthor(model.getAutor());
		bookObject.setBz(model.getBz());
		bookObject.setFinishcontent(model.getFinishcontent());
		if (isnew) {
			infoTextContent = new TextContent();
			infoTextContent.setCreateTime(new Date());
			infoTextContent.setContent(JsonUtil.toJsonStr(bookObject));
			textContentService.insertByFilter(infoTextContent);
		} else {
			infoTextContent = textContentService.selectById(book.getInfoTextContentId());
			infoTextContent.setContent(JsonUtil.toJsonStr(bookObject));
			textContentService.updateByIdFilter(infoTextContent);
		}
		book.setInfoTextContentId(infoTextContent.getId());
		book.setTitle(model.getTitle());
		book.setGradeLevel(model.getGradeLevel());
		book.setSubjectId(model.getSubjectId());
		book.setDifficult(model.getDifficult());
		book.setNation(model.getNation());
		book.setPinyin(model.getPinyin());
		book.setLovel(model.getLovel());
		book.setTaskTimeType(model.getTaskTimeType());
		book.setBuy(model.getBuy());
		book.setStatus(model.getStatus());
		book.setParentid(model.getParentid());
		if (null != model.getStatus() && model.getStatus().intValue() == BookStatusEnum.Publish.getCode()) {
			book.setFinishTime(new Date());
		}
		// 如果没有手动输入值，默认最大值
		if (null != model.getSn() && model.getSn() != 0) {
			book.setSn(model.getSn());
			// 大于等于当前值的都+1
//        	this.bookMapper.updateGTSn(model.getSn());
		} else {
			// 获取最大的顺序号
			book.setSn(this.bookMapper.selectMaxSn() + 1);
		}
		return book;
	}

	/**
	 * 根据id查询
	 */
	@Override
	public BookEditRequestVM getBookEditRequestVM(Integer bookId) {
		// 查询，并转为保存对象
		Book book = bookMapper.selectByPrimaryKey(bookId);
		BookEditRequestVM bookEditRequestVM = modelMapper.map(book, BookEditRequestVM.class);
		// 查询大字段内容
		TextContent questionInfoTextContent = textContentService.selectById(book.getInfoTextContentId());
		// 转为对象
		BookObject bookObject = JsonUtil.toJsonObject(questionInfoTextContent.getContent(), BookObject.class);
		bookEditRequestVM.setAutor(bookObject.getAuthor());
		bookEditRequestVM.setBz(bookObject.getBz());
		bookEditRequestVM.setFinishcontent(bookObject.getFinishcontent());

		List<TaskExam> rs = taskExamMapper.selectListByBookId(bookId);
		List<TaskPageResponseVM> taskItems = rs.stream().map(tk -> {
			TaskPageResponseVM taskVm = modelMapper.map(tk, TaskPageResponseVM.class);
			taskVm.setCreateTime(DateTimeUtil.dateFormat(tk.getCreateTime()));
			taskVm.setTasktimestart(DateTimeUtil.dateShortFormat(tk.getTasktimestart()));
			taskVm.setTasktimeend(DateTimeUtil.dateShortFormat(tk.getTasktimeend()));
			taskVm.setFinishtime(DateTimeUtil.dateShortFormat(tk.getFinishtime()));
			return taskVm;
		}).collect(Collectors.toList());
		bookEditRequestVM.setTaskItems(taskItems);

		return bookEditRequestVM;
	}

	/**
	 * 导入，根据title 插入或者更新
	 */
	@Override
	public void importinsertFullBook() {
		Book book = null;
		BookEditRequestVM model = null;
		try {
			List<List<String[]>> allSheets = XLSXCovertCSVReader
					.readerAllSheetExcel("C:\\Users\\Administrator\\Desktop\\xzs.xlsx");
			for (List<String[]> singleSheet : allSheets) {
				// 循环所有的sheet
				// 定义阶段
				int gradeLevel = 0;
				// 定义模块
				int subjectId = 0;
				String taskTimeType = "5";

				for (String[] rows : singleSheet) {
					model = new BookEditRequestVM();
					int i = 1;
					for (String col : rows) {
						if (i == 1 && StringUtils.isNotBlank(col)) {
							model.setTitle(col);
						}
						if (i == 2 && StringUtils.isNotBlank(col)&&NumberUtils.isParsable(col)) {
							gradeLevel = Integer.parseInt(col);
							model.setGradeLevel(gradeLevel);
						}
						if (i == 3 && StringUtils.isNotBlank(col)&&NumberUtils.isParsable(col)) {
							subjectId = Integer.parseInt(col);
							model.setSubjectId(subjectId);
						}
						if (i == 4 && StringUtils.isNotBlank(col)) {
							model.setBz(col);
						}
						if (i == 5 && StringUtils.isNotBlank(col)&&NumberUtils.isParsable(col)) {
							//难度
							model.setDifficult(Integer.parseInt(col));
						}
						if (i == 6 && StringUtils.isNotBlank(col)&&NumberUtils.isParsable(col)) {
							//状态
							model.setStatus(Integer.parseInt(col));
						}
						if (i == 7 && StringUtils.isNotBlank(col)) {
							//任务时间模型
							taskTimeType=col;
							model.setTaskTimeType(col);;
						}
						i++;
					}
					// 内容为空，直接判断下一条
					if (Objects.equals(model.getTitle(), "表头")) {
						continue;
					}
					// 如果是空，取之前的数值
					if (null == model.getGradeLevel()) {
						model.setGradeLevel(gradeLevel);
					}
					if (null == model.getSubjectId()) {
						model.setSubjectId(subjectId);
					}
					if (StringUtils.isBlank(model.getTaskTimeType())) {
						model.setTaskTimeType(taskTimeType);;
					}
					// 判断是修改还是新增(根据title判断）
					BookPageRequestVM requestVM = new BookPageRequestVM();
					requestVM.setTitle(model.getTitle());
					List<Book> books = bookMapper.page(requestVM);
					if (null != books && books.size() > 0) {
						book = books.get(0);
						model.setSn(book.getSn());
						model.setStatus(book.getStatus());
						model.setDifficult(book.getDifficult());
						bookMapper.updateByPrimaryKeySelective(setBookInfoFromVM(book, false, model));
					} else {
						book = new Book();
						book.setCreateTime(new Date());
						book.setCreateUser(0);
						book.setDeleted(false);

						model.setStatus(1);
						bookMapper.insertSelective(setBookInfoFromVM(book, true, model));
					}

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 初始化或者重装任务
	 * 
	 * @param user
	 * @param books
	 *
	 * @变更记录 2024年2月29日 上午9:19:14 武林林 创建
	 *
	 */
	public void taskInit(User user,BookPageRequestVM requestVM) {
		//要生成任务的知识点
		int i=0;
		List<Book> books=bookMapper.page(requestVM);
		for (Book book : books) {
			//开始时间结束时间
			List<List<String>> l=DateTimeUtil.listLimitDateTime6_1(book.getTaskTimeType());
			for (List<String> limitDateTime : l) {
				TaskRequestVM taskVm=new TaskRequestVM();
				taskVm.setGradeLevel(book.getGradeLevel());
				taskVm.setPriority(1);
				taskVm.setProcess(0);
				taskVm.setTaskcontent("");
				taskVm.setTasktype(1);
				taskVm.setStatus(1);
				taskVm.setSn(i);
				i++;
				
				
				//关联的知识点
				List<BookResponseVM> bookItems=new ArrayList<>();
				BookResponseVM bookVm=new BookResponseVM();
				bookVm.setId(book.getId());
				bookItems.add(bookVm);	
				taskVm.setBookItems(bookItems);
				//表头
				taskVm.setTitle(book.getTitle());
				taskVm.setLimitDateTime(limitDateTime);
				taskExamService.edit(taskVm, user);
			}
		}
		
	}
}
