package com.mindskip.xzs.utility.poi;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
 
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.mindskip.xzs.utility.LoggerUtil;
 
/**
 * 使用CVS模式解决XLSX文件，可以有效解决用户模式内存溢出的问题
 * 该模式是POI官方推荐的读取大数据的模式，在用户模式下，数据量较大、Sheet较多、或者是有很多无用的空行的情况
 * ，容易出现内存溢出,用户模式读取Excel的典型代码如下： FileInputStream file=new
 * FileInputStream("c:\\test.xlsx"); Workbook wb=new XSSFWorkbook(file);
 *
 *
 */
public class XLSXCovertCSVReader {
 
	/**
	 * The type of the data value is indicated by an attribute on the cell. The
	 * value is usually in a "v" element within the cell.
	 */
	enum xssfDataType {
		BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER,
	}
 
	/**
	 * 使用xssf_sax_API处理Excel,请参考： http://poi.apache.org/spreadsheet/how-to.html#xssf_sax_api
	 * <p/>
	 * Also see Standard ECMA-376, 1st edition, part 4, pages 1928ff, at
	 * http://www.ecma-international.org/publications/standards/Ecma-376.htm
	 * <p/>
	 * A web-friendly version is http://openiso.org/Ecma/376/Part4
	 */
	class MyXSSFSheetHandler extends DefaultHandler {
 
		/**
		 * Table with styles
		 */
		private StylesTable stylesTable;
 
		/**
		 * Table with unique strings
		 */
		private ReadOnlySharedStringsTable sharedStringsTable;
 
		/**
		 * Destination for data
		 */
		private final PrintStream output;
 
		/**
		 * Number of columns to read starting with leftmost
		 */
		private final int minColumnCount;
 
		// Set when V start element is seen
		private boolean vIsOpen;
 
		// Set when cell start element is seen;
		// used when cell close element is seen.
		private xssfDataType nextDataType;
 
		// Used to format numeric cell values.
		private short formatIndex;
		private String formatString;
		private final DataFormatter formatter;
 
		private int thisColumn = -1;
		private int thisRowNum = -1;
		// The last column printed to the output stream
		private int lastColumnNumber = -1;
 
		private boolean isSameColCountFlag = true;
 
