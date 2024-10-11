package com.mindskip.xzs.utility.poi;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import com.deepoove.poi.policy.TableRenderPolicy;
import com.deepoove.poi.util.TableTools;

import cn.hutool.core.collection.CollUtil;

/**
 * 创建表格及合并
 *
 * @param
 * @return
 *
 * @变更记录 2024/9/4 10:54 武林林 创建
 */
public class DetailTablePolicyMerge extends DynamicTableRenderPolicy{

    /**
     * 表格有几列
     */
    int column = 0;
    /**
     * 第几列，从0-n 行，n-m行合并
     */

    Map<Integer,List<Integer>> mapMerge = new HashMap<>();

    public DetailTablePolicyMerge(int i, Map<Integer,List<Integer>> map) {
        column = i;
        mapMerge=map;
    }


    @Override
    public void render(XWPFTable table, Object data) throws Exception {
        if (null == data) {
            return;
        }
        TableStyle.setTableStyle(table);

        UserApplicationTableDO detailData = (UserApplicationTableDO) data;

        List<RowRenderData> goods = detailData.getLabors();
        if (null != goods) {
            table.removeRow(Contanst.START_ROW);
            for (RowRenderData good : goods) {
                XWPFTableRow insertNewTableRow = table.insertNewTableRow(Contanst.START_ROW);
                for (int j = 0; j < column; j++) {
                    insertNewTableRow.createCell();
                }
                ;
                //合并单元格
                TableRenderPolicy.Helper.renderRow(table.getRow(Contanst.START_ROW), good, TableStyle.setFontStyle());
            }
        }

        // 合并
        if(CollUtil.isEmpty(mapMerge)){
            return;
        }
        mapMerge.forEach((key, value) -> {
            int fromRow=1;
            for (Integer rown:value){
                TableTools.mergeCellsVertically(table, key, fromRow, rown);
                fromRow=rown+1;
            }
        });

    }




}

