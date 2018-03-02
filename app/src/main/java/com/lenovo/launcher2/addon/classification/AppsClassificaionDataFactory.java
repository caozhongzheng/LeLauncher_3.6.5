package com.lenovo.launcher2.addon.classification;

import android.content.Context;

/**
 * 建议所以分类数据的获取都通过 Factory来获取，不建议直接 生存分类对象
 * @author gecn1
 *
 */
public class AppsClassificaionDataFactory {
    public final static int DATA1 = 1;
    public final static int DATA2 = 2;
    public final static int DATA3 = 3;
    
    private AppsClassificaionDataFactory(){
        
    }
    /**
     * 
     * @param context
     * @param dataSource 需要的分类数据类
     * @return
     */
    public static AppsClassificationData getAppsClassificationData(Context context,int dataSource){
        switch (dataSource) {
        case DATA1:
            return new AppsClassificationData1(context);
        case DATA2:
            return new AppsClassificationData2(context);
//        case DATA3:
//            return new GetAppsCategory(context);
        default:
            return new AppsClassificationData1(context);
        }
    }

}