		// Gathers characters as they are seen.
		private StringBuffer value;
		private String[] record;
		private List<String> recordList;
		private List<String[]> rows = new Vector<String[]>();
		private boolean isCellNull = false;
 
 
		/**
		 * Accepts objects needed while parsing.
		 *
		 * @param styles
		 *            Table of styles
		 * @param strings
		 *            Table of shared strings
		 * @param cols
		 *            Minimum number of columns to show
		 * @param target
		 *            Sink for output
		 */
		public MyXSSFSheetHandler(StylesTable styles,
				ReadOnlySharedStringsTable strings, int cols, PrintStream target) {
			this.stylesTable = styles;
			this.sharedStringsTable = strings;
			this.minColumnCount = cols;
			this.output = target;
			this.value = new StringBuffer();
			this.nextDataType = xssfDataType.SSTINDEX;
			this.formatter = new DataFormatter();
			record = new String[this.minColumnCount];
			rows.clear();// 每次读取都清空行集合
			thisRowNum = -1;
			recordList = new Vector<>();
			isSameColCountFlag = true;
		}
		public MyXSSFSheetHandler(StylesTable styles,
								  ReadOnlySharedStringsTable strings, int cols, PrintStream target,boolean isSameColCountFlag) {
			this.stylesTable = styles;
			this.sharedStringsTable = strings;
			this.minColumnCount = cols;
			this.output = target;
			this.value = new StringBuffer();
			this.nextDataType = xssfDataType.SSTINDEX;
			this.formatter = new DataFormatter();
			rows.clear();// 每次读取都清空行集合
			thisRowNum = -1;
			recordList = new Vector<>();
			this.isSameColCountFlag = isSameColCountFlag;
		}
 
		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			try {
 
 
			if ("inlineStr".equals(name) || "v".equals(name)|| "is".equals(name)) {
 
					vIsOpen = true;
 
 
				// Clear contents cache
				value.setLength(0);
			}
			// c => cell
			else if ("c".equals(name)) {
				// Get the cell reference
				String r = attributes.getValue("r");
				int firstDigit = -1;
				for (int c = 0; c < r.length(); ++c) {
					if (Character.isDigit(r.charAt(c))) {
						firstDigit = c;
						break;
					}
				}
				thisColumn = nameToColumn(r.substring(0, firstDigit));
 
				if(!isSameColCountFlag){
					thisRowNum = numToColumn(r);
				}
 
				// Set up defaults.
				this.nextDataType = xssfDataType.NUMBER;
				this.formatIndex = -1;
				this.formatString = null;
				String cellType = attributes.getValue("t");
				String cellStyleStr = attributes.getValue("s");
				if ("b".equals(cellType))
					nextDataType = xssfDataType.BOOL;
				else if ("e".equals(cellType))
					nextDataType = xssfDataType.ERROR;
				else if ("inlineStr".equals(cellType))
					nextDataType = xssfDataType.INLINESTR;
				else if ("s".equals(cellType))
					nextDataType = xssfDataType.SSTINDEX;
				else if ("str".equals(cellType))
					nextDataType = xssfDataType.FORMULA;
				else if (cellStyleStr != null) {
					// It's a number, but almost certainly one
					// with a special style or format
					int styleIndex = Integer.parseInt(cellStyleStr);
					XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
					this.formatIndex = style.getDataFormat();
					this.formatString = style.getDataFormatString();
					if (this.formatString == null)
						this.formatString = BuiltinFormats
								.getBuiltinFormat(this.formatIndex);
				}
			}
 
			} catch (Exception e) {
				LoggerUtil.error(this.getClass(), "error",e);
 
			}
		}
 
