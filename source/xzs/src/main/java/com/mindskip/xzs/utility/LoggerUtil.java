package com.mindskip.xzs.utility;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.xml.sax.SAXException;

import com.mindskip.xzs.utility.poi.XLSXCovertCSVReader;

public class LoggerUtil {
	public static void error(Class<?> T, String error, Exception e2) {
		System.out.println(error);
	}
	public static void error(Class<?> T, String error) {
		System.out.println(error);
	}
	public static void info(Class<?> T, String error) {
		System.out.println(error);
	}
	
	public static void main(String[] args) {
		try {
			List<List<String[]>> allSheets = XLSXCovertCSVReader.readerAllSheetExcel("C:\\Users\\Administrator\\Desktop\\xzs.xlsx");
			for (List<String[]> singleSheet : allSheets) {
				for (String[] rows : singleSheet) {
					StringBuffer entityT=new StringBuffer();
					int i=1;
					for (String col : rows) {
						entityT.append(i+"=["+col+"]");
						i++;
					}
					System.out.println(entityT.toString());
				}
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
