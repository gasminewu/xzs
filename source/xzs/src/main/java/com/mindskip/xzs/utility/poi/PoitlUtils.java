package com.mindskip.xzs.utility.poi;


import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import org.apache.poi.hpsf.Decimal;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @author 17279
 */
public class PoitlUtils {

    /**
     * @param worDo 生成模板文件所需的所有数据
     * @param templateFilePath 模板文件路径（这里读取的是 resources 下的文件）
     * @param outputFilePath 模板文件输入的地址
     */
    public static void generateWordFile(WordDO worDo, String templateFilePath, String outputFilePath, Configure config) {
        // 读取模板文件
        try (InputStream templateIn = PoitlUtils.class.getResourceAsStream(templateFilePath)) {
            // 生成模板文件
            assert templateIn != null;
            XWPFTemplate template = XWPFTemplate.compile(templateIn,config).render(worDo);
            template.writeAndClose(Files.newOutputStream(Paths.get(outputFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前excel所有的行
     * @param uploadfile
     */
    public  static  List<List<String[]>>  readerAllSheetExcel(){
        InputStream is;
        XSSFWorkbook sheets=null;
        try {
            // is = uploadfile.getInputStream();
            //XSSFWorkbook的一个实例化对象相当于一个excel文件
            sheets = new XSSFWorkbook(Files.newInputStream(new File("C:\\Users\\Administrator\\Desktop\\xzs.xlsx").toPath()));
            // is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 所有的sheet数量
        int numberOfSheets =sheets.getNumberOfSheets();
        XSSFSheet sheet=null;
        List<List<String[]>> result = new Vector<>();
        for (int i=0;i<numberOfSheets;i++){
            sheet=sheets.getSheetAt(i);
            //遍历所有的行
            List<String[]> list=new ArrayList<>();
            for (int j = 0; j <= sheet.getLastRowNum(); j++) {
                XSSFRow row=sheet.getRow(j);
                if(null==row){
                   continue;
                }
                String[] cells=new String[row.getLastCellNum()];
                for (int k=0;k<row.getLastCellNum();k++){
                    cells[k]=getValue(row.getCell(k));
                }
                list.add(cells);
            }
            result.add(list);
        }
        return result;
    }
    private static String getValue(XSSFCell cell){
        String cellValue="";
        if(null==cell){
            return  cellValue;
        }
        try {
            DecimalFormat df=new DecimalFormat("0.00");
            if(cell.getCellType()== CellType.NUMERIC){
                DecimalFormat decimalFormat = new DecimalFormat("#");
                cellValue = decimalFormat.format(cell.getNumericCellValue()).trim();
            }else if(cell.getCellType()== CellType.STRING){
                cellValue=cell.getStringCellValue();
            }else if(cell.getCellType()== CellType.BOOLEAN){
                cellValue=String.valueOf(cell.getBooleanCellValue());
            }else if(cell.getCellType()== CellType.ERROR){

            }else if(cell.getCellType()== CellType.FORMULA){
                cellValue=df.format(cell.getNumericCellValue());
            }else{
                cellValue=null;
            }
        }catch (Exception e){
            e.printStackTrace();
            cellValue="--1111";
        }
        return cellValue;
    }
}
