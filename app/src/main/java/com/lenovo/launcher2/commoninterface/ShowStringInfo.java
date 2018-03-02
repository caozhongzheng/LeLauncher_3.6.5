package com.lenovo.launcher2.commoninterface;

import com.lenovo.launcher2.customizer.Debug.R5;

public class ShowStringInfo extends ItemInfo{
    public int mNewAdd = 0;
    public String mNewString;
    
    public static final int SHOW_NO = 0;
    public static final int SHOW_NEW = 1;
    public static final int SHOW_NUM = 2;
    public static final int SHOW_MISSED_NUM = 3;

    public ShowStringInfo(ShowStringInfo info) {
        super(info);
        mNewAdd = info.mNewAdd;
        mNewString = info.mNewString;
    }
    
    public ShowStringInfo() {
    }

    public void updateInfo(int num) {        
        if (num == -1)
        {
            mNewAdd = SHOW_NUM;
            mNewString = " ";
        }
        else if (num > 0 && num <= 99)
        {
            mNewAdd = SHOW_NUM;
            mNewString = num + "";
        }
        else if (num > 99)                                
        {
            mNewAdd = SHOW_NUM;
            mNewString = "99+";
        }
        else
        {
            mNewAdd = SHOW_NO;
            mNewString = null;
        }            

        return ;
    }
    
    public void updateInfo(String str) {
        try
        {
            int num = Integer.parseInt(str);
            updateInfo(num);
        }
        catch (Exception e)
        {
            R5.echo("no save int");
        }
        
        return ;
    }
}
