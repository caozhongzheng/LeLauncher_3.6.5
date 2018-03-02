package com.lenovo.lejingpin.hw.lcapackageinstaller.utils;

import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XmlParser {
	
	private XmlParser(){
		
	}
	
	public static Map<String,String> Parse(String text, XmlDefaultHandler handler){
		Map<String,String> data = null;

		try{
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(handler);
		 
			CharArrayReader car = new CharArrayReader(text.toCharArray());
			xr.parse(new InputSource(car));    

			data = handler.getParseData();
			car.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return data;
	}
	
	private static  Map<String,String> Parse(Reader r,XmlDefaultHandler handler ){
		Map<String,String> data = null;
		try{
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(handler);
			
	        xr.parse(new InputSource(r));
	        
			data = handler.getParseData();
			r.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return data;
	}
	
	public static Map<String,String> Parse(InputStream is,XmlDefaultHandler handler){
		InputStreamReader isr = new InputStreamReader(is);
		return Parse(isr,handler);
	}
}
