package com.lenovo.launcher2.weather.widget.settings;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;


public class WeatherHandler extends DefaultHandler{
	private static final String FORMAT_DATE_PATTERN = "yyyy MM dd kk mm";
	private List<WeatherDetails> mweatherdetails = new ArrayList<WeatherDetails>();
	private static final String TAG = "WeatherHandler";
	public String mcityId;
	public String mcityName;
	public String mcityStatus;
	public String mcityStatus1;
	public String mcityStatus2;
	public String mcityDirection;
	public String mcityDirection1;
	public String mcityDirection2;
	public String mcityPower;
	public String mcityTemperature;
	public String mcityTemperature1;
	public String mcityTemperature2;
	public String mcityZwx;
	public String mcityZwxL;
	public String mcityZwxS;
	public String mcityKtk;
	public String mcityKtkL;
	public String mcityKtkS;
	public String mcityPollution;
	public String mcityPollutionL;
	public String mcityPollutionS;
	public String mcityXcz;
	public String mcityXczL;
	public String mcityXczS;
	public String mcityChy;
	public String mcityChyL;
	public String mcityLastupdate;
	public String mcityDate;
	
	boolean forecast_conditions = false;
	boolean current_conditions = false;
	boolean current_units = false;

	
	private String preTag = null;
	private int index = -1;
	private final static String TRUE = "True";
	private Context mcontext ;
	private StringBuffer mcurrentValue = new StringBuffer();  
	private String mcityname;
	public WeatherHandler(Context context,List<WeatherDetails>  weatherdetails,String cityname)
	{
		super();
		mcontext = context;
		mweatherdetails = weatherdetails;
		mcityname = cityname;
	}
	public List<WeatherDetails> getForecastWeachers()
	{
		return mweatherdetails;
	}
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		if(qName.equals("item")){
			Log.d("a","mcityStatus="+mcityStatus1);
			if(mcityDirection1!=null){
				if(!mcityDirection1.equals(mcityDirection2))
					mcityDirection = mcityDirection1+"风"+mcityDirection2+"风";
				else
					mcityDirection = mcityDirection1+"风";
			}
        	int temp0 = -1;
        	int temp1 = -1;
            if (mcityTemperature1!=null&&!TextUtils.isEmpty(mcityTemperature1) 
            		&&mcityTemperature2!=null&&!mcityTemperature2.isEmpty()) {
                temp0 = Integer.parseInt(mcityTemperature1);
                temp1 = Integer.parseInt(mcityTemperature2);
                String low = String.valueOf(Math.min(temp0, temp1));
                String high = String.format("%1$d\u00B0", Math.max(temp0, temp1));
                mcityTemperature = low + "~" + high + "C";
            }else if(mcityTemperature1!=null&&!TextUtils.isEmpty(mcityTemperature1)){
            	mcityTemperature = String.format("%1$s\u00B0", mcityTemperature1)+"C";
            }else if(mcityTemperature2!=null&&!TextUtils.isEmpty(mcityTemperature2)){
            	mcityTemperature = String.format("%1$s\u00B0", mcityTemperature2)+"C";
            }
            Log.d("ax","1111mcityname="+mcityname);
            Log.d("ax","222mcityName="+mcityName);
            
            
            if(mcityname.equals(mcityName)){
            	WeatherDetails weatherdetails= new	WeatherDetails(mcityId, mcityName, mcityStatus, 
					mcityStatus1, mcityStatus2, mcityDirection,
					mcityDirection1, mcityDirection2, mcityPower,
					mcityTemperature,mcityTemperature1,mcityTemperature2,
					mcityZwx,mcityZwxL,mcityZwxS,
					mcityKtk,mcityKtkL,mcityKtkS,
					mcityPollution,mcityPollutionL,mcityPollutionS,
					mcityXcz,mcityXczL,mcityXczS,
					mcityChy,mcityChyL, mcityLastupdate,mcityDate);
            	mweatherdetails.add(weatherdetails);
            }
			mcityId = null;
			mcityName = null;
			mcityStatus = null;
			mcityStatus1 = null;
			mcityStatus2 = null;
			mcityDirection1 = null;
			mcityDirection2 = null;
			mcityPower = null;
			mcityTemperature1 = null;
			mcityTemperature2 = null;
			mcityZwx = null;
			mcityZwxL = null;
			mcityZwxS = null;
			mcityKtk = null;
			mcityKtkL = null;
			mcityKtkS = null;
			mcityPollution = null;
			mcityPollutionL = null;
			mcityPollutionS = null;
			mcityXcz = null;
		    mcityXczL = null;
			mcityXczS = null;
			mcityChy = null;
			mcityChyL = null;
			mcityLastupdate = null;
			mcityDate = null;
		}else if(qName.equals("errno")){
			mweatherdetails = null;
		}
	    preTag = null;
	}

	@Override
	public void startDocument() throws SAXException {
		
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		preTag = qName;
		if(qName.equals("root")){
			mweatherdetails.clear();
		}
		super.startElement(uri, localName, qName, attributes);
	}
    @Override  
    public void characters(char[] ch, int start, int length) throws SAXException {  

    	if(preTag!=null){  
    		String content = new String(ch,start,length);  
			if(preTag.equals("date"))
				mcityDate = content;
			else if(preTag.equals("city_id"))
				mcityId = content; 
			else if(preTag.equals("city"))
				mcityName =content;
			else if(preTag.equals("status"))
				mcityStatus = content;
			else if(preTag.equals("status1"))
				mcityStatus1 = content;
			else if(preTag.equals("status2"))
				mcityStatus2 = content;
			else if(preTag.equals("direction1"))
				mcityDirection1 = content; 
			else if(preTag.equals("direction2"))
				mcityDirection2 =content;
			else if(preTag.equals("power"))
				mcityPower = content;
			else if(preTag.equals("temperature1"))
				mcityTemperature1 = content; 
			else if(preTag.equals("temperature2"))
				mcityTemperature2 =content;
			else if(preTag.equals("zwx"))
				mcityZwx = content;
			else if(preTag.equals("zwx_l"))
				mcityZwxL = content;
			else if(preTag.equals("zwx_s"))
				mcityZwxS = content;
   			else if(preTag.equals("ktk"))
				mcityKtk = content;
			else if(preTag.equals("ktk_l"))
				mcityKtkL = content;
			else if(preTag.equals("ktk_s"))
				mcityKtkS = content;
   			else if(preTag.equals("pollution"))
				mcityPollution = content;
			else if(preTag.equals("pollution_l"))
				mcityPollutionL = content;
			else if(preTag.equals("pollution_s"))
				mcityPollutionS = content;
   			else if(preTag.equals("xcz"))
				mcityXcz = content;
			else if(preTag.equals("xcz_l"))
				mcityXczL = content;
			else if(preTag.equals("xcz_s"))
				mcityXczS = content;
   			else if(preTag.equals("chy"))
				mcityChy = content;
			else if(preTag.equals("chy_l"))
				mcityChyL = content;
			else if(preTag.equals("last_update"))
				mcityLastupdate = content;
    	} 
    }

}
