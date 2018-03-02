package com.lenovo.lejingpin.hw.lcapackageinstaller.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class XmlDefaultHandler extends DefaultHandler{
	public final static String tag = "LcaPackageInstaller";
	
	private String mCurrentTAG;
	private Map<String, String> data;
	private StringBuffer license;
	private ArrayList<String> imei;
	private boolean hasTest;

	public XmlDefaultHandler() {
		data = new HashMap<String, String>();
		imei = new ArrayList<String>();
		hasTest = false;
	}
	
	public Map<String, String> getParseData(){
		return data;
	}
	
	public boolean getHasTest(){
		return hasTest;
	}

	public ArrayList<String> getImei(){
		return imei;
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attr) throws SAXException {
		mCurrentTAG = localName;
		int len = attr.getLength();
		if (localName.equals("License")) {
			license = new StringBuffer();
		} else if (localName.equals("test")) {
			hasTest = true;
		}
		for (int i = 0; i < len; i++) {
			Log.d(tag, String.format("attr(%s) value(%s)", attr.getLocalName(i),attr.getValue(i)));
			data.put(attr.getLocalName(i), attr.getValue(i));
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (localName.equals("License")) {
			data.put(mCurrentTAG, license.toString());
			license = null;
		}
		mCurrentTAG = null;
	}
	
	@Override
	public void characters(char ch[], int start, int length) {
		if (mCurrentTAG.equals("License")) {
			license.append(new String(ch, start, length));
		} else if(mCurrentTAG.equals("did")) {
			imei.add(new String(ch, start, length));
		} else {
			data.put(mCurrentTAG, new String(ch, start, length));
		}
	}
}
