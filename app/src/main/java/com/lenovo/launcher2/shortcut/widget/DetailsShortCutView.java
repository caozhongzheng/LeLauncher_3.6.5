package com.lenovo.launcher2.shortcut.widget;
import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.weather.widget.utils.WeatherUtilites;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class DetailsShortCutView extends RelativeLayout {
	private Button mbtn;
	private LinearLayout mlayout[] = new LinearLayout[5];
	private ImageView mimag[] = new ImageView[5];
	private final static int mres[]={R.drawable.shutcut_widget_music,
		R.drawable.shutcut_widget_clock,
		R.drawable.shutcut_widget_bress,
		R.drawable.shutcut_widget_setting,
		R.drawable.shutcut_widget_app};
	private Context mcontext;
	RelativeLayout.LayoutParams lp1[] = new RelativeLayout.LayoutParams[5];
	TranslateAnimation animtion[] = new TranslateAnimation[5];
	LauncherApplication mapp ;
	private int mstatebar =0;
	private OnClickListener displaylistener,clocklistener,qingjinglistener,applistener,musiclistener;
	
	private RelativeLayout.LayoutParams lp;
	private int deleteNum,type,top,radius,left;
	public DetailsShortCutView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public DetailsShortCutView(Context context) {
		super(context);
		mcontext = context;
		mstatebar = getStatusHeight(context);
		this.setBackgroundColor(0XAA000000);
		// TODO Auto-generated constructor stub
	}
	public DetailsShortCutView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		mcontext.sendBroadcast(new Intent("com.lenovo.shortcut.close"));
		return super.onTouchEvent(event);
	}
	
	public void setOnclickListener(int deviceType,LauncherApplication app,int width,int height){
		deleteNum=0;
		if (deviceType!=-1) {
			deleteNum=30;
		}
		mapp = app;
         lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
				, LinearLayout.LayoutParams.WRAP_CONTENT);
        Log.d("a","width="+width);
        Log.d("a","height="+height);
          radius = 0;
          type = 0;
          top = 0;
          left = 0;
        if(width>1000){
        	radius = 400;
        	type = 0;
        	if (deviceType!=-1) {
        		top =60;
            	left =50;
			}else{
        	  top = 75;
        	  left = 40;
			}
        }
        else if(width<1000&&width>600){
        	radius = 300;
        	type = 1;
        	top = 45;
        	left = 30;
        }
        else{ 
        	type = 2;
        	radius = 200;
        	if (deviceType==0) {
        		left = 25;
          	  top = 20;
			}
        	else{
        	   left = 25;
          	    top = 40;
        	}
        }
		  displaylistener = new OnClickListener(){
    		public void onClick(View v){
//    			DetailsShortCutView.this.setVisibility(View.GONE);
    			new Thread(){
    				public void run(){
    					Intent intent = new Intent("com.lenovo.shortcut.close");
    					intent.putExtra("shortcut_type", 1);
    	    			mcontext.sendBroadcast(intent);
    	    			
    	    			/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-3-26 S*/
    	    			Reaper.processReaper( getContext(),
    	    		         Reaper.REAPER_EVENT_CATEGORY_WIDGET,
    	    		         Reaper.REAPER_EVENT_ACTION_WIDGET_SHORTCUT,
    	    		         "1",
    	    		         Reaper.REAPER_NO_INT_VALUE );
    	    			/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-3-26 E*/
    				}
    			}.start();

    		}
        };
         clocklistener = new OnClickListener(){
    		public void onClick(View v){
//    			DetailsShortCutView.this.setVisibility(View.GONE);
    			new Thread(){
    				public void run(){
    					Intent intent = new Intent("com.lenovo.shortcut.close");
    					intent.putExtra("shortcut_type", 2);
    	    			mcontext.sendBroadcast(intent);
    	    			
    	    			/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-3-26 S*/
    	    			Reaper.processReaper( getContext(),
    	    		         Reaper.REAPER_EVENT_CATEGORY_WIDGET,
    	    		         Reaper.REAPER_EVENT_ACTION_WIDGET_SHORTCUT,
    	    		         "2",
    	    		         Reaper.REAPER_NO_INT_VALUE );
    	    			/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-3-26 E*/
    				}
    			}.start();
    		}
        };
         qingjinglistener = new OnClickListener(){
    		public void onClick(View v){
//    			DetailsShortCutView.this.setVisibility(View.GONE);
    			new Thread(){
    				public void run(){
    					Intent intent = new Intent("com.lenovo.shortcut.close");
    					intent.putExtra("shortcut_type", 3);
    	    			mcontext.sendBroadcast(intent);
    	    			
    	    			/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-3-26 S*/
    	    			Reaper.processReaper( getContext(),
    	    		         Reaper.REAPER_EVENT_CATEGORY_WIDGET,
    	    		         Reaper.REAPER_EVENT_ACTION_WIDGET_SHORTCUT,
    	    		         "3",
    	    		         Reaper.REAPER_NO_INT_VALUE );
    	    			/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-3-26 E*/
    				}
    			}.start();
    		}
        };
         applistener = new OnClickListener(){
    		public void onClick(View v){
//    			DetailsShortCutView.this.setVisibility(View.GONE);
    			new Thread(){
    				public void run(){
    					Intent intent = new Intent("com.lenovo.shortcut.close");
    					intent.putExtra("shortcut_type", 4);
    	    			mcontext.sendBroadcast(intent);
    	    			
    	    			/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-3-26 S*/
    	    			Reaper.processReaper( getContext(),
    	    		         Reaper.REAPER_EVENT_CATEGORY_WIDGET,
    	    		         Reaper.REAPER_EVENT_ACTION_WIDGET_SHORTCUT,
    	    		         "4",
    	    		         Reaper.REAPER_NO_INT_VALUE );
    	    			/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-3-26 E*/
    				}
    			}.start();
    		}
        };
         musiclistener = new OnClickListener(){
    		public void onClick(View v){
//    			DetailsShortCutView.this.setVisibility(View.GONE);
    			new Thread(){
    				public void run(){
		    			mcontext.sendBroadcast(new Intent("com.lenovo.shortcut.close"));
    					Intent intent = new Intent("com.lenovo.shortcut.close");
    					intent.putExtra("shortcut_type", 5);
    	    			mcontext.sendBroadcast(intent);
    	    			
    	    			/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-3-26 S*/
    	    			Reaper.processReaper( getContext(),
    	    		         Reaper.REAPER_EVENT_CATEGORY_WIDGET,
    	    		         Reaper.REAPER_EVENT_ACTION_WIDGET_SHORTCUT,
    	    		         "5",
    	    		         Reaper.REAPER_NO_INT_VALUE );
    	    			/* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-3-26 E*/
    				}
    			}.start();
    		}
        };
	}
	public void setRect7(Rect rect,int height,int width,LauncherApplication app,int deviceType){
		Log.d("short", "进来了");
        setOnclickListener(deviceType,app,width,height);
        
        int angle = 0;
		for(int i = 0;i<5;i++){
			mimag[i] = new ImageView(mcontext);
			if(i==3){
				if(!WeatherUtilites.findForPackage(mcontext, "com.lenovo.safe.powercenter")){
					mimag[i].setBackgroundDrawable(mapp.mLauncherContext.getDrawable(mres[i]));
				}else
					mimag[i].setBackgroundDrawable(mapp.mLauncherContext.getDrawable(R.drawable.shutcut_widget_batty));
			}else
				mimag[i].setBackgroundDrawable(mapp.mLauncherContext.getDrawable(mres[i]));
			mlayout[i] = new LinearLayout(mcontext);
			mlayout[i].addView(mimag[i],lp);
			mlayout[i].setClickable(true);
			mlayout[i].setFocusable(true);
			mlayout[i].setGravity(Gravity.CENTER);
			mlayout[i].setBackgroundDrawable(mapp.mLauncherContext.getDrawable(R.drawable.shutcut_widget_layout_bg));
			switch(i){
			case 0:
				mlayout[i].setOnClickListener(musiclistener);
				break;
			case 1:
				mlayout[i].setOnClickListener(clocklistener);
				break;
			case 2:
				mlayout[i].setOnClickListener(displaylistener);
				break;
			case 3:
				mlayout[i].setOnClickListener(qingjinglistener);
				break;
			case 4:
				mlayout[i].setOnClickListener(applistener);
				break;
			default:
				break;
			}
			lp1[i] = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			AnimationSet set = new AnimationSet(true);  
			if(rect.left<rect.width()){
		        Log.d("a","rect.top="+rect.top);
		        Log.d("a","rect.bottom="+rect.bottom);
		        Log.d("a","rect.height()="+rect.height());
				if(rect.top<(rect.height()-deleteNum)){//左上角
					angle = 20;
					int addDistance=0;
					int addTopDistance=0;
					if(type==0){
						addDistance=50;
						radius = 180;
						addTopDistance=-20;
					   Log.i("a", "type="+type);
				    }
					else if(type==1) {
						radius = 180;
						addDistance=10;
						addTopDistance=110;
						Log.i("a", "type="+type);
					}
				   else{
						addTopDistance=50;
						radius = 180;
					}
					Log.d("a", "左上角");
					Log.i("a", "type="+type);
					lp1[i].topMargin = (int) (radius*Math.sin((85-angle*i)*Math.PI/180))+addTopDistance;
					lp1[i].leftMargin = (int) (radius*Math.sin((5+angle*i)*Math.PI/180))+addDistance;
					animtion[i] = new TranslateAnimation((float)-lp1[i].leftMargin,0,(float)-lp1[i].topMargin,0);
				}else if(height>rect.bottom+rect.height()){//左边
					int addDistance=0;
					int addTopDistance=0;
						if(type==0){
						   addDistance=75;
						   addTopDistance=-35;
						   radius = 150;
						}
						else if(type==1){
							radius = 130;
							addTopDistance=80;
						}
						else{
							radius = 130;
							addTopDistance=50;
						}
						Log.d("a", "左边");
					double	h = radius*Math.sin((60-i*30)*Math.PI/180);
					double	h1 = radius*Math.sin((30+30*i)*Math.PI/180);
					if(60-i*30>90)
						h = -h;
					lp1[i].topMargin = (int) (rect.top+top-rect.height()/2-h)+20+mstatebar+addTopDistance;
					lp1[i].leftMargin = (int) h1+addDistance;
					animtion[i] = new TranslateAnimation((float)-lp1[i].leftMargin,0,(float) (rect.height()/2+(int)h-top),0);
				}else{//左下角
					angle = 20;
					int addDistance=0;
					int addTopDistance=0;
					if(type==0){
						radius =180;
						addTopDistance=25;
						addDistance=55;
					}
					else if(type==1){
						radius = 180;
						addTopDistance=50;
					}
					else{
						radius = 180;
						addTopDistance=54;
						addDistance=-15;
					}
					Log.d("a", "左下角");
					lp1[i].topMargin = height-(int) (radius*Math.sin((85-angle*i)*Math.PI/180))-rect.height()-mstatebar-addTopDistance;
					lp1[i].leftMargin = (int) (radius*Math.sin((5+angle*i)*Math.PI/180))+addDistance;
					animtion[i] = new TranslateAnimation((float)-lp1[i].leftMargin,0,(float)lp1[i].topMargin,0);
				}
			}else if(rect.top<(rect.height()-deleteNum)){
				if(width>=rect.right+rect.width()){//居上
					int addDistance=0;
					int addTopDistance=0;
					if(type==0){
						radius = 150;
						addTopDistance=-10;
						addDistance=-20;
					}
					else if(type==1){
						radius =150;
						addTopDistance=100;
					}
					else{
						radius = 130;
						addTopDistance=80;
						addDistance=-15;
					}
					Log.d("a", "居上3");
					double	h1 = radius*Math.sin((60-i*30)*Math.PI/180);
					double	h = radius*Math.sin((30+30*i)*Math.PI/180);
					if(60-i*30>90)
						h = -h;
					lp1[i].topMargin = (int) h+addTopDistance;
					lp1[i].leftMargin = (int) (rect.left-h1)+left+addDistance;
					animtion[i] = new TranslateAnimation((float) h1,0,(float)-lp1[i].topMargin,0);
				}else{//右上角
					angle = 20;
					int addDistance=0;
					int addTopDistance=0;
					if(type==0){
						radius =180;
						addDistance=-10;
						addTopDistance=-20;
					}
					else if(type==1){
						addTopDistance=100;
						radius =180;
					}
					else{
						radius = 180;
						addTopDistance=60;
					}
					Log.d("a", "右上角");
					lp1[i].topMargin = (int) (radius*Math.sin((85-angle*i)*Math.PI/180))+addTopDistance;
					lp1[i].leftMargin = (int) (rect.left-radius*Math.sin((5+angle*i)*Math.PI/180))+rect.width()/2;
					animtion[i] = new TranslateAnimation((float) (radius*Math.sin((5+angle*i)*Math.PI/180)+addDistance),0,(float)-lp1[i].topMargin,0);
				}
			}else if(rect.top>(rect.height()-deleteNum)&&width<rect.right+rect.width()){//右边
				int addDistance=0;
				int addTopDistance=0;
				Log.d("a","rect.bottom+rect.height()="+(rect.bottom+rect.height())+",height="+height+"rect.bottom="+rect.bottom+"rect.height="+rect.height()+"rect.top="+rect.top);
				if(rect.bottom+rect.height()>=height){
					angle = 20;
					if(type==0){
						radius = 200;
					}
					else if(type==1){
						radius =180;
						addTopDistance=60;
					}
					else{	
						radius = 180;
						addTopDistance=54;
					}
					Log.d("a", "右下角");
					lp1[i].topMargin =height-(int) (radius*Math.sin((85-angle*i)*Math.PI/180))-rect.height()-mstatebar-addTopDistance;
					if(type==2)
						lp1[i].leftMargin = (int) (rect.left-radius*Math.sin((5+angle*i)*Math.PI/180))+rect.width()/2-left;
					else
						lp1[i].leftMargin = (int) (rect.left-radius*Math.sin((5+angle*i)*Math.PI/180))+rect.width()/2;
					animtion[i] = new TranslateAnimation((float) (radius*Math.sin((5+angle*i)*Math.PI/180))+addDistance,0,(float)lp1[i].topMargin,0);
				}else{
					if(type==0){
						radius =150;
						addTopDistance=-15;
					}
	               else if(type==1){
						radius =150;
						addTopDistance=110;
	               }
				   else{
				 	 radius = 130;
				 	 addTopDistance=50;
				 	 addDistance=-15;
				   }
					 Log.d("a", "右边");
					double	h = radius*Math.sin((60-i*30)*Math.PI/180);
					double	h1 = radius*Math.sin((30+30*i)*Math.PI/180);
					if(60-i*30>90)
						h = -h;
					lp1[i].topMargin = (int) (rect.top-rect.height()/2+top-h)+20+mstatebar+addTopDistance;
					lp1[i].leftMargin = (int) (rect.left - h1)+rect.width()/2+addDistance;;
					animtion[i] = new TranslateAnimation((float)h1,0,(float)(rect.height()/2-top+h),0);
				}
			}else {
				if(rect.bottom<height/2){//居中上
					int addDistance=0;
					int addTopDistance=0;
					int posx = 0;
					if(type==0){
						radius =150;
						addDistance=-35;
						posx =0;
					}
					else if(type==1){
						radius =150;
						addTopDistance=100;
					}
					else{
						posx = -15;
						addTopDistance=65;
						radius = 130;
					}
					Log.d("a", "居中上");
					double	h1 = radius*Math.sin((60-i*30)*Math.PI/180);
					double	h = radius*Math.sin((30+30*i)*Math.PI/180);
					if(60-i*30>90)
						h = -h;
					lp1[i].topMargin = (int) (rect.top-rect.height()/2+h)+mstatebar+addTopDistance;
					lp1[i].leftMargin = (int) (rect.left-h1)+left+posx;
					double xd = h1+((double)rect.width()/2);
					double yd = ((double)rect.height())/2-h;
					animtion[i] = new TranslateAnimation((float)xd,0,(float)yd,0);
				}else{//居中下
					int posx = 0;
					int addTopDistance=0;
					int addDistance=0;
					if(height>rect.bottom+rect.height()){
						if(type==0){
							addTopDistance=50;
							radius =150;
							addDistance=40;
							posx = 10;
						}
						else if(type==1){
							radius =150;
							addTopDistance=30;
						}
						else{
							posx = -5;
							radius = 130;
						}
						Log.d("a","居中下");
						double	h1 = radius*Math.sin((60-i*30)*Math.PI/180);
						double	h = radius*Math.sin((30+30*i)*Math.PI/180);
						if(60-i*30>90)
							h = -h;
						lp1[i].topMargin = (int) (rect.top+rect.height()/2-h)-top+addTopDistance;
						lp1[i].leftMargin = (int) (rect.left-h1)+left+posx;
						double xd = h1+((double)rect.width()/2);
						animtion[i] = new TranslateAnimation((float)xd,0,(float)h,0);
					}else{
						if(type==0){
							radius = 150;
							addDistance=120;
							addTopDistance=65;
						}
						else if(type==1){
							radius=150;
					    }
						else{
							posx = -5;
							radius = 150;
							addDistance=80;
						}
						Log.d("a", "居下");
						double	h1 = radius*Math.sin((60-i*30)*Math.PI/180);
						double	h = radius*Math.sin((30+30*i)*Math.PI/180);
						if(60-i*30>90)
							h = -h;
						lp1[i].topMargin = (int) (rect.top+rect.height()/2-h)-top+addTopDistance;
						if(rect.left<width/2)
							lp1[i].leftMargin = (int) (rect.left-h1)+left+posx;
						else
							lp1[i].leftMargin = (int) (rect.left-h1)+left+posx-rect.width()+addDistance;
						double xd = h1+((double)rect.width()/2);
						animtion[i] = new TranslateAnimation((float) xd,0,(float)h,0);
					}
				}
			}
			long offset = i * 200/4;
			AlphaAnimation alph = new AlphaAnimation(0,1);
			alph.setDuration(100);
			animtion[i].setDuration(300);
			set.setStartOffset(offset);
			set.addAnimation(alph);
			set.addAnimation(animtion[i]);
			SwitchAnimationListener li = new SwitchAnimationListener();
			set.setAnimationListener(li);
			set.setInterpolator(new OvershootInterpolator(2.0F));
			mlayout[i].startAnimation(set);
			addView(mlayout[i],lp1[i]);
		}
	}
	
	public void setRect1(Rect rect,int height,int width,LauncherApplication app,int deviceType){

         setOnclickListener(deviceType,app,width,height);
        int angle = 0;
		for(int i = 0;i<5;i++){
			mimag[i] = new ImageView(mcontext);
			if(i==3){
				if(!WeatherUtilites.findForPackage(mcontext, "com.lenovo.safe.powercenter")){
					mimag[i].setBackgroundDrawable(mapp.mLauncherContext.getDrawable(mres[i]));
				}else
					mimag[i].setBackgroundDrawable(mapp.mLauncherContext.getDrawable(R.drawable.shutcut_widget_batty));
			}else
				mimag[i].setBackgroundDrawable(mapp.mLauncherContext.getDrawable(mres[i]));
			mlayout[i] = new LinearLayout(mcontext);
			mlayout[i].addView(mimag[i],lp);
			mlayout[i].setClickable(true);
			mlayout[i].setFocusable(true);
			mlayout[i].setGravity(Gravity.CENTER);
			mlayout[i].setBackgroundDrawable(mapp.mLauncherContext.getDrawable(R.drawable.shutcut_widget_layout_bg));
			switch(i){
			case 0:
				mlayout[i].setOnClickListener(musiclistener);
				break;
			case 1:
				mlayout[i].setOnClickListener(clocklistener);
				break;
			case 2:
				mlayout[i].setOnClickListener(displaylistener);
				break;
			case 3:
				mlayout[i].setOnClickListener(qingjinglistener);
				break;
			case 4:
				mlayout[i].setOnClickListener(applistener);
				break;
			default:
				break;
			}
			lp1[i] = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			AnimationSet set = new AnimationSet(true);  
			if(rect.left<rect.width()){
		        Log.d("a","rect.top="+rect.top);
		        Log.d("a","rect.bottom="+rect.bottom);
		        Log.d("a","rect.height()="+rect.height());
				if(rect.top<(rect.height()-deleteNum)){//左上角
					angle = 20;
					int addDistance=0;
					int addTopDistance=0;
					if(type==0){
						addDistance=120;
						radius = 180;
					   Log.i("a", "type="+type);
				    }
					else if(type==1) {
						radius = 180;
						addDistance=10;
						addTopDistance=110;
						Log.i("a", "type="+type);
					}
				   else{
						
						radius = 150;
					}
					Log.d("a", "左上角");
					lp1[i].topMargin = (int) (radius*Math.sin((85-angle*i)*Math.PI/180))+addTopDistance;
					lp1[i].leftMargin = (int) (radius*Math.sin((5+angle*i)*Math.PI/180))+addDistance;
					animtion[i] = new TranslateAnimation((float)-lp1[i].leftMargin,0,(float)-lp1[i].topMargin,0);
				}else if(height>rect.bottom+rect.height()){//左边
					int addDistance=0;
					int addTopDistance=0;
					if(type==0){
						addDistance=80;
						radius = 150;
					}
					else if(type==1){
						radius = 150;
						addTopDistance=90;
					}
					else{
						radius = 150;
					}
					Log.d("a", "左边");
					double	h = radius*Math.sin((60-i*30)*Math.PI/180);
					double	h1 = radius*Math.sin((30+30*i)*Math.PI/180);
					if(60-i*30>90)
						h = -h;
					lp1[i].topMargin = (int) (rect.top+top-rect.height()/2-h)+20+mstatebar+addTopDistance;
					lp1[i].leftMargin = (int) h1+addDistance;
					animtion[i] = new TranslateAnimation((float)-lp1[i].leftMargin,0,(float) (rect.height()/2+(int)h-top),0);
				}else{//左下角
					angle = 20;
					int addDistance=0;
					int addTopDistance=0;
					if(type==0){
						radius = 150;
					}
					else if(type==1){
						radius = 180;
						addTopDistance=50;
					}
					else{
						radius = 150;
					}
					Log.d("a", "左下角");
					lp1[i].topMargin = height-(int) (radius*Math.sin((85-angle*i)*Math.PI/180))-rect.height()-mstatebar-addTopDistance;
					lp1[i].leftMargin = (int) (radius*Math.sin((5+angle*i)*Math.PI/180));
					animtion[i] = new TranslateAnimation((float)-lp1[i].leftMargin,0,(float)lp1[i].topMargin,0);
				}
			}else if(rect.top<(rect.height()-deleteNum)){
				int addDistance=0;
				int addTopDistance=0;
				if(width>=rect.right+rect.width()){//居上
					if(type==0){
						radius = 150;
					}
					else if(type==1){
						radius =150;
						addTopDistance=100;
					}
					else{
						radius = 150;
					}
					Log.d("a", "居上");
					double	h1 = radius*Math.sin((60-i*30)*Math.PI/180);
					double	h = radius*Math.sin((30+30*i)*Math.PI/180);
					if(60-i*30>90)
						h = -h;
					lp1[i].topMargin = (int) h+addTopDistance;
					lp1[i].leftMargin = (int) (rect.left-h1)+left;
					animtion[i] = new TranslateAnimation((float) h1,0,(float)-lp1[i].topMargin,0);
				}else{//右上角
					angle = 20;
					if(type==0){
						radius = 200;
					}
					else if(type==1){
						addTopDistance=100;
						radius =180;
					}
					else{
						radius = 150;
					}
					Log.d("a", "右上角");
					lp1[i].topMargin = (int) (radius*Math.sin((85-angle*i)*Math.PI/180))+addTopDistance;
					lp1[i].leftMargin = (int) (rect.left-radius*Math.sin((5+angle*i)*Math.PI/180))+rect.width()/2;
					animtion[i] = new TranslateAnimation((float) (radius*Math.sin((5+angle*i)*Math.PI/180)+addDistance),0,(float)-lp1[i].topMargin,0);
				}
			}else if(rect.top>(rect.height()-deleteNum)&&width<rect.right+rect.width()){//右边
				if(rect.bottom+rect.height()>height){
					angle = 20;
					int addDistance=0;
					int addTopDistance=0;
					if(type==0){
						radius = 200;
					}
					else if(type==1){
						radius =180;
						addTopDistance=60;
					}
					else{	
						radius = 150;
					}
					Log.d("a", "右边");
					lp1[i].topMargin =height-(int) (radius*Math.sin((85-angle*i)*Math.PI/180))-rect.height()-mstatebar-addTopDistance;
					if(type==2)
						lp1[i].leftMargin = (int) (rect.left-radius*Math.sin((5+angle*i)*Math.PI/180))+rect.width()/2-left;
					else
						lp1[i].leftMargin = (int) (rect.left-radius*Math.sin((5+angle*i)*Math.PI/180))+rect.width()/2;
					animtion[i] = new TranslateAnimation((float) (radius*Math.sin((5+angle*i)*Math.PI/180)+addDistance),0,(float)lp1[i].topMargin,0);
				}else{
					int addDistance=0;
					int addTopDistance=0;
					if(type==0){
						radius =150;
					}
	               else if(type==1){
						radius =150;
						addTopDistance=80;
	               }
				   else{
				 	 radius = 150;
				   }
					Log.d("a", "右下角");
					double	h = radius*Math.sin((60-i*30)*Math.PI/180);
					double	h1 = radius*Math.sin((30+30*i)*Math.PI/180);
					if(60-i*30>90)
						h = -h;
					lp1[i].topMargin = (int) (rect.top-rect.height()/2+top-h)+20+mstatebar+addTopDistance;
					lp1[i].leftMargin = (int) (rect.left - h1)+rect.width()/2;
					animtion[i] = new TranslateAnimation((float)h1,0,(float)(rect.height()/2-top+h),0);
				}
			}else {
				if(rect.bottom<height/2){//居中上
					int posx = 0;
					int addDistance=0;
					int addTopDistance=0;
					if(type==0){
						radius =170;
						posx = 10;
					}
					else if(type==1){
						radius =150;
						addTopDistance=100;
					}
					else{
						posx = -5;
						radius = 150;
					}
					Log.d("a", "居中上");
					double	h1 = radius*Math.sin((60-i*30)*Math.PI/180);
					double	h = radius*Math.sin((30+30*i)*Math.PI/180);
					if(60-i*30>90)
						h = -h;
					lp1[i].topMargin = (int) (rect.top-rect.height()/2+h)+mstatebar+addTopDistance;
					lp1[i].leftMargin = (int) (rect.left-h1)+left+posx;
					double xd = h1+((double)rect.width()/2);
					double yd = ((double)rect.height())/2-h;
					animtion[i] = new TranslateAnimation((float)xd,0,(float)yd,0);
				}else{//居中下
					int posx = 0;
					int addTopDistance=0;
					int addDistance=0;
					if(height>rect.bottom+rect.height()){
						if(type==0){
							addTopDistance=80;
							radius =150;
							posx = 10;
						}
						else if(type==1){
							radius =150;
							addTopDistance=30;
						}
						else{
							posx = -5;
							radius = 150;
						}
						Log.d("a","居中下");
						double	h1 = radius*Math.sin((60-i*30)*Math.PI/180);
						double	h = radius*Math.sin((30+30*i)*Math.PI/180);
						if(60-i*30>90)
							h = -h;
						lp1[i].topMargin = (int) (rect.top+rect.height()/2-h)-top+addTopDistance;
						lp1[i].leftMargin = (int) (rect.left-h1)+left+posx;
						double xd = h1+((double)rect.width()/2);
						animtion[i] = new TranslateAnimation((float) xd,0,(float)h,0);
					}else{
						if(type==0){
							radius = 150;
							addDistance=140;
							posx = 10;
						}
						else if(type==1){
							radius=150;
							addDistance=140;
						}
						else{
							posx = -5;
							radius = 150;
						}
						Log.d("a", "居下");
						double	h1 = radius*Math.sin((60-i*30)*Math.PI/180);
						double	h = radius*Math.sin((30+30*i)*Math.PI/180);
						if(60-i*30>90)
							h = -h;
						lp1[i].topMargin = (int) (rect.top+rect.height()/2-h)-top+addTopDistance;
						if(rect.left<width/2)
							lp1[i].leftMargin = (int) (rect.left-h1)+left+posx;
						else
							lp1[i].leftMargin = (int) (rect.left-h1)+left+posx-rect.width()+addDistance;
						double xd = h1+((double)rect.width()/2);
						animtion[i] = new TranslateAnimation((float) xd,0,(float)h,0);
					}
				}
			}
			long offset = i * 200/4;
			AlphaAnimation alph = new AlphaAnimation(0,1);
			alph.setDuration(100);
			animtion[i].setDuration(300);
			set.setStartOffset(offset);
			set.addAnimation(alph);
			set.addAnimation(animtion[i]);
			SwitchAnimationListener li = new SwitchAnimationListener();
			set.setAnimationListener(li);
			set.setInterpolator(new OvershootInterpolator(2.0F));
			mlayout[i].startAnimation(set);
			addView(mlayout[i],lp1[i]);
		}
	}
	
	public void setRect(Rect rect,int height,int width,LauncherApplication app,int deviceType)
	{
        setOnclickListener(deviceType,app,width,height);
        int angle = 0;
		for(int i = 0;i<5;i++){
			mimag[i] = new ImageView(mcontext);
			if(i==3){
				if(!WeatherUtilites.findForPackage(mcontext, "com.lenovo.safe.powercenter")){
					mimag[i].setBackgroundDrawable(mapp.mLauncherContext.getDrawable(mres[i]));
				}else
					mimag[i].setBackgroundDrawable(mapp.mLauncherContext.getDrawable(R.drawable.shutcut_widget_batty));
			}else
				mimag[i].setBackgroundDrawable(mapp.mLauncherContext.getDrawable(mres[i]));
			mlayout[i] = new LinearLayout(mcontext);
			mlayout[i].addView(mimag[i],lp);
			mlayout[i].setClickable(true);
			mlayout[i].setFocusable(true);
			mlayout[i].setGravity(Gravity.CENTER);
			mlayout[i].setBackgroundDrawable(mapp.mLauncherContext.getDrawable(R.drawable.shutcut_widget_layout_bg));
			switch(i){
			case 0:
				mlayout[i].setOnClickListener(musiclistener);
				break;
			case 1:
				mlayout[i].setOnClickListener(clocklistener);
				break;
			case 2:
				mlayout[i].setOnClickListener(displaylistener);
				break;
			case 3:
				mlayout[i].setOnClickListener(qingjinglistener);
				break;
			case 4:
				mlayout[i].setOnClickListener(applistener);
				break;
			default:
				break;
			}
			lp1[i] = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			AnimationSet set = new AnimationSet(true);  
			if(rect.left<rect.width()){
		        Log.d("a","rect.top="+rect.top);
		        Log.d("a","rect.bottom="+rect.bottom);
		        Log.d("a","rect.height()="+rect.height());
				if(rect.top<rect.height()){//左上角
					angle = 20;
					lp1[i].topMargin = (int) (radius*Math.sin((85-angle*i)*Math.PI/180));
					lp1[i].leftMargin = (int) (radius*Math.sin((5+angle*i)*Math.PI/180));
					animtion[i] = new TranslateAnimation((float)-lp1[i].leftMargin,0,(float)-lp1[i].topMargin,0);
				}else if(height>rect.bottom+rect.height()){//左边
					if(type==0)
						radius = 350;
					else if(type==1)
						radius = 250;
					else
						radius = 150;
					double	h = radius*Math.sin((60-i*30)*Math.PI/180);
					double	h1 = radius*Math.sin((30+30*i)*Math.PI/180);
					if(60-i*30>90)
						h = -h;
					lp1[i].topMargin = (int) (rect.top+top-rect.height()/2-h)+20+mstatebar;
					lp1[i].leftMargin = (int) h1;
					animtion[i] = new TranslateAnimation((float)-lp1[i].leftMargin,0,(float) (rect.height()/2+(int)h-top),0);
				}else{//左下角
					angle = 20;
					lp1[i].topMargin = height-(int) (radius*Math.sin((85-angle*i)*Math.PI/180))-rect.height()-mstatebar;
					lp1[i].leftMargin = (int) (radius*Math.sin((5+angle*i)*Math.PI/180));
					animtion[i] = new TranslateAnimation((float)-lp1[i].leftMargin,0,(float)lp1[i].topMargin,0);
				}
			}else if(rect.top<rect.height()){
				if(width>=rect.right+rect.width()){//居上
					if(type==0)
						radius = 350;
					else if(type==1)
						radius = 230;
					else
						radius = 150;
					double	h1 = radius*Math.sin((60-i*30)*Math.PI/180);
					double	h = radius*Math.sin((30+30*i)*Math.PI/180);
					if(60-i*30>90)
						h = -h;
					lp1[i].topMargin = (int) h;
					lp1[i].leftMargin = (int) (rect.left-h1)+left;
					animtion[i] = new TranslateAnimation((float) h1,0,(float)-lp1[i].topMargin,0);
				}else{//右上角
					angle = 20;
					lp1[i].topMargin = (int) (radius*Math.sin((85-angle*i)*Math.PI/180));
					lp1[i].leftMargin = (int) (rect.left-radius*Math.sin((5+angle*i)*Math.PI/180))+rect.width()/2;
					animtion[i] = new TranslateAnimation((float) (radius*Math.sin((5+angle*i)*Math.PI/180)),0,(float)-lp1[i].topMargin,0);
				}
			}else if(rect.top>rect.height()&&width<rect.right+rect.width()){//右边
				if(rect.bottom+rect.height()>height){
					angle = 20;
					lp1[i].topMargin =height-(int) (radius*Math.sin((85-angle*i)*Math.PI/180))-rect.height()-mstatebar;
					if(type==2)
						lp1[i].leftMargin = (int) (rect.left-radius*Math.sin((5+angle*i)*Math.PI/180))+rect.width()/2-left;
					else
						lp1[i].leftMargin = (int) (rect.left-radius*Math.sin((5+angle*i)*Math.PI/180))+rect.width()/2;
					animtion[i] = new TranslateAnimation((float) (radius*Math.sin((5+angle*i)*Math.PI/180)),0,(float)lp1[i].topMargin,0);
				}else{
					if(type==0)
						radius = 350;
					else if(type==1)
						radius = 250;
					else
						radius = 150;
					double	h = radius*Math.sin((60-i*30)*Math.PI/180);
					double	h1 = radius*Math.sin((30+30*i)*Math.PI/180);
					if(60-i*30>90)
						h = -h;
					lp1[i].topMargin = (int) (rect.top-rect.height()/2+top-h)+20+mstatebar;
					lp1[i].leftMargin = (int) (rect.left - h1)+rect.width()/2;
					animtion[i] = new TranslateAnimation((float)h1,0,(float)(rect.height()/2-top+h),0);
				}
			}else {
				if(rect.bottom<height/2){//居中上
					int posx = 0;
					if(type==0){
						radius = 350;
						posx = 10;
					}
					else if(type==1)
						radius = 230;
					else{
						posx = -5;
						radius = 170;
					}
					double	h1 = radius*Math.sin((60-i*30)*Math.PI/180);
					double	h = radius*Math.sin((30+30*i)*Math.PI/180);
					if(60-i*30>90)
						h = -h;
					lp1[i].topMargin = (int) (rect.top-rect.height()/2+h)+mstatebar;
					lp1[i].leftMargin = (int) (rect.left-h1)+left+posx;
                                        double xd = h1+((double)rect.width()/2);
					double yd = ((double)rect.height())/2-h;
					animtion[i] = new TranslateAnimation((float)xd,0,(float)yd,0);
					//animtion[i] = new TranslateAnimation((float)(h1+rect.width()/2),0,(float)(rect.height()/2-h),0);
				}else{//居中下
					int posx = 0;
					if(height>rect.bottom+rect.height()){
						if(type==0){
							radius = 350;
							posx = 10;
						}
						else if(type==1)
							radius = 230;
						else{
							posx = -5;
							radius = 170;
						}
						double	h1 = radius*Math.sin((60-i*30)*Math.PI/180);
						double	h = radius*Math.sin((30+30*i)*Math.PI/180);
						if(60-i*30>90)
							h = -h;
						lp1[i].topMargin = (int) (rect.top+rect.height()/2-h)-top;
						lp1[i].leftMargin = (int) (rect.left-h1)+left+posx;
						double xd = h1+((double)rect.width()/2);
						animtion[i] = new TranslateAnimation((float) xd,0,(float)h,0);
					}else{
						if(type==0){
							radius = 300;
							posx = 10;
						}
						else if(type==1)
							radius = 200;
						else{
							posx = -5;
							radius = 150;
						}
						double	h1 = radius*Math.sin((60-i*30)*Math.PI/180);
						double	h = radius*Math.sin((30+30*i)*Math.PI/180);
						if(60-i*30>90)
							h = -h;
						lp1[i].topMargin = (int) (rect.top+rect.height()/2-h)-top;
						if(rect.left<width/2)
							lp1[i].leftMargin = (int) (rect.left-h1)+left+posx;
						else
							lp1[i].leftMargin = (int) (rect.left-h1)+left+posx-rect.width();
						double xd = h1+((double)rect.width()/2);
						animtion[i] = new TranslateAnimation((float) xd,0,(float)h,0);
					}
				}
			}
			long offset = i * 200/4;
			AlphaAnimation alph = new AlphaAnimation(0,1);
			alph.setDuration(100);
			animtion[i].setDuration(300);
			set.setStartOffset(offset);
			set.addAnimation(alph);
			set.addAnimation(animtion[i]);
			SwitchAnimationListener li = new SwitchAnimationListener();
			set.setAnimationListener(li);
			set.setInterpolator(new OvershootInterpolator(2.0F));
			mlayout[i].startAnimation(set);
			addView(mlayout[i],lp1[i]);
		}
	}
	
	private class SwitchAnimationListener implements Animation.AnimationListener {
		public void onAnimationStart(Animation animation) {	
			for(int i=0;i<5;i++)
				mlayout[i].setVisibility(View.VISIBLE);
		}

		public void onAnimationEnd(Animation animation) {

		}
		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}
	}
	private int getStatusHeight(Context context){
        int statusHeight = 0;
//        Rect localRect = new Rect();
        if (0 == statusHeight){
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }catch(Exception e){
            	 e.printStackTrace();
            }
        }
        return statusHeight;
    }
}
