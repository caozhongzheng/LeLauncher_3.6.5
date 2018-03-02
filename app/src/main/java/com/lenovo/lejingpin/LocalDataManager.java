package com.lenovo.lejingpin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.NetworkStats.Entry;
import android.os.Environment;
import android.renderscript.Element;
import android.util.Log;


public class LocalDataManager {
    static final String TAG = "LocalDataManager";
    static LocalDataManager mLocalDataMagager;
    final String mLocalDataSubPath = "/.IdeaDesktop/LeJingpin/xml";
    private Context mContext;
    
    public static synchronized LocalDataManager getInstance(Context context) {
        if ( mLocalDataMagager == null) {
        	mLocalDataMagager = new LocalDataManager(context);
        }
        return mLocalDataMagager;
    }
    public LocalDataManager(Context context) {
    	mContext = context;
    }
    
    
    public static boolean isSDCardReady(){
    	String sdcardState = android.os.Environment.getExternalStorageState();
    	if(sdcardState.equalsIgnoreCase(Environment.MEDIA_MOUNTED)){
    		return true;
    	}else{
    		return false;
    	}
    }

    private String getLocalDataPath(String filename){
    	String path = Environment.getExternalStorageDirectory() + mLocalDataSubPath;
    	return path + File.separator + filename + ".xml";
    }
    
    /**
     * writeDataToSDCard
     * @param spName: the name of sharedPreference; filename: the file to backup
     * @return true for sucess, false for fail.
     */
    public boolean writeDataToSDCard(String spName, String filename){
    	if(isSDCardReady()){
			String spath = Environment.getExternalStorageDirectory() + mLocalDataSubPath;
			File path = new File(spath);
			if (!path.exists()){
				path.mkdirs();// 创建一个目录
			}
    		File file = new File(getLocalDataPath(filename));
    		if(!file.exists()){
	    		try {
					file.createNewFile();
	    		}catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
	    			Log.e(TAG,"FileNotFoundException: "+e.getMessage());
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(TAG,"IOException: "+e.getMessage());
					e.printStackTrace();
					return false;
				}
    		}
    		boolean res = saveSharedPreferencesToFile(spName, file);
			Log.e(TAG,"saveSharedPreferencesToFile return "+res);
   		    return res;
    	}
    	return false;
    }
    
 
    private boolean saveSharedPreferencesToFile(String spName, File dst) {
    	Log.i(TAG, "saveSharedPreferencesToFile, spName = "+spName);
        boolean res = false;
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(dst));
            SharedPreferences pref = mContext.getSharedPreferences(spName, Context.MODE_PRIVATE);
            output.writeObject(pref.getAll());
            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                res = false;
            }
        }
        return res;
    }

 /*   public HashMap<String, String> readDataFromSDCard(String filename){
    	if(!isSDCardReady())
    		return null;
    	File file = new File(getLocalDataPath(filename));
    	if (file.exists()){
    		try {
				FileInputStream fis = new FileInputStream(file);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            DocumentBuilder dbu = null;
	            try {
	                dbu = dbf.newDocumentBuilder();
	            } catch (ParserConfigurationException pce) {
	            	Log.e(TAG,"newDocumentBuilder error: "+pce.toString());
	                return null;
	            }
	            Document doc = null;
	            try {
	                doc = dbu.parse(fis);
	            } catch (Exception e) {
	                e.printStackTrace();
	                return null;
	            }
	            mDataMap.clear();

	            NodeList nodelist =doc.getElementsByTagName("string");
	            for(int i = 0; i < nodelist.getLength(); i++){
	            	Node node = nodelist.item(i);
	            	String name = node.getNodeName();
	            	String value = node.getNodeValue();
	            	mDataMap.put(name, value);
	            }
	            return mDataMap;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
    		
        }else{
        	return null;
        }
    }*/
    

    @SuppressWarnings("unchecked")
	public boolean loadSharedPreferencesFromFile(String spName, String filename) {
    	Log.i(TAG, "loadSharedPreferencesFromFile: "+ spName +", "+filename);
    	if(!isSDCardReady())
    		return false;
    	File file = new File(getLocalDataPath(filename));
		if (file.exists()) {
			boolean res = false;
			ObjectInputStream input = null;
			try {
				input = new ObjectInputStream(new FileInputStream(file));
				Editor prefEdit = mContext.getSharedPreferences(spName,
						Context.MODE_PRIVATE).edit();
				prefEdit.clear();
				Map<String, ?> entries = (Map<String, ?>)input.readObject();
				for (java.util.Map.Entry<String, ?> entry : entries.entrySet()) {
					Object v = entry.getValue();
					String key = entry.getKey();
					if (v instanceof Boolean)
						prefEdit.putBoolean(key, ((Boolean) v).booleanValue());
					else if (v instanceof Float)
						prefEdit.putFloat(key, ((Float) v).floatValue());
					else if (v instanceof Integer)
						prefEdit.putInt(key, ((Integer) v).intValue());
					else if (v instanceof Long)
						prefEdit.putLong(key, ((Long) v).longValue());
					else if (v instanceof String)
						prefEdit.putString(key, ((String) v));
				}
				prefEdit.commit();
				res = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (input != null) {
						input.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			return res;
		}
		return false;
    }
    
}
