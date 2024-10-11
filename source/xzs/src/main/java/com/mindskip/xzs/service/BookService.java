package com.mindskip.xzs.service;

import com.github.pagehelper.PageInfo;
import com.mindskip.xzs.domain.Book;
import com.mindskip.xzs.domain.User;
import com.mindskip.xzs.viewmodel.admin.book.BookEditRequestVM;
import com.mindskip.xzs.viewmodel.admin.book.BookPageRequestVM;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookService extends BaseService<Book> {

    PageInfo<Book> page(BookPageRequestVM requestVM);

    Book insertFullBook(BookEditRequestVM model, Integer userId);

    BookEditRequestVM getBookEditRequestVM(Integer bookId);
    /**
     * 导入，根据title 插入或者更新
     */
    void importinsertFullBook(MultipartFile uploadfile) ;

	void taskInit(User currentUser,BookPageRequestVM requestVM);
	void updateSelectionStatus(User currentUser, List<BookPageRequestVM> requestVM);

}
