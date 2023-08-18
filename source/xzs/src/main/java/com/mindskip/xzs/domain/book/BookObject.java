package com.mindskip.xzs.domain.book;



import lombok.Data;

@Data
public class BookObject {

    private String author;

    private String bz;
    /**
     * 更改状态的说明，只有最后一次的说明
     */
    private String finishcontent;


}
