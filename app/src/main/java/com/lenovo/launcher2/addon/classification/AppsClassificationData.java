package com.lenovo.launcher2.addon.classification;

import java.util.ArrayList;
import java.util.Map;

/**
 * 
 * @author gecn1
 * 所有使用是使用分类数据的类 必须实现该接口
 */
public interface AppsClassificationData {
    /**
     * 获取所以的分类信息以及内容
     * @return 分类名字 以及该分类下的app info
     */
    Map<String, ArrayList<String>> getCategoryApps();
}
