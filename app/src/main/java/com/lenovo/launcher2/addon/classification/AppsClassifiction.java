package com.lenovo.launcher2.addon.classification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.lenovo.launcher.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * 该抽象类封装了智能分类动作的所有逻辑操作，如需进行分类，请在UI线程中（可能是launcher） 实现该抽象类的所有接口
 * 
 * @author gecn1
 *
 * @param <T> T 为container 类型 在launcher中为FolderIcon
 */
public abstract class AppsClassifiction<T> {
    private AppsClassificationData mData;
    protected Context mContext;
    public final static String TAG="AppsClassifiction";
    public final static boolean DEBUG=true;
    private int mCellCount;
    Map<String,T> existedFolderName = new LinkedHashMap<String,T>();
    Map<String,T> unExistedFolderName = new LinkedHashMap<String,T>();
    Map<String, ArrayList<String>> appsCategory;
    private AppCategoryBehavior mAppCategoryBehavior;
    
    
    //UI操作
    private Handler mHandler  = new Handler(){
        @Override  
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                T FolderIcon = (T)msg.obj;
                addCategoryAppsToContainer( FolderIcon, msg.getData().getStringArrayList("package"));
            }else if(msg.what ==1){
                if(msg.arg2 ==1){
                    Toast.makeText(mContext, R.string.classfication_no_more_space, Toast.LENGTH_SHORT).show();
                }else{
                    createContainerAndAddCateoryApps(msg.getData().getString("folderName"), 
                            msg.getData().getStringArrayList("package"),msg.arg1);
                }
                
            }else if(msg.what == 2){
                recoveryAfterShow();
            }else if(msg.what == 3){
                boolean isAddNewScreen = false;
                int realAddScreenNums =0 ;//实际添加的屏幕数小于等于需要添加的屏幕数
                int screen =-1;
                if(msg.arg1 ==0){
                    isAddNewScreen = false;
                    screen = msg.arg2;
                    
                }else{
                    isAddNewScreen = true;
                    realAddScreenNums = addNewScreen(msg.arg1);
                }
                
                int screenNums = getWorkSpaceScreenNums();
                int addScreenNums = realAddScreenNums;//msg.arg1;

                CreatContainerRunnable r = new CreatContainerRunnable(screenNums,addScreenNums,isAddNewScreen,screen);
                new Thread(r).start();
                
            }
        }
        
    };
    
    
    public  AppsClassifiction(Context c,AppsClassificationData data){
        mData = data;
        mContext = c;
    }
    
    public AppsClassifiction(Context c){
        mData = AppsClassificaionDataFactory.getAppsClassificationData(c, AppsClassificaionDataFactory.DATA2);
        mContext = c;

    }
    public void setAppsClassificationData(AppsClassificationData data){
        mData = data;
    }
    
    public void setAppCategoryBehavior(AppCategoryBehavior behavior){
        mAppCategoryBehavior = behavior;
    }
    
    public String[] getClassificationData(){
        return null;
    }


    private void getFolderExistedInScreenByFolderName(Map<String,T>existedFolderName,Map<String,T>unExistedFolderName,
            final Map<String, ArrayList<String>> appsCategory){
        Iterator<Map.Entry<String, ArrayList<String>>> it = appsCategory.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, ArrayList<String>> map = it.next();
            String folderName = map.getKey();
            T folder = findContainerByName(folderName);
            if(folder != null){
                if(DEBUG){
                    Log.d(TAG, "AppsClassifiction->getFolderExistedInScreenByFolderName-folder exist" + folderName );
                }
                existedFolderName.put(folderName,folder);
            }else{
                if(DEBUG){
                    Log.d(TAG, "AppsClassifiction->getFolderExistedInScreenByFolderName-folder unexist" + folderName );
                }
                unExistedFolderName.put(folderName,null);
            }
        }
    }


    public void show(){
        prepareBeforeShow();
        new Thread(addCategoryAppsRunnable).start();
        
    }
    
    
    /**
     * 请务必注意 该线程内不允许UI操作
     * 请务必注意 createFolderAndAddCategoryAppsRunnable 该线程必须在addCategoryAppsRunnable 之后进行操作（在handler 中已经保证该顺序）
     * 因为 在addCategoryAppsRunnable 中有可能操作workspace 的屏幕数 而且 existedFolderName 等map list 都不是线程安全的
     * @author gcn
     *
     */
    private  class CreatContainerRunnable implements Runnable{
        int mScreenNums = 0;
        int mAddNums = 0;
        boolean mIsAddNewScreen = false;
        int mScreen =-1;
        public CreatContainerRunnable(int screenNums,int addNums ,boolean isAddNewScreen ,int screen){
            mScreenNums = screenNums;
            mAddNums = addNums;
            mIsAddNewScreen = isAddNewScreen;
            mScreen = screen;
        }
        @Override
        public void run() {
            Iterator<Map.Entry<String, T>> it = unExistedFolderName.entrySet().iterator();
            int i = 0;
            while(it.hasNext()){
                Map.Entry<String, T> map = it.next();
                String folderName = map.getKey();
                ArrayList<String> intentURI = appsCategory.get(folderName);
                if(DEBUG){
                    Log.d(TAG, "AppsClassifiction- folder unexist" + folderName  + "has" +intentURI.size()+ "apps");
                }
                if(intentURI.size() <1){
                    continue;
                }
                Message msg = mHandler.obtainMessage();  
                msg.what = 1;  
                Bundle data = new Bundle();
                data.putStringArrayList("package", intentURI);
                data.putString("folderName", folderName);
                if(mIsAddNewScreen ==false){
                    msg.arg1 = mScreen;
                }else{
                    int currentScreen =  (mScreenNums - mAddNums)+i/mCellCount;
                    if(currentScreen>=mScreenNums){
                        msg.arg2  =1;
                        msg.sendToTarget();
                        break;
                    }
                    msg.arg1 =currentScreen;
                }
                i++;
                msg.setData(data);
                msg.sendToTarget();
            }
            
          Message msg = mHandler.obtainMessage();  
          msg.what = 2;  
          msg.sendToTarget();
        }
        
    }
    
    
    
    /**
     * 请务必注意 该线程内不允许UI操作
     */
    private Runnable addCategoryAppsRunnable = new Runnable() {

        @Override
        public void run() {
            appsCategory = mData.getCategoryApps();
            getFolderExistedInScreenByFolderName(existedFolderName,unExistedFolderName,appsCategory);
            
            Iterator<Map.Entry<String, T>> it = existedFolderName.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, T> map = it.next();
                String folderName = map.getKey();
                ArrayList<String> intentURI = appsCategory.get(folderName);
                if(DEBUG){
                    Log.d(TAG, "AppsClassifiction-folder exist" + folderName  + "has" +intentURI.size()+ "apps");
                }
                Message msg = mHandler.obtainMessage();  
                msg.what = 0;  
                Bundle data = new Bundle();
                data.putStringArrayList("package", intentURI);
                msg.obj = map.getValue();
                msg.setData(data);
                msg.sendToTarget();  
            }
            
            int size = unExistedFolderName.size();
            int screen = findEngouhSapce(size);
            Message msg1 = mHandler.obtainMessage();  
            msg1.what = 3; 
            
            if(screen<0){
                //创建屏幕
                mCellCount = getWrokSpaceCellCount();
                int n1 = (int) Math.ceil(size*1.0 /mCellCount);//需要的新屏幕数
               // int n2 = (int) Math.ceil((size-space)*1.0 /mCellCount);//去除最后一屏剩余的空间，需要屏幕的数目
                ////如果n2<n1 则说明把最后一屏幕的空间利用，可以让屏幕数少一屏
                msg1.arg1 = n1;
                //msg1.arg2 = n1; 
                
            }else{
                //不需要创建屏幕
                msg1.arg2 = screen;
                msg1.arg1 = 0;
            }
            msg1.sendToTarget();
            
 

        }
    };
    
    public  void recoveryAfterShow(){
        if(mAppCategoryBehavior != null){
            mAppCategoryBehavior.afterShowBehavior();
        }
    }
    public  void prepareBeforeShow(){
        if(mAppCategoryBehavior != null){
            mAppCategoryBehavior.beforeShowBehavior();
        }
    }
    /**
     * 
     * @return cellcount 数目
     */
    public abstract int getWrokSpaceCellCount();
    
    /**
     * 获取当前状态下的屏幕数
     * @return  屏幕的数
     */
    public abstract int getWorkSpaceScreenNums();
    
    /**
     *  默认查找 最后一屏
     * @param needSpace 所需要的空间
     * @return 返回 sapce 数目
     */
    
    public abstract int findEngouhSapce(int needCount);
    /**
     * 
     * @param addScreenNums   需要增加的屏幕数
     * @return 返回 实际添加的屏幕数
     */
    public abstract int addNewScreen(int addScreenNums);
    /**
     * 通过文件夹名找folderIcon
     * @param classificationName 文件名字 
     * @return 返回folderIcon
     */
    public abstract T findContainerByName(String classificationName);
    
    /**
     * 向已知folder 添加分类应用
     * @param folder  folder 引用
     * @param packageName
     * @return 成功添加的个数
     */
    public abstract void addCategoryAppsToContainer(T folder,List<String> IntentURI);
    
    /**
     * 文件夹不存在，创建文件夹并添加该分类的所有数据
     * @param folderName  要添加 文件的名字
     * @param packageName 
     * @param screen   添加到指定屏幕
     */
    public abstract void createContainerAndAddCateoryApps(String folderName,List<String> IntentURI,int screen);
    
}
