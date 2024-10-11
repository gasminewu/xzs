package com.mindskip.xzs.utility.poi;

import com.deepoove.poi.data.AttachmentRenderData;
import com.deepoove.poi.data.TableRenderData;
import com.deepoove.poi.expression.Name;
import lombok.Data;

/**
 * Description:
 *
 * @author wulinlin
 * @since 2024-09-04 8:47
 */
@Data
public class WordDO {
    private TableRenderData order;
    /**
     * 表头
     */
    private String header;
    /**
     * 一、用户总体应用情况-表
     */
    @Name("detail_table")
    private UserApplicationTableDO userApplicationTableDO;

}