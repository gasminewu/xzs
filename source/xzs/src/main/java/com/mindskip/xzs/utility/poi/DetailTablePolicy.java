package com.mindskip.xzs.utility.poi;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import com.deepoove.poi.policy.TableRenderPolicy;

/**
 * @author Administrator
 */
public class DetailTablePolicy extends DynamicTableRenderPolicy{

    /**
     * 表格有几列
     */
    int column = 0;


    public DetailTablePolicy(int i) {
        column = i;
    }
    /**
     * 表格有几列
     */
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
                TableRenderPolicy.Helper.renderRow(table.getRow(Contanst.START_ROW), good, TableStyle.setFontStyle());
            }
        }
    }

}

