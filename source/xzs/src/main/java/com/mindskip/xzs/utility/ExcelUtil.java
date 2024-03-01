package com.mindskip.xzs.utility;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//import com.chinasoft.biz.attendanceTools.XLSB2Lists;
//import com.chinasoft.biz.attendanceTools.controller.OmpFindDataController;
//import com.chinasoft.util.ArraysTools;
//import com.chinasoft.util.LoggerUtil;
//import com.chinasoft.util.StringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.binary.XSSFBSharedStringsTable;
import org.apache.poi.xssf.binary.XSSFBSheetHandler;
import org.apache.poi.xssf.binary.XSSFBStylesTable;
import org.apache.poi.xssf.eventusermodel.XSSFBReader;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class ExcelUtil {

   private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

   /**
    * Excel 2003
    */
   public final static String XLS = "xls";
   /**
    * Excel 2007
    */
   public final static String XLSX = "xlsx";

   /**
    *
    * @Title: getExcelHeadTitle
    * @Description: 读取excel 标题列
    * @param filePath	excel文件所在路径
    * @return List<String>    返回标题内容数组
    */
   public List<String> getExcelHeadTitle(String filePath){
       File file = new File(filePath);
       return getExcelHeadTitle(file);
   }

   public static Workbook getWorkBook(MultipartFile file) throws IOException {
       Workbook workBook = null;
       if(file == null) return null;

       String endName = getEndname(file.getOriginalFilename());
       if(endName == null) return null;

       if (endName.toLowerCase().equals(XLS)) {
           workBook = new HSSFWorkbook(file.getInputStream());
       } else if (endName.toLowerCase().equals(XLSX)) {
           workBook = new XSSFWorkbook(file.getInputStream());
       }
       return workBook;
   }

   public static Workbook getWorkBook(File file) throws IOException {
       Workbook workBook = null;
       if(file == null) return null;

       String endName = getEndname(file.getName());
       if(endName == null) return null;

       InputStream in = new FileInputStream(file);

       if (endName.toLowerCase().equals(XLS)) {
           workBook = new HSSFWorkbook(in);
       } else if (endName.toLowerCase().equals(XLSX)) {
           workBook = new XSSFWorkbook(in);
       }
       return workBook;
   }

   private static String getEndname(String s) {
       if(StringUtils.isEmpty(s)) {
           return null;
       }
       if(s.indexOf(".") > -1) {
           return s.substring(s.lastIndexOf(".")+1, s.length());
       }
       return null;
   }
   /**
    *
    * @Title: getExcelHeadTitle
    * @Description: 读取excel 标题列
    * @param f	excel文件所在路径
    * @return List<String>    返回标题内容数组
    */
   public List<String> getExcelHeadTitle(File f){
       String name = getMemType(f.getName());
       FileInputStream fi = null;
       try {
           fi = new FileInputStream(f);
       } catch (FileNotFoundException e2) {
           LoggerUtil.error(this.getClass(), "文件不存在", e2);
           e2.printStackTrace();
       }

       Workbook workbook = null;
       try {
           workbook = getWorkbookBySheetName(fi , name, null);
       } catch (IOException e1) {
           LoggerUtil.error(this.getClass(), "workbook 对象获取失败", e1);
           e1.printStackTrace();
       }
       Sheet sheet = getSheet(workbook, null);
       FormulaEvaluator evaluator = getEvaluator(workbook);
       return getExcelHeadMenu(sheet, evaluator);
   }

   public static JsonObject createKeysHeadRelations(List<String> headList, List<String> keyList){
       int length = headList.size();
       JsonObject result = new JsonObject();
       for (int i = 0; i < length; i++) {
           result.addProperty(headList.get(i), keyList.get(i));
       }
       return result;
   }

   public static JsonObject createKeysHeadRelations(List<String> headList, String[] keyArray){
       List<String> keyList = Arrays.asList(keyArray);
       return createKeysHeadRelations(headList, keyList);
   }

   public static JsonObject createKeysHeadRelations(String[] headArray, String[] keyArray){
       List<String> keyList = Arrays.asList(keyArray);
       List<String> headList = Arrays.asList(headArray);
       return createKeysHeadRelations(headList, keyList);
   }

   /**
    *
    * @Title: getExcelContent
    * @Description: 根据列头值获取excel中文件内容	默认读取第一个标签页
    * @param filePath	文件实际路径
    * @param s	excel标题头对应的变量名称
    * @return JsonArray    返回类型	以key-value 数组形式返回， key为数据库中对应变量名称，value为String类型内容
    */
   public JsonArray getExcelContent(String filePath, String [] s){
       return getExcelContent(filePath,null , s);
   }

   /**
    *
    * @Title: getExcelContent
    * @Description: 根据列头值获取excel中文件内容	默认读取第一个标签页
    * @param filePath	文件实际路径
    * @param s	excel标题头对应的变量名称
    * @param formate	格式化列内容，例如：{"fieldName1":{"英特尔":"1","amd":"2"},"fieldName2":{"黑"："1","白"："2","灰"："3"}}
    * @return JsonArray    返回类型	以key-value 数组形式返回， key为数据库中对应变量名称，value为String类型内容
    */
   public JsonArray getExcelContent(String filePath, String [] s, JsonObject formate){
       return getExcelContent(filePath,null , s, formate);
   }

   /**
    *
    * @Title: getExcelContent
    * @Description: 根据列头值获取excel中文件内容
    * @param filePath	文件实际路径
    * @param sheetName	excel标签页名称
    * @param s	excel标题头对应的变量名称
    * @return JsonArray    返回类型	以key-value 数组形式返回， key为数据库中对应变量名称，value为String类型内容
    */
   public JsonArray getExcelContent(String filePath, String sheetName, String [] s, JsonObject formate){
       File file = new File(filePath);
       FileInputStream fis = null;
       Workbook workbook = null;
       JsonArray resultArray = null;
       if(file.exists()){

           try {
               fis = new FileInputStream(file);
           } catch (FileNotFoundException e1) {
               LoggerUtil.error(this.getClass(), "excel 文件不存在。捕获位置：getExcelContent 方法中", e1);
               e1.printStackTrace();
               return null;
           }finally {
               if(fis != null) {
                   try {
                       fis.close();
                   } catch (IOException e) {
                       LoggerUtil.error(this.getClass(), "excel 文件不存在。捕获位置：getExcelContent 方法中", e);
                   }
               }
           }
           Sheet sheet = getSheet(workbook, null);
           FormulaEvaluator evaluator = getEvaluator(workbook);
           List<String> headlist = getExcelHeadMenu(sheet, evaluator);
           JsonObject keys = createKeysHeadRelations(headlist, s);
           if(formate == null){
               resultArray = exportListFromExcelArray(workbook, sheetName, keys);
           }else{
               resultArray = exportListFromExcelArray(workbook, sheetName, keys, formate);
           }

       }else{
           LoggerUtil.error(this.getClass(), "excel 文件不存在。捕获位置：getExcelContent 方法中");
           return null;
       }
       return resultArray;
   }

   /**
    *
    * @Title: getExcelContent
    * @Description: 根据列头值获取excel中文件内容
    * @param filePath	文件实际路径
    * @param filePath	excel标签页名称
    * @param s	excel标题头对应的变量名称
    * @return JsonArray    返回类型	以key-value 数组形式返回， key为数据库中对应变量名称，value为String类型内容
    */
   public JsonArray getExcelContent(String filePath, String [] s, JsonObject formate, int[] timeIndex, String pattern){
       File file = new File(filePath);
       FileInputStream fis = null;
       Workbook workbook = null;
       JsonArray resultArray = null;
       if(file.exists()){
           try {
               fis = new FileInputStream(file);
           } catch (FileNotFoundException e1) {
               LoggerUtil.error(this.getClass(), "excel 文件不存在。捕获位置：getExcelContent 方法中",e1);
               return null;
           }finally {
               if(fis != null) {
                   try {
                       fis.close();
                   } catch (IOException e) {
                       LoggerUtil.error(this.getClass(), "excel 文件不存在。捕获位置：getExcelContent 方法中",e);

                   }
               }
           }
           Sheet sheet = getSheet(workbook, null);
           FormulaEvaluator evaluator = getEvaluator(workbook);
           List<String> headlist = getExcelHeadMenu(sheet, evaluator);
           JsonObject keys = createKeysHeadRelations(headlist, s);
           if(formate == null){
               resultArray = exportListFromExcelArray(workbook, null, keys, timeIndex,pattern);
           }else{
               resultArray = exportListFromExcelArray(workbook, null, keys, formate, timeIndex,pattern);
           }

       }else{
           LoggerUtil.error(this.getClass(), "excel 文件不存在。捕获位置：getExcelContent 方法中");
           return null;
       }
       return resultArray;
   }

   /**
    *
    * @Title: getExcelContent
    * @Description: 根据列头值获取excel中文件内容
    * @param filePath	文件实际路径
    * @param timeIndex	excel标签页名称
    * @param s	excel标题头对应的变量名称
    * @return JsonArray    返回类型	以key-value 数组形式返回， key为数据库中对应变量名称，value为String类型内容
    */
   public JsonArray getExcelContent(String filePath, String [] s, JsonObject formate, int[] timeIndex){
       File file = new File(filePath);
       FileInputStream fis = null;
       Workbook workbook = null;
       JsonArray resultArray = null;
       if(file.exists()){
           try {
               fis = new FileInputStream(file);
           } catch (FileNotFoundException e1) {
               LoggerUtil.error(this.getClass(), "excel 文件不存在。捕获位置：getExcelContent 方法中",e1);
               return null;
           }finally {
               if(fis != null) {
                   try {
                       fis.close();
                   } catch (IOException e) {
                       LoggerUtil.error(this.getClass(), "excel 文件不存在。捕获位置：getExcelContent 方法中",e);
                   }
               }
           }
           Sheet sheet = getSheet(workbook, null);
           FormulaEvaluator evaluator = getEvaluator(workbook);
           List<String> headlist = getExcelHeadMenu(sheet, evaluator);
           JsonObject keys = createKeysHeadRelations(headlist, s);
           if(formate == null){
               resultArray = exportListFromExcelArray(workbook, null, keys, timeIndex,null);
           }else{
               resultArray = exportListFromExcelArray(workbook, null, keys, formate, timeIndex,null);
           }

       }else{
           LoggerUtil.error(this.getClass(), "excel 文件不存在。捕获位置：getExcelContent 方法中");
           return null;
       }
       return resultArray;
   }
   /**
    *
    * @Title: getExcelContent
    * @Description: 根据列头值获取excel中文件内容
    * @param filePath	文件实际路径
    * @param sheetName	excel标签页名称
    * @param s	excel标题头对应的变量名称
    * @return JsonArray    返回类型	以key-value 数组形式返回， key为数据库中对应变量名称，value为String类型内容
    */
   public JsonArray getExcelContent(String filePath, String sheetName, String [] s){
       return getExcelContent(filePath, sheetName, s, null);
   }

   /**
    *
    * @Title: getExcelContent
    * @Description: 根据列头值获取excel中文件内容
    * @param filePath	文件实际路径
    * @param sheetName	excel标签页名称
    * @param keys	key-value形式。key为excel列头名称,value为该列对应数据中字段名称。
    * @return JsonArray    返回类型	以key-value 数组形式返回， key为数据库中对应变量名称，value为String类型内容
    */
   private JsonArray getExcelContent(String filePath, String sheetName, JsonObject keys){
       File file = new File(filePath);
       FileInputStream fis = null;
       Workbook workbook = null;
       JsonArray resultArray = null;
       if(file.exists()){
           String extensionName = getMemType(file.getName());
           try {
               fis = new FileInputStream(file);
           } catch (FileNotFoundException e1) {
               LoggerUtil.error(this.getClass(), "excel 文件不存在。捕获位置：getExcelContent 方法中");
               e1.printStackTrace();
               return null;
           }

           try {
               workbook = getWorkbookBySheetName(fis, extensionName, sheetName);
           } catch (IOException e) {
               LoggerUtil.error(this.getClass(), "excel 文件读取异常。捕获位置：getExcelContent 方法中");
               e.printStackTrace();
               return null;
           }

           resultArray = exportListFromExcelArray(workbook, sheetName, keys);
       }else{
           LoggerUtil.error(this.getClass(), "excel 文件不存在。捕获位置：getExcelContent 方法中");
           return null;
       }
       return resultArray;
   }

   private Workbook getWorkbookBySheetName(InputStream is,
                                           String extensionName, String sheetName) throws IOException {

       Workbook workbook = null;

       if (extensionName.toLowerCase().equals(XLS)) {
           workbook = new HSSFWorkbook(is);
       } else if (extensionName.toLowerCase().equals(XLSX)) {
           workbook = new XSSFWorkbook(is);
       }
       return workbook;
   }

   /**
    *
    * @Title: getMemType
    * @Description: 获取文件扩展类型
    * @param fileName	文件名称全称
    * @return String    扩展名称
    */
   private String getMemType(String fileName){
       return fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
   }
   private Sheet getSheet(Workbook workbook, String sheetName){
       Sheet sheet = null;
       if(sheetName == null || "".equals(sheetName)){
           sheet = workbook.getSheetAt(0);
       }else{
           sheet = workbook.getSheet(sheetName);
       }
       return sheet;
   }

   private FormulaEvaluator getEvaluator(Workbook workbook){
       return workbook.getCreationHelper().createFormulaEvaluator();
   }

   private JsonArray exportListFromExcelArray(Workbook workbook, String sheetName, JsonObject keys) {
       // 解析公式结果
       FormulaEvaluator evaluator = getEvaluator(workbook);
       return  exportListFromExcelArray(workbook, sheetName, keys, evaluator, null);
   }

   private JsonArray exportListFromExcelArray(Workbook workbook, String sheetName, JsonObject keys, JsonObject formate) {
       // 解析公式结果
       FormulaEvaluator evaluator = getEvaluator(workbook);
       return  exportListFromExcelArray(workbook, sheetName, keys, evaluator, formate);
   }

   private JsonArray exportListFromExcelArray(Workbook workbook, String sheetName, JsonObject keys, int [] timeIndex,String pattern) {
       // 解析公式结果
       FormulaEvaluator evaluator = getEvaluator(workbook);
       return  exportListFromExcelArray(workbook, sheetName, keys, evaluator, null,timeIndex, pattern);
   }

   private JsonArray exportListFromExcelArray(Workbook workbook, String sheetName, JsonObject keys, JsonObject formate, int [] timeIndex, String pattern) {
       // 解析公式结果
       FormulaEvaluator evaluator = getEvaluator(workbook);
       return  exportListFromExcelArray(workbook, sheetName, keys, evaluator, formate,timeIndex, pattern);
   }

   private JsonArray exportListFromExcelArray(Workbook workbook, String sheetName, JsonObject keys, FormulaEvaluator evaluator, JsonObject formate) {

       Sheet sheet = getSheet(workbook, sheetName);

       List<String> headList = getHeadMenu(sheet, keys, evaluator);

       int minRowIx = sheet.getFirstRowNum();
       int maxRowIx = sheet.getLastRowNum();
       maxRowIx = sheet.getPhysicalNumberOfRows();

       Row row = null;
       int minColIx;
       int maxColIx;
       Cell cell = null;
       CellValue cellValue = null;
       String value = null;
       String head = null;
       JsonObject formatter = null;
       JsonArray rowsData = new JsonArray();
       for (int rowIx = minRowIx+1; rowIx < maxRowIx; rowIx++) {
           row = sheet.getRow(rowIx);
           minColIx = row.getFirstCellNum();
           maxColIx = row.getLastCellNum();
           JsonObject rowData = new JsonObject();
           int headIndex = 0;
           int isEmptyRow = 0;
           int isEmptyCell = 1;
           if(isEmptyRows(row, maxColIx)){
               continue;
           }
           for (int colIx = minColIx; colIx < maxColIx; colIx++) {
               cell = row.getCell(colIx);
               if(cell == null){
                   value = "";
                   isEmptyCell = 0;
               }else{
                   if(cell.getCellTypeEnum() == CellType.BLANK){
                       cell.setCellValue("");
                       isEmptyCell = 0;
                   }
                   cell.setCellType(CellType.STRING);
                   cellValue = evaluator.evaluate(cell);

                   if (cellValue == null) {
                       continue;
                   }
                   value = cellValue.getStringValue();
               }
               head = headList.get(headIndex);
               if(formate != null){
                   if(formate.has(head)){
                       formatter = formate.get(head).getAsJsonObject();
                       if(formatter.has(value)){
                           value = formatter.get(value).getAsString();
                       }
                   }
               }
               rowData.addProperty(head, value);
               isEmptyRow += isEmptyCell;
               headIndex++;
           }
           if(rowData.size() > 0 && isEmptyRow !=0){
               rowsData.add(rowData);
           }
       }
       return rowsData;
   }

   private JsonArray exportListFromExcelArray(Workbook workbook, String sheetName, JsonObject keys, FormulaEvaluator evaluator, JsonObject formate, int[] timeIndex,String pattern) {

       Sheet sheet = getSheet(workbook, sheetName);

       List<String> headList = getHeadMenu(sheet, keys, evaluator);
       String patte = null;
       if(pattern == null){
           patte = "yyyy-MM";
       }else{
           patte = pattern;
       }
       SimpleDateFormat dateFormate = new SimpleDateFormat(patte);
       int minRowIx = sheet.getFirstRowNum();
       int maxRowIx = sheet.getLastRowNum();
       maxRowIx = sheet.getPhysicalNumberOfRows();

       Row row = null;
       int minColIx;
       int maxColIx;
       Cell cell = null;
       CellValue cellValue = null;
       String value = null;
       String head = null;
       JsonObject formatter = null;
       JsonArray rowsData = new JsonArray();
       for (int rowIx = minRowIx+1; rowIx < maxRowIx; rowIx++) {
           row = sheet.getRow(rowIx);
           if(row == null) continue;
           minColIx = row.getFirstCellNum();
           maxColIx = row.getLastCellNum();
           JsonObject rowData = new JsonObject();
           int headIndex = 0;
           int isEmptyRow = 0;
           int isEmptyCell = 1;
           if(isEmptyRows(row, maxColIx)){
               continue;
           }
           for (int colIx = minColIx; colIx < maxColIx; colIx++) {
               cell = row.getCell(colIx);
               if(cell == null){
                   value = "";
                   isEmptyCell = 0;
               }else{
                   cell.setCellType(CellType.STRING);
                   if(cell.getCellTypeEnum() == CellType.BLANK){
                       cell.setCellValue("");
                       isEmptyCell = 0;
                       value = "";
                   }
//                   else if(ArraysTools.contains(timeIndex, colIx)){
//                       if(cell.getCellTypeEnum() == CellType.NUMERIC) {
//                           value = dateFormate.format(cell.getDateCellValue());
//                       }else if(cell.getCellTypeEnum() == CellType.STRING) {
//                           value = cell.getStringCellValue();
//                       }
//                   }
                   else{
                       cell.setCellType(CellType.STRING);
                       cellValue = evaluator.evaluate(cell);

                       if (cellValue == null) {
                           continue;
                       }
                       value = cellValue.getStringValue();
                   }

               }
               head = headList.get(headIndex);
               if(formate != null){
                   if(formate.has(head)){
                       formatter = formate.get(head).getAsJsonObject();
                       if(formatter.has(value)){
                           value = formatter.get(value).getAsString();
                       }
                   }
               }
               rowData.addProperty(head, value);
               isEmptyRow += isEmptyCell;
               headIndex++;
           }
           if(rowData.size() > 0 && isEmptyRow !=0){
               rowsData.add(rowData);
           }
       }
       return rowsData;
   }
   private List<String> getHeadMenu(Sheet sheet, JsonObject keys, FormulaEvaluator evaluator){
       Row row = sheet.getRow(0);
       int minColIx = row.getFirstCellNum();
       int maxColIx = row.getLastCellNum();
       Cell cell = null;
       CellValue cellValue = null;
       String value = null;
       List<String> headList = new ArrayList<String>();
       for (int colIx = minColIx; colIx < maxColIx; colIx++){
           cell = row.getCell(colIx);
           cellValue = evaluator.evaluate(cell);
           if (cellValue == null) {
               continue;
           }
           value = cellValue.getStringValue();
           headList.add(keys.get(value).getAsString());
       }
       return headList;
   }

   /**
    *
    * @Title: getExcelHeadMenu
    * @Description: 获取excel中标题列内容
    * @param sheet 标签页名称。 为null默认取第一个标签页
    * @param evaluator	excel解析器
    * @return List<String>    返回类型
    */
   public List<String> getExcelHeadMenu(Sheet sheet,FormulaEvaluator evaluator){
       Row row = sheet.getRow(0);
       int minColIx = row.getFirstCellNum();
       int maxColIx = row.getLastCellNum();
       Cell cell = null;
       CellValue cellValue = null;
       String value = null;
       List<String> headList = new ArrayList<String>();
       for (int colIx = minColIx; colIx <= maxColIx; colIx++){
           cell = row.getCell(colIx);
           cellValue = evaluator.evaluate(cell);
           if (cellValue == null) {
               continue;
           }
           value = cellValue.getStringValue();
           headList.add(value);
       }
       return headList;
   }

   public static List<String> getExcelHeadMenu(Sheet sheet,FormulaEvaluator evaluator, int headRow){
       Row row = sheet.getRow(headRow);
       int minColIx = row.getFirstCellNum();
       int maxColIx = row.getLastCellNum();
       Cell cell = null;
       CellValue cellValue = null;
       String value = null;
       List<String> headList = new ArrayList<String>();
       for (int colIx = minColIx; colIx <= maxColIx; colIx++){
           cell = row.getCell(colIx);
           cellValue = evaluator.evaluate(cell);
           if (cellValue == null) {
               continue;
           }
           value = cellValue.getStringValue();
           headList.add(value);
       }
       return headList;
   }



   public JsonObject createHeadKeysMapping(String[] keys,String[] excelHead){
       int len = keys.length;
       JsonObject result = new JsonObject();
       for (int i = 0; i < len; i++) {
           result.addProperty(keys[i], excelHead[i]);
       }
       return result;
   }

   public Workbook getWrokBook(String fileType){
       Workbook wb = null;
       if (XLS.equals(fileType)) {
           wb = new HSSFWorkbook();
       } else if(XLSX.equals(fileType)) {
           wb = new XSSFWorkbook();
       } else {
           LoggerUtil.error(this.getClass(), "文件格式不正确");
       }
       return wb;
   }

   /**
    *
    * @Title: write
    * @Description: 根据数据内容生成excel文件
    * @param wb	excel工作薄
    * @param keys	变量字符串数组
    * @param sheetName	标签页名称
    * @param excelHead	变量字符串对应的excel标题头数组
    * @param list	数据
    * @param headStyle	标题头样式	可为null
    * @param cellStyle	文件内容	样式可为null
    * @return Workbook    返回类型
    */
   public Workbook write(Workbook wb , String[] keys,String sheetName, String[] excelHead,List<?> list, CellStyle headStyle,CellStyle cellStyle){

       Sheet sheet =null;

       if(sheetName == null || "".equals(sheetName)){
           sheetName = "sheet1";
       }

       sheet = wb.createSheet(sheetName);

       //列宽
       for (int i=0;i<excelHead.length;i++) {
           sheet.autoSizeColumn(i);
           int length1 = sheet.getColumnWidth(i);
           int length2 = excelHead[i].length();
           int columnWidth = length1 > length2 ? length1 : length2;
           sheet.setColumnWidth(i, columnWidth*3);
       }


       // 生成字段与excel表头对应关系
       JsonObject keysMappings = createHeadKeysMapping(keys, excelHead);

       // excel写入标题头
       writeHead(sheet, keys, keysMappings, cellStyle);

       // excel写入内容
       writeContent(list, sheet, keys, headStyle);

       return wb;
   }

   /**
    *
    * @Title: write
    * @Description: 根据数据内容生成excel文件
    * @param wb	excel工作薄
    * @param keys	变量字符串数组
    * @param sheetName	标签页名称
    * @param excelHead	变量字符串对应的excel标题头数组
    * @param list	数据
    * @param headStyle	标题头样式	可为null
    * @param cellStyle	文件内容	样式可为null
    * @return Workbook    返回类型
    */
   public void writeSheet(Workbook wb , String[] keys,String sheetName, String[] excelHead,JsonArray list, CellStyle headStyle,CellStyle cellStyle){

       Sheet sheet =null;

       if(sheetName == null || "".equals(sheetName)){
           sheetName = "sheet1";
       }

       sheet = wb.createSheet(sheetName);
       //设置表格默认宽度
       //列宽
       for (int i=0;i<excelHead.length;i++) {
           sheet.autoSizeColumn(i);
           int length1 = sheet.getColumnWidth(i);
           int length2 = excelHead[i].length();
           int columnWidth = length1 > length2 ? length1 : length2;
           sheet.setColumnWidth(i, columnWidth*3);
       }

       // 生成字段与excel表头对应关系
       JsonObject keysMappings = createHeadKeysMapping(keys, excelHead);

       // excel写入标题头
       writeHead(sheet, keys, keysMappings, cellStyle);

       // excel写入内容
       writeContent(list, sheet, keys, headStyle);
   }

   public Workbook writeMultSheet(Workbook wb , List<String[]> keyList,List<String> sheetNameList, List<String[]> excelHeadList,List<List<?>> listAll, CellStyle headStyle,CellStyle cellStyle){

       int size = sheetNameList.size();
       for (int j = 0; j < size; j++) {
           Sheet sheet =null;
           String sheetName = sheetNameList.get(j);
           if(StringUtils.isEmpty(sheetName)){
               sheetName = "sheet1";
           }

           sheet = wb.createSheet(sheetName);

           String [] excelHead = excelHeadList.get(j);
           //列宽
           for (int i=0;i<excelHead.length;i++) {
               sheet.autoSizeColumn(i);
               int length1 = sheet.getColumnWidth(i);
               int length2 = excelHead[i].length();
               int columnWidth = length1 > length2 ? length1 : length2;
               sheet.setColumnWidth(i, columnWidth*3);
           }

           String [] keys = keyList.get(j);
           // 生成字段与excel表头对应关系
           JsonObject keysMappings = createHeadKeysMapping(keys, excelHead);

           // excel写入标题头
           writeHead(sheet, keys, keysMappings, cellStyle);

           List<?> list = listAll.get(j);
           // excel写入内容
           writeContent(list, sheet, keys, headStyle);
       }

       return wb;
   }

   public Workbook writeMultSheet(Workbook wb , List<String[]> keyList,List<String> sheetNameList, List<String[]> excelHeadList,List<List<?>> listAll, CellStyle headStyle,CellStyle cellStyle,JsonObject formate ) throws Exception{

       int size = sheetNameList.size();
       for (int j = 0; j < size; j++) {
           Sheet sheet =null;
           String sheetName = sheetNameList.get(j);
           if(StringUtils.isEmpty(sheetName)){
               sheetName = "sheet1";
           }

           sheet = wb.createSheet(sheetName);

           String [] excelHead = excelHeadList.get(j);
           //列宽
           for (int i=0;i<excelHead.length;i++) {
               sheet.autoSizeColumn(i);
               int length1 = sheet.getColumnWidth(i);
               int length2 = excelHead[i].length();
               int columnWidth = length1 > length2 ? length1 : length2;
               sheet.setColumnWidth(i, columnWidth*3);
           }

           String [] keys = keyList.get(j);
           // 生成字段与excel表头对应关系
           JsonObject keysMappings = createHeadKeysMapping(keys, excelHead);

           // excel写入标题头
           writeHead(sheet, keys, keysMappings, cellStyle);

           List<?> list = listAll.get(j);
           // excel写入内容
           writeContent(list, sheet, keys, headStyle, formate);
       }

       return wb;
   }

   public Workbook write(Workbook wb , String[] keys,String sheetName, String[] excelHead,JsonArray list, CellStyle headStyle,CellStyle cellStyle){

       Sheet sheet =null;

       if(sheetName == null || "".equals(sheetName)){
           sheetName = "sheet1";
       }

       sheet = wb.createSheet(sheetName);
       //设置表格默认宽度
       //列宽
       for (int i=0;i<excelHead.length;i++) {
           sheet.autoSizeColumn(i);
           int length1 = sheet.getColumnWidth(i);
           int length2 = excelHead[i].length();
           int columnWidth = length1 > length2 ? length1 : length2;
           sheet.setColumnWidth(i, columnWidth*3);
       }


       // 生成字段与excel表头对应关系
       JsonObject keysMappings = createHeadKeysMapping(keys, excelHead);

       // excel写入标题头
       writeHead(sheet, keys, keysMappings, cellStyle);

       // excel写入内容
       writeContent(list, sheet, keys, headStyle);

       return wb;
   }
   /**
    *
    * @Title: write
    * @Description: 根据数据内容生成excel文件
    * @param wb	excel工作薄
    * @param keys	变量字符串数组
    * @param sheetName	标签页名称
    * @param excelHead	变量字符串对应的excel标题头数组
    * @param list	数据
    * @param headStyle	标题头样式	可为null
    * @param cellStyle	文件内容	样式可为null
    * @param formate	格式化列内容，例如：{"fieldName1":{"1":"intel","2":"amd"},"fieldName2":{"1":"黑","2":"白","3":"灰"}}
    * @return Workbook    返回类型
    * @throws Exception
    */
   public Workbook write(Workbook wb , String[] keys,String sheetName, String[] excelHead,List<?> list, CellStyle headStyle,CellStyle cellStyle, JsonObject formate) throws Exception{

       Sheet sheet =null;

       if(sheetName == null || "".equals(sheetName)){
           sheetName = "sheet1";
       }

       sheet = wb.createSheet(sheetName);
       //列宽
       for (int i=0;i<excelHead.length;i++) {
           sheet.setColumnWidth(i, excelHead[i].length()*2000);
       }

       // 生成字段与excel表头对应关系
       JsonObject keysMappings = createHeadKeysMapping(keys, excelHead);

       // excel写入标题头
       writeHead(sheet, keys, keysMappings, cellStyle);

       // excel写入内容
       writeContent(list, sheet, keys, headStyle, formate);

       return wb;
   }

   private void writeHead(Sheet sheet, String[] keys, JsonObject keysMapping){
       writeHead(sheet, keys, keysMapping, null);
   }

   private void writeHead(Sheet sheet, String[] keys, JsonObject keysMapping, CellStyle style){
       Row row = sheet.createRow(0);
       row.setHeightInPoints(40);//表头行高
       int length = keys.length;
       Cell cell = null;
       for (int i = 0; i < length; i++) {
           cell = row.createCell(i);
           if(style != null){
               cell.setCellStyle(style);
           }
           cell.setCellValue(keysMapping.get(keys[i]).getAsString());
       }
   }

   private void writeContent(List<?> list, Sheet sheet, String[] keys,CellStyle style){
       if(list == null) return;
       JsonArray ja = gson.toJsonTree(list).getAsJsonArray();
       int size = ja.size();
       int keyLen = keys.length;
       JsonObject jo = null;
       Row row = null;
       Cell cell = null;

       for (int i = 0; i < size; i++) {
           row = sheet.createRow(i + 1);
           jo = ja.get(i).getAsJsonObject();
           for (int j = 0; j < keyLen; j++) {
               cell = row.createCell(j);
               if(style != null){
                   cell.setCellStyle(style);
               }
               cell.setCellType(CellType.STRING);
               if(jo.has(keys[j])){
                   cell.setCellValue(jo.get(keys[j]).getAsString());
               }else{
                   cell.setCellValue("");
               }

           }
       }
   }


   private void writeContentWD(List<String []> list, Sheet sheet,CellStyle style,String[] excelHead)
   {
       {
           if(list == null) return;
           JsonArray ja = gson.toJsonTree(list).getAsJsonArray();
           int size = ja.size();
           int keyLen = excelHead.length;
           JsonObject jo = null;
           Row row = null;
           Cell cell = null;

           for (int i = 0; i < size; i++) {
               row = sheet.createRow(i + 1);
               for (int j = 0; j < keyLen; j++) {
                   cell = row.createCell(j);
                   if(style != null){
                       cell.setCellStyle(style);
                   }
                   cell.setCellType(CellType.STRING);
                   cell.setCellValue(list.get(i)[j]);
               }
           }
       }
   }

   private void writeContentCard(List<String []> list, Sheet sheet,CellStyle style,String[] excelHead){
       if(list == null) return;
       JsonArray ja = gson.toJsonTree(list).getAsJsonArray();
       int size = ja.size();
       int keyLen = excelHead.length+1;
       JsonObject jo = null;
       Row row = null;
       Cell cell = null;

       for (int i = 0; i < size; i++) {
           row = sheet.createRow(i + 6);
           for (int j = 0; j < keyLen; j++) {
               cell = row.createCell(j);
               if(style != null){
                   cell.setCellStyle(style);
               }
               cell.setCellType(CellType.STRING);
               cell.setCellValue(list.get(i)[j]);
           }
       }
   }

   private void writeContent(JsonArray ja, Sheet sheet, String[] keys,CellStyle style){
       if(ja == null) return;

       int size = ja.size();
       int keyLen = keys.length;
       JsonObject jo = null;
       Row row = null;
       Cell cell = null;

       for (int i = 0; i < size; i++) {
           row = sheet.createRow(i + 1);
           jo = ja.get(i).getAsJsonObject();
           for (int j = 0; j < keyLen; j++) {
               cell = row.createCell(j);
               if(style != null){
                   cell.setCellStyle(style);
               }
               cell.setCellType(CellType.STRING);
               if(jo.has(keys[j])){
                   if(jo.get(keys[j]).isJsonNull()) {
                       cell.setCellValue("");
                   }else {
                       cell.setCellValue(jo.get(keys[j]).getAsString());
                   }
               }else{
                   cell.setCellValue("");
               }

           }
       }
   }

   public static void writeDiskExcelContent(JsonArray ja, Sheet sheet, String[] keys){
       writeDiskExcelContent(ja, sheet, keys, 1);
   }

   public static void writeDiskExcelContent(JsonArray ja, Sheet sheet, String[] keys,int rowStart){
       writeDiskExcelContent(ja, sheet, keys, rowStart,null);
   }

   public static void writeDiskExcelContent(JsonArray ja, Sheet sheet, String[] keys,int rowStart,CellStyle cs){
       if(ja == null) return;
       int size = ja.size();
       int keyLen = keys.length;
       JsonObject jo = null;
       Row row = null;
       Cell cell = null;

       for (int i = 0; i < size; i++) {
           //row = sheet.getRow(i + 1);
           //if(row == null) {
           row = sheet.createRow(i+rowStart);
           //}
           jo = ja.get(i).getAsJsonObject();
           for (int j = 0; j < keyLen; j++) {
               cell = row.getCell(j);
               if(cell == null) {
                   cell = row.createCell(j);
               }
               if(cs != null) {
                   cell.setCellStyle(cs);
               }
               if(keys[j].contains(".")) {
                   String[] tempKeys = keys[j].split("\\.");
                   String val = getObjectFieldValue(tempKeys,jo);
                   cell.setCellValue(val);
               }else {
                   if(jo.has(keys[j])){
                       if(jo.get(keys[j]).isJsonNull()) {
                           cell.setCellValue("");
                       }else {
                           cell.setCellValue(jo.get(keys[j]).getAsString());
                       }
                   }else{
                       cell.setCellValue("");
                   }
               }
           }
       }
   }
   private static String getObjectFieldValue(String[] fields,JsonObject jo) {

       JsonObject temp = null;
       for (int k = 0; k < fields.length; k++) {
           if(temp == null) {
               temp = jo;
           }
           JsonElement je = temp.get(fields[k]);
           if(je.isJsonObject()) {
               temp = je.getAsJsonObject();
               continue;
           }else if(je.isJsonNull()){
               return "";
           }else if(je.isJsonPrimitive()) {
               return je.getAsString();
           }
       }
       return "";
   }

   public static void writeDiskExcelContent(JsonArray ja, Sheet sheet, String[] keys,int rowStart,CellStyle cs, JsonObject formate) {
       if(ja == null) return;
       int size = ja.size();
       int keyLen = keys.length;
       JsonObject jo = null;
       Row row = null;
       Cell cell = null;

       int isDate = 0;
       String formatter = null;
       Date formateDate = null;
       String filedName = null;
       String value = null;
       JsonObject temp = null;
       for (int i = 0; i < size; i++) {
           row = sheet.createRow(i+rowStart);
           jo = ja.get(i).getAsJsonObject();
           for (int j = 0; j < keyLen; j++) {
               cell = row.getCell(j);
               if(cell == null) {
                   cell = row.createCell(j);
               }
               if(cs != null) {
                   cell.setCellStyle(cs);
               }
               if(keys[j].contains(".")) {
                   String[] tempKeys = keys[j].split("\\.");
                   String val = getObjectFieldValue(tempKeys,jo);
                   cell.setCellValue(val);
               }else {
                   if(jo.has(keys[j])){
                       if(jo.get(keys[j]).isJsonNull()) {
                           cell.setCellValue("");
                       }else {
                           filedName=keys[j];
                           value = jo.get(filedName).getAsString();

                           if(formate.has(filedName)){
                               temp = formate.get(filedName).getAsJsonObject();
                               if(temp.has(value)){
                                   value = temp.get(value).getAsString();
                               }else if("-1".equals(value)){
                                   value="";
                               }
                               if(temp.has("isDate") && StringUtils.isNotEmpty(value)) {
                                   isDate = temp.get("isDate").getAsInt();
                                   if(isDate == 1) {
                                       if(temp.has("formatter")) {
                                           formatter = temp.get("formatter").getAsString();

                                           try {
                                               formateDate = new SimpleDateFormat(formatter).parse(value);
                                           } catch (ParseException e) {
                                               // TODO Auto-generated catch block
                                               e.printStackTrace();
                                           }

                                           value = new SimpleDateFormat(formatter).format(formateDate);
                                       }
                                   }
                               }
                           }
                       }
                       cell.setCellValue(value);
                   }else{
                       cell.setCellValue("");
                   }
               }
           }
       }
   }
   private void writeContent(List<?> list, Sheet sheet, String[] keys,CellStyle style, JsonObject formate,int rowStart) throws Exception{
       JsonArray ja = gson.toJsonTree(list).getAsJsonArray();
       int size = ja.size();
       int keyLen = keys.length;
       JsonObject jo = null;
       Row row = null;
       Cell cell = null;
       String filedName = null;
       String value = null;
       JsonObject temp = null;

       int isDate = 0;
       String formatter = null;
       Date formateDate = null;

       for (int i = 0; i < size; i++) {
//   		row = sheet.createRow(i + 1);
           //row = sheet.getRow(i + 1);
           //if(row == null) {
           row = sheet.createRow(i+rowStart);
           //}
           jo = ja.get(i).getAsJsonObject();
           for (int j = 0; j < keyLen; j++) {
               cell = row.createCell(j);
               if(style != null){
                   cell.setCellStyle(style);
               }
               cell.setCellType(CellType.STRING);
               filedName = keys[j];
               if(jo.has(filedName)){
                   value = jo.get(filedName).getAsString();
                   if(formate.has(filedName)){
                       temp = formate.get(filedName).getAsJsonObject();
                       if(temp.has(value)){
                           value = temp.get(value).getAsString();
                       }else if("-1".equals(value)){
                           value="";
                       }
                       if(temp.has("isDate") && StringUtils.isNotEmpty(value)) {
                           isDate = temp.get("isDate").getAsInt();
                           if(isDate == 1) {
                               if(temp.has("formatter")) {
                                   formatter = temp.get("formatter").getAsString();

                                   formateDate = new SimpleDateFormat(formatter).parse(value);

                                   value = new SimpleDateFormat(formatter).format(formateDate);
                               }
                           }
                       }
                   }
               }else{
                   value = "";
               }
               cell.setCellValue(value);
           }
       }
   }
   private void writeContent(List<?> list, Sheet sheet, String[] keys,CellStyle style, JsonObject formate) throws Exception{
       JsonArray ja = gson.toJsonTree(list).getAsJsonArray();
       int size = ja.size();
       int keyLen = keys.length;
       JsonObject jo = null;
       Row row = null;
       Cell cell = null;
       String filedName = null;
       String value = null;
       JsonObject temp = null;

       int isDate = 0;
       String formatter = null;
       Date formateDate = null;

       for (int i = 0; i < size; i++) {
           row = sheet.createRow(i + 1);
           jo = ja.get(i).getAsJsonObject();
           for (int j = 0; j < keyLen; j++) {
               cell = row.createCell(j);
               if(style != null){
                   cell.setCellStyle(style);
               }
               cell.setCellType(CellType.STRING);
               filedName = keys[j];
               if(jo.has(filedName)){
                   value = jo.get(filedName).getAsString();
                   if(formate.has(filedName)){
                       temp = formate.get(filedName).getAsJsonObject();
                       if(temp.has(value)){
                           value = temp.get(value).getAsString();
                       }else if("-1".equals(value)){
                           value="";
                       }
                       if(temp.has("isDate") && StringUtils.isNotEmpty(value)) {
                           isDate = temp.get("isDate").getAsInt();
                           if(isDate == 1) {
                               if(temp.has("formatter")) {
                                   formatter = temp.get("formatter").getAsString();

                                   formateDate = new SimpleDateFormat(formatter).parse(value);

                                   value = new SimpleDateFormat(formatter).format(formateDate);
                               }
                           }
                       }
                   }
               }else{
                   value = "";
               }
               cell.setCellValue(value);
           }
       }
   }
   private void writeContent(List<?> list, Sheet sheet, String[] keys){
       writeContent(list, sheet, keys, null);
   }

   public static boolean isEmptyRows(Row row,int cellNum){
       if(row == null) return true;
       if(row.getFirstCellNum() == -1) return true;
       Cell cell = null;
       for (int i = 0; i < cellNum; i++) {
           cell = row.getCell(i);
           if(cell != null){
               if(cell != null && cell.getCellTypeEnum() != CellType.BLANK){
                   return false;
               }
           }
       }
       return true;
   }

   public String trim(String str) {
       if (str == "" || str == null) {
           return "";
       }
       int len = str.length();
       int st = 0;
       char[] val = str.toCharArray();

       while ((st < len) && (val[st] == 160)) {
           st++;
       }
       while ((st < len) && (val[len - 1] == 160)) {
           len--;
       }
       return ((st > 0) || (len < str.length())) ? str.substring(st, len) : str;
   }

   /**
    *
    * @Title: getExcelColumnPosition
    * @Description: 获取excel 列位置
    * @param @param i    必须大于 0
    * @return void    返回类型
    */
   public String getExcelColumnPosition(int i,int j){
       if(i <= 0) return null;
       String temp = "";
       String hightLitter = "";
       if( i > 26){
           int m = i / 26;
           int s = i % 26;
           if(s == 0) m = m-1;
           if(m > 0){
               hightLitter = (char)(64 + m) + "";
           }

           if(s == 0) s = 26;
           s += 64;
           temp = (char)s + "";
       }else{
           int n = 64 + i;
           temp = (char) n + "";
       }
       return hightLitter + temp + j;
   }

//   public synchronized static List<List<String>> readXlsb(String xlsbFileName) {
//       OPCPackage pkg;
//       try {
//           pkg = OPCPackage.open(xlsbFileName);
//
//           XSSFBReader r = new XSSFBReader(pkg);
//           XSSFBSharedStringsTable sst = new XSSFBSharedStringsTable(pkg);
//           XSSFBStylesTable xssfbStylesTable = r.getXSSFBStylesTable();
//           XSSFBReader.SheetIterator it = (XSSFBReader.SheetIterator) r.getSheetsData();
//
//           InputStream is = it.next();
//           String name = it.getSheetName();
//
//           XLSB2Lists testSheetHandler = new XLSB2Lists();
//           testSheetHandler.startSheet(name);
//           XSSFBSheetHandler sheetHandler = new XSSFBSheetHandler(is, xssfbStylesTable,
//                   it.getXSSFBSheetComments(),
//                   sst, testSheetHandler,
//                   new DataFormatter(),
//                   false);
//           sheetHandler.parse();
//
//
//
//           // sheet content
//           List list1 = testSheetHandler.getSheetContentAsList();
//
//           is.close();
//           pkg.close();
//           return list1;
//       } catch (InvalidFormatException e) {
//           LoggerUtil.error(OmpFindDataController.class, "读取XLSB error:"+xlsbFileName, e);
//       } catch (IOException e) {
//           LoggerUtil.error(OmpFindDataController.class, "读取XLSB error:"+xlsbFileName, e);
//       } catch (OpenXML4JException e) {
//           LoggerUtil.error(OmpFindDataController.class, "读取XLSB error:"+xlsbFileName, e);
//       } catch (SAXException e) {
//           LoggerUtil.error(OmpFindDataController.class, "读取XLSB error:"+xlsbFileName, e);
//       }finally {
//
//       }
//       return null;
//   }

//   public synchronized static List<List<List<String>>> readAllXlsb(String xlsbFileName) {
//       OPCPackage pkg;
//       try {
//           pkg = OPCPackage.open(xlsbFileName);
//
//           XSSFBReader r = new XSSFBReader(pkg);
//           XSSFBSharedStringsTable sst = new XSSFBSharedStringsTable(pkg);
//           XSSFBStylesTable xssfbStylesTable = r.getXSSFBStylesTable();
//
//           List<List<List<String>>> resultList = new Vector<>();
//           XSSFBReader.SheetIterator it = (XSSFBReader.SheetIterator) r.getSheetsData();
//
//           while(it.hasNext()){XLSB2Lists testSheetHandler = new XLSB2Lists();
//               InputStream is = it.next();
//               testSheetHandler.startSheet(it.getSheetName());
//               XSSFBSheetHandler sheetHandler = new XSSFBSheetHandler(is, xssfbStylesTable,
//                       it.getXSSFBSheetComments(),
//                       sst, testSheetHandler,
//                       new DataFormatter(),
//                       false);
//               sheetHandler.parse();
//
//
//
//               // sheet content
//               List list1 = testSheetHandler.getSheetContentAsList();
//               resultList.add(list1);
//           }
//
//
//           pkg.close();
//           return resultList;
//       } catch (InvalidFormatException e) {
//           LoggerUtil.error(OmpFindDataController.class, "读取XLSB error:"+xlsbFileName, e);
//       } catch (IOException e) {
//           LoggerUtil.error(OmpFindDataController.class, "读取XLSB error:"+xlsbFileName, e);
//       } catch (OpenXML4JException e) {
//           LoggerUtil.error(OmpFindDataController.class, "读取XLSB error:"+xlsbFileName, e);
//       } catch (SAXException e) {
//           LoggerUtil.error(OmpFindDataController.class, "读取XLSB error:"+xlsbFileName, e);
//       }finally {
//
//       }
//       return null;
//   }

   public static CellStyle[] getExportWorkBookStyle(Workbook workbook){
       if(workbook == null){
           return null;
       }
       CellStyle style = workbook.createCellStyle();
       // 设置这些样式
       style.setFillForegroundColor(IndexedColors.TAN.getIndex());
       style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
       style.setBorderBottom(BorderStyle.THIN);
       style.setBorderLeft(BorderStyle.THIN);
       style.setBorderRight(BorderStyle.THIN);
       style.setBorderTop(BorderStyle.THIN);
       style.setAlignment(HorizontalAlignment.CENTER);
       style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
       // 生成一个字体
       Font font = workbook.createFont();
       font.setColor(IndexedColors.VIOLET.getIndex());
       font.setFontHeightInPoints((short) 12);
       font.setBold(true);// 加粗
       // 把字体应用到当前的样式
       style.setFont(font);
       // 生成并设置另一个样式
       CellStyle style2 = workbook.createCellStyle();
       style2.setFillForegroundColor(IndexedColors.WHITE.getIndex());
       style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
       style2.setBorderBottom(BorderStyle.THIN);
       style2.setBorderLeft(BorderStyle.THIN);
       style2.setBorderRight(BorderStyle.THIN);
       style2.setBorderTop(BorderStyle.THIN);
       style2.setAlignment(HorizontalAlignment.CENTER);
       style2.setVerticalAlignment(VerticalAlignment.CENTER);
       // 生成另一个字体
       Font font2 = workbook.createFont();
       font2.setBold(false);// 正常
       return new CellStyle[]{style,style2};
   }


}