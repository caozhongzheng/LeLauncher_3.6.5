package com.lenovo.launcher2.addon.classification;


public class AppsFilter {
    
    private AppsFilter(){
        
    }
    public static boolean filterPackageName(String packageName ,String className){
        return packageName== null ? false : ( packageName.contains("com.lenovo.launcher.theme")
                ||packageName.contains("com.lenovo.launcher")
                ||packageName.contains("com.lenovo.xlauncher")
                ||(packageName.equals("com.android.contacts") && (className !=null) &&className.equals("com.android.contacts.activities.DialtactsActivity"))
                ||(packageName.equals("com.lenovo.ideafriend")&& (className !=null) && className.equals("com.lenovo.ideafriend.alias.DialtactsActivity"))
                ||(packageName.equals("com.android.contacts")&& (className !=null) && className.equals("com.android.contacts.activities.PeopleActivity"))
                ||(packageName.equals("com.lenovo.ideafriend")&& (className !=null) && className.equals("com.lenovo.ideafriend.alias.PeopleActivity"))
                ||(packageName.equals("com.android.mms"))
                || packageName.equals("com.lenovo.mms")
                || packageName.equals("com.lenovo.ideafriend")
                || packageName.equals("com.android.browser")
                || packageName.contains("com.qigame.lock"));
    }

}