		/*
		 * (non-Javadoc)
		 *
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
		 * java.lang.String, java.lang.String)
		 */
		public void endElement(String uri, String localName, String name)
				throws SAXException {
 
			String thisStr = null;
			try {
 
 
			// v => contents of a cell
			if ("v".equals(name)||"is".equals(name)) {
				// Process the value contents as required.
				// Do now, as characters() may be called more than once
				switch (nextDataType) {
 
				case BOOL:
					char first = value.charAt(0);
					thisStr = first == '0' ? "FALSE" : "TRUE";
					break;
 
				case ERROR:
					thisStr = "\"ERROR:" + value.toString() + '"';
					break;
 
				case FORMULA:
					// A formula could result in a string value,
					// so always add double-quote characters.
					thisStr = '"' + value.toString() + '"';
					break;
 
				case INLINESTR:
					// TODO: have seen an example of this, so it's untested.
					XSSFRichTextString rtsi = new XSSFRichTextString(
							value.toString());
					thisStr = rtsi.toString() ;
					break;
 
				case SSTINDEX:
					String sstIndex = value.toString();
					try {
						int idx = Integer.parseInt(sstIndex);
						XSSFRichTextString rtss = new XSSFRichTextString(
								sharedStringsTable.getEntryAt(idx));
						thisStr =  rtss.toString();
					} catch (NumberFormatException ex) {
						output.println("Failed to parse SST index '" + sstIndex
								+ "': " + ex.toString());
					}
					break;
 
				case NUMBER:
					String n = value.toString();
					// 判断是否是日期格式
					if (HSSFDateUtil.isADateFormat(this.formatIndex, n)) {
						Double d = Double.parseDouble(n);
						Date date=HSSFDateUtil.getJavaDate(d);
						thisStr=formateDateToString(date);
					} else if (this.formatString != null)
						thisStr = formatter.formatRawCellContents(
								Double.parseDouble(n), this.formatIndex,
								this.formatString);
					else
						thisStr = n;
					break;
 
				default:
					thisStr = "(TODO: Unexpected type: " + nextDataType + ")";
					break;
				}
 
				// Output after we've seen the string contents
				// Emit commas for any fields that were missing on this row
				if (lastColumnNumber == -1) {
					lastColumnNumber = 0;
				}
				//判断单元格的值是否为空
				if (thisStr == null || "".equals(isCellNull)) {
					isCellNull = true;// 设置单元格是否为空值
				}
 
				if(!isSameColCountFlag){
					if(thisRowNum == 1){
						recordList.add(thisStr);
					}else{
						// 当前行若大于 指定行长度时则提示
						if(thisColumn >=minColumns) {
							//System.out.println("当前列长度("+thisColumn+") 大于指定列长度("+minColumns+")");
							return;
						}
						record[thisColumn] = thisStr;
 
						// Update column
						if (thisColumn > -1)
							lastColumnNumber = thisColumn;
					}
 
				}else{
					// 当前行若大于 指定行长度时则提示
					if(thisColumn >=minColumns) {
						//System.out.println("当前列长度("+thisColumn+") 大于指定列长度("+minColumns+")");
						return;
					}
					record[thisColumn] = thisStr;
 
					// Update column
					if (thisColumn > -1)
						lastColumnNumber = thisColumn;
				}
 
 
 
			} else if ("row".equals(name)) {
				if(!isSameColCountFlag){
					if(thisRowNum == 1){
						minColumns = recordList.size();
						String [] tempArray = new String[minColumns];
						record =recordList.toArray(tempArray);
					}
 
					// Print out any missing commas if needed
					if (minColumns > 0) {
						// Columns are 0 based
						if (lastColumnNumber == -1) {
							lastColumnNumber = 0;
						}
						if (isCellNull == false && record[primaryIndex] != null)// 判断是否空行
						{
							rows.add(record.clone());
							isCellNull = false;
							for (int i = 0; i < minColumns; i++) {
								record[i] = null;
							}
						}
					}
					lastColumnNumber = -1;
				}else{
					// Print out any missing commas if needed
					if (minColumns > 0) {
						// Columns are 0 based
						if (lastColumnNumber == -1) {
							lastColumnNumber = 0;
						}
						if (isCellNull == false && record[primaryIndex] != null)// 判断是否空行
						{
							rows.add(record.clone());
							isCellNull = false;
							for (int i = 0; i < record.length; i++) {
								record[i] = null;
							}
						}
					}
					lastColumnNumber = -1;
				}
			}
 
			} catch (Exception e) {
				LoggerUtil.error(this.getClass(), "error",e);
				LoggerUtil.info(this.getClass(), "name:"+name + "  nextDataType="+nextDataType + "  value"+thisStr);
 
			}
		}
 
		public List<String[]> getRows() {
			return rows;
		}
 
		public void setRows(List<String[]> rows) {
			this.rows = rows;
		}
 
