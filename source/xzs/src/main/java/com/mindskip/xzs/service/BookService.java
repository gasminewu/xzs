package com.mindskip.xzs.service;

import com.github.pagehelper.PageInfo;
import com.mindskip.xzs.domain.Book;
import com.mindskip.xzs.viewmodel.admin.book.BookEditRequestVM;
import com.mindskip.xzs.viewmodel.admin.book.BookPageRequestVM;

public interface BookService extends BaseService<Book> {

    PageInfo<Book> page(BookPageRequestVM requestVM);

    Book insertFullBook(BookEditRequestVM model, Integer userId);

    BookEditRequestVM getBookEditRequestVM(Integer bookId);


}
