package com.lenovo.lejingpin.network;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;

public class ToolKit {
	private ToolKit(){}
	
	public static String convertErrorData(String s) {
		if (s.equals("null") || s.equals("")) {
			return "0";
		}
		return s;
	}

	public static long convertDate(String s) {
		if (s.equals("null") || s.equals("")) {
			return 0;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d1;
		Date d2;
		long m1;
		long m2;
		try {
			d1 = df.parse(s);
			d2 = df.parse("1970-01-01 08:00:00");
			m1 = d1.getTime();
			m2 = d2.getTime();
			return m1 - m2;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;

	}
	 public static String forJSON(String aText){
		    final StringBuilder result = new StringBuilder();
		    StringCharacterIterator iterator = new StringCharacterIterator(aText);
		    char character = iterator.current();
		    while (character != StringCharacterIterator.DONE){
		      if( character == '\"' ){
		        result.append("\\\"");
		      }
		      else if(character == '\\'){
		        result.append("\\\\");
		      }
		      else if(character == '/'){
		        result.append("\\/");
		      }
		      else if(character == '\b'){
		        result.append("\\b");
		      }
		      else if(character == '\f'){
		        result.append("\\f");
		      }
		      else if(character == '\n'){
		        result.append("\\n");
		      }
		      else if(character == '\r'){
		        result.append("\\r");
		      }
		      else if(character == '\t'){
		        result.append("\\t");
		      }
		      else {
		        //the char is not a special one
		        //add it to the result as is
		        result.append(character);
		      }
		      character = iterator.next();
		    }
		    return result.toString();    
		  }

}
