package com.mindskip.xzs.repository;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mindskip.xzs.domain.Book;
import com.mindskip.xzs.domain.other.KeyValue;
import com.mindskip.xzs.viewmodel.admin.book.BookPageRequestVM;

@Mapper
public interface BookMapper extends BaseMapper<Book> {

    List<Book> page(BookPageRequestVM requestVM);

    List<Book> selectByIds(@Param("ids") List<Integer> ids);

    Integer selectAllCount();
    
    /**
     * 获取最大的顺序号
     */
    Integer selectMaxSn();
    /**
     * 大于等于当前值的都+1
     */
    Integer updateGTSn(@Param("sninput") Integer sninput);

    List<KeyValue> selectCountByDate(@Param("startTime") Date startTime,@Param("endTime") Date endTime);
}
