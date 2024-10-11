package com.mindskip.xzs.utility.poi;

import org.apache.poi.xwpf.usermodel.XWPFTable;

import com.deepoove.poi.data.style.Style;

public class TableStyle {

    /**
     * @Title: setTableStyle
     * @Description: 设置表格边框 为黑色横线
     * @Author liuren
     * @DateTime 2021年12月2日 下午9:58:43
     * @param table
     */
    public static void setTableStyle(XWPFTable table) {
        //设置table的内部横向边框
        table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE,1,1,"010101");
        //设置table的内部纵向边框
        table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE,1,1,"010101");
        //设置table的顶部边框
        table.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE,1,1,"010101");
        //设置table的顶部边框
        table.setTopBorder(XWPFTable.XWPFBorderType.SINGLE,1,1,"010101");
        //设置table的顶左边框
        table.setLeftBorder(XWPFTable.XWPFBorderType.SINGLE,1,1,"010101");
        //设置table的右部边框
        table.setRightBorder(XWPFTable.XWPFBorderType.SINGLE,1,1,"010101");
    }

    /**
     * @Title: setFontStyle
     * @Description: 设置动态表格中的字体样式和字体大小
     * @Author liuren
     * @DateTime 2021年12月2日 下午10:02:10
     * @return
     */
    public static Style setFontStyle() {
        Style style = new Style();
        style.setFontFamily("仿宋");
        style.setFontSize(14);
        return style;
    }
}

