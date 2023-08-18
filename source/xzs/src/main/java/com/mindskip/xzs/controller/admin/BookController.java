package com.mindskip.xzs.controller.admin;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.mindskip.xzs.base.BaseApiController;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.base.SystemCode;
import com.mindskip.xzs.domain.Book;
import com.mindskip.xzs.service.BookService;
import com.mindskip.xzs.service.TextContentService;
import com.mindskip.xzs.utility.DateTimeUtil;
import com.mindskip.xzs.utility.ErrorUtil;
import com.mindskip.xzs.utility.PageInfoHelper;
import com.mindskip.xzs.viewmodel.admin.book.BookEditRequestVM;
import com.mindskip.xzs.viewmodel.admin.book.BookPageRequestVM;
import com.mindskip.xzs.viewmodel.admin.book.BookResponseVM;

@RestController("AdminBookController")
@RequestMapping(value = "/api/admin/book")
public class BookController extends BaseApiController {

    private final BookService bookService;
    private final TextContentService textContentService;

    @Autowired
    public BookController(BookService bookService, TextContentService textContentService) {
        this.bookService = bookService;
        this.textContentService = textContentService;
    }

    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public RestResponse<PageInfo<BookResponseVM>> pageList(@RequestBody BookPageRequestVM model) {
        PageInfo<Book> pageInfo = bookService.page(model);
        PageInfo<BookResponseVM> page = PageInfoHelper.copyMap(pageInfo, q -> {
        	BookResponseVM vm = modelMapper.map(q, BookResponseVM.class);
            vm.setCreateTime(DateTimeUtil.dateShortFormat(q.getCreateTime()));
            return vm;
        });
        return RestResponse.ok(page);
    }

    /**
     * 更新或者修改书籍
     * @param model
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public RestResponse<?> edit(@RequestBody @Valid BookEditRequestVM model) {
    	//验证
        RestResponse<?> validbookEditRequestResult = validbookEditRequestVM(model);
        if (validbookEditRequestResult.getCode() != SystemCode.OK.getCode()) {
            return validbookEditRequestResult;
        }
        bookService.insertFullBook(model, getCurrentUser().getId());

        return RestResponse.ok();
    }

    @RequestMapping(value = "/select/{id}", method = RequestMethod.POST)
    public RestResponse<BookEditRequestVM> select(@PathVariable Integer id) {
         BookEditRequestVM newVM = bookService.getBookEditRequestVM(id);
        return RestResponse.ok(newVM);
    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public RestResponse<?> delete(@PathVariable Integer id) {
        Book book = bookService.selectById(id);
        book.setDeleted(true);
        bookService.updateByIdFilter(book);
        return RestResponse.ok();
    }

    /**
     * 新增或者修改书籍 验证
     * @param model
     * @return
     */
    private RestResponse<?> validbookEditRequestVM(BookEditRequestVM model) {
    	if (StringUtils.isBlank(model.getTitle())) {
    		String errorMsg = ErrorUtil.parameterErrorFormat("title", "不能为空");
    		return new RestResponse<>(SystemCode.ParameterValidError.getCode(), errorMsg);
    	}
        return RestResponse.ok();
    }
}
