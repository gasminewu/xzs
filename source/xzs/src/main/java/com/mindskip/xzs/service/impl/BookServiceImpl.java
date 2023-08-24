package com.mindskip.xzs.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mindskip.xzs.domain.Book;
import com.mindskip.xzs.domain.TaskExam;
import com.mindskip.xzs.domain.TextContent;
import com.mindskip.xzs.domain.book.BookObject;
import com.mindskip.xzs.domain.enums.BookStatusEnum;
import com.mindskip.xzs.repository.BookMapper;
import com.mindskip.xzs.repository.TaskExamMapper;
import com.mindskip.xzs.service.BookService;
import com.mindskip.xzs.service.TextContentService;
import com.mindskip.xzs.utility.DateTimeUtil;
import com.mindskip.xzs.utility.JsonUtil;
import com.mindskip.xzs.utility.ModelMapperSingle;
import com.mindskip.xzs.viewmodel.admin.book.BookEditRequestVM;
import com.mindskip.xzs.viewmodel.admin.book.BookPageRequestVM;
import com.mindskip.xzs.viewmodel.admin.book.BookResponseVM;
import com.mindskip.xzs.viewmodel.admin.task.TaskPageResponseVM;

@Service
@Transactional
public class BookServiceImpl extends BaseServiceImpl<Book> implements BookService {

    protected final static ModelMapper modelMapper = ModelMapperSingle.Instance();
    private final BookMapper bookMapper;
    private final TextContentService textContentService;
    private final TaskExamMapper taskExamMapper;

    @Autowired
    public BookServiceImpl(BookMapper bookMapper, TextContentService textContentService,TaskExamMapper taskExamMapper) {
        super(bookMapper);
        this.textContentService = textContentService;
        this.bookMapper = bookMapper;
		this.taskExamMapper = taskExamMapper;
    }

    @Override
    public PageInfo<Book> page(BookPageRequestVM requestVM) {
        return PageHelper.startPage(requestVM.getPageIndex(), requestVM.getPageSize(), "sn desc").doSelectPageInfo(() ->
                bookMapper.page(requestVM)
        );
    }


    /**
     * 插入或者更新
     */
    @Override
    public Book insertFullBook(BookEditRequestVM model, Integer userId) {
    	Book book=null;
    	if (null == model.getId()) {
    		//新增
    		book = new Book();
            book.setCreateTime(new Date());
            book.setCreateUser(userId);
            book.setDeleted(false);
            bookMapper.insertSelective(setBookInfoFromVM(book, true, model));
         } else {
        	//更新
    		book = bookMapper.selectByPrimaryKey(model.getId());
            bookMapper.updateByPrimaryKeySelective(setBookInfoFromVM(book, false, model));
            
         }
    	return book;
    }
    /**
     * 书籍对象 ，以及作者，备注大字段内容拼接
     */
	public Book setBookInfoFromVM(Book book, Boolean isnew, BookEditRequestVM model) {
    	//作者，备注等 插入
    	TextContent infoTextContent=null;
    	BookObject bookObject = new BookObject();
    	bookObject.setAuthor(model.getAutor());
    	bookObject.setBz(model.getBz());
    	bookObject.setFinishcontent(model.getFinishcontent());
    	if(isnew) {
    		infoTextContent=new TextContent();
    		infoTextContent.setCreateTime(new Date());
    		infoTextContent.setContent(JsonUtil.toJsonStr(bookObject));
    		textContentService.insertByFilter(infoTextContent);
    	}else {
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
        book.setBuy(model.getBuy());
        book.setStatus(model.getStatus());
        book.setParentid(model.getParentid());
        if(null!=model.getStatus()&& model.getStatus().intValue()==BookStatusEnum.Publish.getCode()) {
        	book.setFinishTime(new Date());
        }
        //如果没有手动输入值，默认最大值
        if(null!=model.getSn()) {
        	book.setSn(model.getSn());
        	//大于等于当前值的都+1
        	this.bookMapper.updateGTSn(model.getSn());
        }else {
        	//获取最大的顺序号
        	book.setSn(this.bookMapper.selectMaxSn()+1);
        }
        return book;
    }
    /**
     * 根据id查询
     */
    @Override
    public BookEditRequestVM getBookEditRequestVM(Integer bookId) {
    	//查询，并转为保存对象
    	Book book = bookMapper.selectByPrimaryKey(bookId);
    	BookEditRequestVM bookEditRequestVM = modelMapper.map(book, BookEditRequestVM.class);
    	//查询大字段内容
    	TextContent questionInfoTextContent = textContentService.selectById(book.getInfoTextContentId());
    	//转为对象
        BookObject bookObject = JsonUtil.toJsonObject(questionInfoTextContent.getContent(), BookObject.class);
        bookEditRequestVM.setAutor(bookObject.getAuthor());
        bookEditRequestVM.setBz(bookObject.getBz());
        bookEditRequestVM.setFinishcontent(bookObject.getFinishcontent());
        
    	List<TaskExam> rs=taskExamMapper.selectListByBookId(bookId);
        List<TaskPageResponseVM> taskItems=rs.stream().map(tk -> {
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
}
