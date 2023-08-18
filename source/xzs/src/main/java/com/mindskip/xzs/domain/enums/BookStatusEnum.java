package com.mindskip.xzs.domain.enums;

public enum BookStatusEnum {

    OK(1, "正常"),
    Publish(2, "归档"),
	Buy(3, "计划购买");

    int code;
    String name;

    BookStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