		/**
		 * Captures characters only if a suitable element is open. Originally
		 * was just "v"; extended for inlineStr also.
		 */
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (vIsOpen)
				value.append(ch, start, length);
		}
 
		/**
		 * Converts an Excel column name like "C" to a zero-based index.
		 *
		 * @param name
		 * @return Index corresponding to the specified name
		 */
		private int nameToColumn(String name) {
			int column = -1;
			for (int i = 0; i < name.length(); ++i) {
				int c = name.charAt(i);
				column = (column + 1) * 26 + c - 'A';
			}
			return column;
		}
 
		private int numToColumn(String name){
			if(name == null){
				return -1;
			}
			String regEx="[^0-9]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(name);
			String str = m.replaceAll("").trim();
			if(StringUtils.isEmpty(str)){
				return -1;
			}
			return Integer.valueOf(str);
		}
 
 
		private String formateDateToString(Date date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化日期
			return sdf.format(date);
 
		}
 
	}
 
	// /
 
	private OPCPackage xlsxPackage;
	private int minColumns;
	private PrintStream output;
	private String sheetName;
	private int sheetIndex;
	private int primaryIndex;
	/**
	 * Creates a new XLSX -> CSV converter
	 *
	 * @param pkg
	 *            The XLSX package to process
	 * @param output
	 *            The PrintStream to output the CSV to
	 * @param minColumns
	 *            The minimum number of columns to output, or -1 for no minimum
	 */
	public XLSXCovertCSVReader(OPCPackage pkg, PrintStream output,
			String sheetName, int minColumns) {
		this.xlsxPackage = pkg;
		this.output = output;
		this.minColumns = minColumns;
		this.sheetName = sheetName;
	}
 
	public XLSXCovertCSVReader(OPCPackage pkg, PrintStream output,
			String sheetName, int minColumns, int primaryIndex) {
		this.xlsxPackage = pkg;
		this.output = output;
		this.minColumns = minColumns;
		this.sheetName = sheetName;
		this.primaryIndex = primaryIndex;
	}
 
 
 
	public XLSXCovertCSVReader(OPCPackage pkg, PrintStream output,
			int sheetIndex, int minColumns) {
		this.xlsxPackage = pkg;
		this.output = output;
		this.minColumns = minColumns;
		this.sheetIndex = sheetIndex;
	}
 
	public XLSXCovertCSVReader(OPCPackage pkg, PrintStream output,
			int sheetIndex, int minColumns, int primaryIndex) {
		this.xlsxPackage = pkg;
		this.output = output;
		this.minColumns = minColumns;
		this.sheetIndex = sheetIndex;
		this.primaryIndex = primaryIndex;
	}
 
 
 
 
 
	/**
	 * Parses and shows the content of one sheet using the specified styles and
	 * shared-strings tables.
	 *
	 * @param styles
	 * @param strings
	 * @param sheetInputStream
	 */
	public List<String[]> processSheet(StylesTable styles,
			ReadOnlySharedStringsTable strings, InputStream sheetInputStream)
			throws IOException, ParserConfigurationException, SAXException {
 
		InputSource sheetSource = new InputSource(sheetInputStream);
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxFactory.newSAXParser();
		XMLReader sheetParser = saxParser.getXMLReader();
		MyXSSFSheetHandler handler = new MyXSSFSheetHandler(styles, strings,
				this.minColumns, this.output);
		sheetParser.setContentHandler(handler);
		sheetParser.parse(sheetSource);
		return handler.getRows();
	}
 
	/**
	 * Parses and shows the content of one sheet using the specified styles and
	 * shared-strings tables.
	 *
	 * @param styles
	 * @param strings
	 * @param sheetInputStream
	 */
	public List<String[]> processSheet(StylesTable styles,
									   ReadOnlySharedStringsTable strings, InputStream sheetInputStream,boolean isSameFlag)
			throws IOException, ParserConfigurationException, SAXException {
 
		InputSource sheetSource = new InputSource(sheetInputStream);
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxFactory.newSAXParser();
		XMLReader sheetParser = saxParser.getXMLReader();
		MyXSSFSheetHandler handler = new MyXSSFSheetHandler(styles, strings,
				this.minColumns, this.output,isSameFlag);
		sheetParser.setContentHandler(handler);
		sheetParser.parse(sheetSource);
		return handler.getRows();
	}
 
	/**
	 * 初始化这个处理程序 将
	 *
	 * @throws IOException
	 * @throws OpenXML4JException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public List<String[]> process() throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
 
		ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(
				this.xlsxPackage);
		XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
		List<String[]> list = null;
		StylesTable styles = xssfReader.getStylesTable();
		XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader
				.getSheetsData();
		int index = 0;
		while (iter.hasNext()) {
			InputStream stream = iter.next();
			String sheetNameTemp = iter.getSheetName();
			if (this.sheetName.equals(sheetNameTemp)) {
				list = processSheet(styles, strings, stream);
				stream.close();
				++index;
			}
		}
		return list;
	}
 
	public List<String[]> processIndex() throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
 
		ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(
				this.xlsxPackage);
		XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
 
		List<String[]> list = null;
		StylesTable styles = xssfReader.getStylesTable();
		XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader
				.getSheetsData();
 
		int index = 0;
		while (iter.hasNext()) {
			if(index == sheetIndex) {
				InputStream stream = iter.next();
				list = processSheet(styles, strings, stream);
				stream.close();
			}
			if(index == 0 && (list ==null  || list.size()==0)) {
				InputStream stream = iter.next();
				list = processSheet(styles, strings, stream);
				stream.close();
			}else {
				break;
			}
			++index;
		}
		return list;
	}
 
	public List<List<String[]>> processAllSheet() throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
 
 
		ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(
				this.xlsxPackage);
 
		XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
		SharedStringsTable sst = xssfReader.getSharedStringsTable();
		List<List<String[]>> result = new Vector<>();
		StylesTable styles = xssfReader.getStylesTable();
		XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader
				.getSheetsData();
 
 
		while (iter.hasNext()) {
 
				InputStream stream = iter.next();
			List<String[]> list = processSheet(styles, strings, stream);
			if(list != null && list.size() >0){
				result.add(list);
			}
				stream.close();
		}
		return result;
	}
 
	public List<List<String[]>> processAllSheet(boolean flag) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
 
 
		ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(
				this.xlsxPackage);
 
		XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
		SharedStringsTable sst = xssfReader.getSharedStringsTable();
		List<List<String[]>> result = new Vector<>();
		StylesTable styles = xssfReader.getStylesTable();
		XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader
				.getSheetsData();
 
 
		while (iter.hasNext()) {
			InputStream stream = iter.next();
			List<String[]> list = processSheet(styles, strings, stream,flag);
			if(list != null && list.size() >0){
				result.add(list);
			}
			stream.close();
		}
		return result;
	}
 
	public Map<String,List<String[]>> processAllSheetByMap() throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
 
 
		ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(
				this.xlsxPackage);
 
		XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
		SharedStringsTable sst = xssfReader.getSharedStringsTable();
		Map<String,List<String[]>> result = new HashMap<>();
		StylesTable styles = xssfReader.getStylesTable();
		XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader
				.getSheetsData();
 
 
		while (iter.hasNext()) {
 
			InputStream stream = iter.next();
			String sheetName = iter.getSheetName();
			List<String[]> list = processSheet(styles, strings, stream);
			if(list != null && list.size() >0){
				result.put(sheetName,list);
			}
			stream.close();
		}
		return result;
	}
 
	public Map<String,List<String[]>> processAllSheetByMap(boolean flag) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
 
 
		ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(
				this.xlsxPackage);
 
		XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
		SharedStringsTable sst = xssfReader.getSharedStringsTable();
		Map<String,List<String[]>> result = new HashMap<>();
		StylesTable styles = xssfReader.getStylesTable();
		XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader
				.getSheetsData();
 
 
		while (iter.hasNext()) {
			InputStream stream = iter.next();
			String sheetName = iter.getSheetName();
			List<String[]> list = processSheet(styles, strings, stream,flag);
			if(list != null && list.size() >0){
				result.put(sheetName,list);
			}
			stream.close();
		}
		return result;
	}
 
	/**
	 * 读取Excel
	 *
	 * @param path
	 *            文件路径
	 * @param sheetName
	 *            sheet名称
	 * @param minColumns
	 *            列总数
	 * @return
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws OpenXML4JException
	 * @throws IOException
	 */
	public static List<String[]> readerExcel(String path, String sheetName,
			int minColumns) throws Exception {
		return readerExcel(path, sheetName, minColumns,0);
	}
 
	public static List<String[]> readerExcel(String path, String sheetName,
			int minColumns,int avalibleColumns) throws Exception {
 
		OPCPackage p = OPCPackage.open(path, PackageAccess.READ);
		XLSXCovertCSVReader xlsx2csv = new XLSXCovertCSVReader(p, System.out,
				sheetName, minColumns, avalibleColumns);
		List<String[]> list = xlsx2csv.process();
		p.close();
		return list;
	}
 
 
	public static List<String[]> readerExcel(String path, int sheetName,
			int minColumns) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		return readerExcel(path, sheetName, minColumns,0);
	}
 
	public static List<String[]> readerExcel(String path, int sheetName,
			int minColumns,int avalibleColumns) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		OPCPackage p = OPCPackage.open(path, PackageAccess.READ);
		XLSXCovertCSVReader xlsx2csv = new XLSXCovertCSVReader(p, System.out,
				sheetName, minColumns,avalibleColumns);
		List<String[]> list = xlsx2csv.processIndex();
		p.close();
		return list;
	}
 
	public static List<List<String[]>> readerAllSameSheetExcel(String path,
											 int minColumns) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		return readerAllSameSheetExcel(path, minColumns,0);
	}
 
	public static List<List<String[]>> readerAllSameSheetExcel(String path, int minColumns,int avalibleColumns) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		OPCPackage p = OPCPackage.open(path, PackageAccess.READ);
		XLSXCovertCSVReader xlsx2csv = new XLSXCovertCSVReader(p, System.out,
				-1, minColumns,avalibleColumns);
		List<List<String[]>> list = xlsx2csv.processAllSheet();
		p.close();
		return list;
	}
 
	public static List<List<String[]>> readerAllSheetExcel(String path,int avalibleColumns) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		OPCPackage p = OPCPackage.open(path, PackageAccess.READ);
		XLSXCovertCSVReader xlsx2csv = new XLSXCovertCSVReader(p, System.out,
				-1, 0,avalibleColumns);
		List<List<String[]>> list = xlsx2csv.processAllSheet(false);
		p.close();
		return list;
	}
	public static Map<String,List<String[]>> readerAllSheetExcelByMap(String path,int avalibleColumns) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		OPCPackage p = OPCPackage.open(path, PackageAccess.READ);
		XLSXCovertCSVReader xlsx2csv = new XLSXCovertCSVReader(p, System.out,
				-1, 0,avalibleColumns);
		Map<String,List<String[]>> list = xlsx2csv.processAllSheetByMap(false);
		p.close();
		return list;
	}
 
	public static List<List<String[]>> readerAllSheetExcel(String path) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		return readerAllSheetExcel(path,0);
	}
 
	public static Map<String,List<String[]>> readerAllSheetExcelByMap(String path) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		return readerAllSheetExcelByMap(path,0);
	}
 
	public static List<List<String[]>> readerAllSheetExcel(InputStream path) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		return readerAllSheetExcel(path,0);
	}
 
	public static Map<String,List<String[]>> readerAllSheetExcelByMap(InputStream path) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		return readerAllSheetExcelByMap(path,0);
	}
 
	public static List<List<String[]>> readerAllSheetExcel(InputStream path,int avalibleColumns) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		OPCPackage p = OPCPackage.open(path);
		XLSXCovertCSVReader xlsx2csv = new XLSXCovertCSVReader(p, System.out,
				-1, 0,avalibleColumns);
		List<List<String[]>> list = xlsx2csv.processAllSheet(false);
		p.close();
		return list;
	}
 
	public static Map<String,List<String[]>> readerAllSheetExcelByMap(InputStream path,int avalibleColumns) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		OPCPackage p = OPCPackage.open(path);
		XLSXCovertCSVReader xlsx2csv = new XLSXCovertCSVReader(p, System.out,
				-1, 0,avalibleColumns);
		Map<String,List<String[]>> list = xlsx2csv.processAllSheetByMap(false);
		p.close();
		return list;
	}
 
	public static List<String[]> readerExcel(InputStream path, int sheetName,
			int minColumns) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		return readerExcel(path, sheetName, minColumns,0);
	}
 
	public static List<String[]> readerExcel(InputStream path, int sheetName,
			int minColumns,int avalibleColumns) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		OPCPackage p = OPCPackage.open(path);
		XLSXCovertCSVReader xlsx2csv = new XLSXCovertCSVReader(p, System.out,
				sheetName, minColumns, avalibleColumns);
		List<String[]> list = xlsx2csv.processIndex();
		p.close();
		return list;
	}
 
	public static List<String[]> readerExcel(InputStream path, String sheetName,
			int minColumns) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		return readerExcel(path, sheetName, minColumns,0);
	}
 
	public static List<String[]> readerExcel(InputStream path, String sheetName,
			int minColumns,int avalibleColumns) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException {
		OPCPackage p = OPCPackage.open(path);
		XLSXCovertCSVReader xlsx2csv = new XLSXCovertCSVReader(p, System.out,
				sheetName, minColumns,avalibleColumns);
		List<String[]> list = xlsx2csv.process();
		p.close();
		return list;
	}
 
	/**
	 *  复制样式
	 * @param fromStyle
	 * @param toStyle
	 */
	public static void copyCellStyle(CellStyle fromStyle,CellStyle toStyle){
		try {
			toStyle.cloneStyleFrom(fromStyle);
		}catch (Exception e){
 
		}
 
	}
 
	/**
	 * 复制合并单元格
	 * @param fromSheet
	 * @param toSheet
	 */
	public static void mergeSheetAllRegion(Sheet fromSheet, Sheet toSheet){
		int num = fromSheet.getNumMergedRegions();
		CellRangeAddress cellR = null;
		for (int i = 0; i < num; i++){
			cellR = fromSheet.getMergedRegion(i);
			toSheet.addMergedRegion(cellR);
		}
	}
 
	/**
	 * 复制单元格
	 * @param wb
	 * @param fromCell
	 * @param toCell
	 */
	public static void copyCell(Workbook wb,Cell fromCell,Cell toCell){
		CellStyle newstyle = wb.createCellStyle();
		copyCellStyle(fromCell.getCellStyle(),newstyle);
		toCell.setCellStyle(newstyle);
		if(fromCell.getCellComment() != null){
			toCell.setCellComment(fromCell.getCellComment());
		}
 
		CellType fromCellType = fromCell.getCellTypeEnum();
		toCell.setCellType(fromCellType);
		if(fromCellType == CellType.NUMERIC){
			if(DateUtil.isCellDateFormatted(fromCell)){
				toCell.setCellValue(fromCell.getDateCellValue());
			}else{
				toCell.setCellValue(fromCell.getNumericCellValue());
			}
		}else if(fromCellType == CellType.STRING){
			toCell.setCellValue(fromCell.getRichStringCellValue());
		}else if(fromCellType == CellType.BLANK){
		}else if(fromCellType == CellType.BOOLEAN){
			toCell.setCellValue(fromCell.getBooleanCellValue());
		}else if(fromCellType == CellType.ERROR){
			toCell.setCellValue(fromCell.getErrorCellValue());
		}else if(fromCellType == CellType.FORMULA){
			toCell.setCellValue(fromCell.getCellFormula());
		}else{
		}
	}
 
	/**
	 * 复制行
	 * @param wb
	 * @param fromRow
	 * @param toRow
	 */
	public static void copyRow(Workbook wb,Row fromRow,Row toRow){
		toRow.setHeight(fromRow.getHeight());
		for(Iterator cellIt = fromRow.cellIterator();cellIt.hasNext();){
			Cell tmpCell = (Cell) cellIt.next();
			Cell newCell = toRow.createCell(tmpCell.getColumnIndex());
			copyCell(wb,tmpCell,newCell);
		}
	}
 
	/**
	 * 复制 sheet
	 * @param wb
	 * @param fromSheet
	 * @param toSheet
	 */
	public static void copySheet(Workbook wb, Sheet fromSheet, Sheet toSheet){
		mergeSheetAllRegion(fromSheet,toSheet);
		int length = fromSheet.getRow(fromSheet.getFirstRowNum()).getLastCellNum();
		for (int i = 0; i <= length; i++){
			toSheet.setColumnWidth(i,fromSheet.getColumnWidth(i));
		}
		for(Iterator rowIt = fromSheet.rowIterator();rowIt.hasNext();){
			Row fromRow = (Row) rowIt.next();
			Row newRow = toSheet.createRow(fromRow.getRowNum());
			copyRow(wb,fromRow,newRow);
		}
	}
}