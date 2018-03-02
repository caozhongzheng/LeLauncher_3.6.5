package com.lenovo.launcher2.customizer;

public class LauncherPersonalSettings {
	public static final String SLIDEEFFECT_NORMAL = "NONE";
	public static final String SLIDEEFFECT_SCALE = "SCALE";
	public static final String SLIDEEFFECT_ROTATE = "ROTATE";
	public static final String SLIDEEFFECT_BOUNCE = "BOUNCE";
	public static final String SLIDEEFFECT_BULLDOZE = "BULLDOZE";
	public static final String SLIDEEFFECT_ROLL = "ROLL";
   /*** AUT:zhaoxy . DATE:2012-03-05 . START***/
	public static final String SLIDEEFFECT_WILD = "WINDMILL";
	public static final String SLIDEEFFECT_SWEEP = "SWEEP";
	public static final String SLIDEEFFECT_WORM = "WORM";
   /*** AUT:zhaoxy . DATE:2012-03-05 . END***/
	public static final String SLIDEEFFECT_CUBE = "CUBE";
	public static final String SLIDEEFFECT_WAVE = "WAVE";
	/* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-04-10 . START */
	public static final String SLIDEEFFECT_WAVE_2 = "WAVE2";
	public static final String SLIDEEFFECT_CHARIOT = "CHARIOT";
	public static final String SLIDEEFFECT_SNAKE = "SNAKE";
	/* RK_ID: RK_SLIDEEFFECT. AUT: liuli1 . DATE: 2012-04-10 . END */
	/** AUT: henryyu1986@163.com DATE: 2012-1-12 S */
    //cancel roll by xingqx for bug on roll
	public static final String SLIDEEFFECT_CYLINDER = "CYLINDER";
	public static final String SLIDEEFFECT_SPHERE = "SPHERE";
	public static final String SLIDEEFFECT_RANDOM = "RANDOM";
    public static final String[] SLIDEEFFECT_ARRAY = { SLIDEEFFECT_NORMAL, SLIDEEFFECT_SCALE, SLIDEEFFECT_ROTATE,
            SLIDEEFFECT_BOUNCE, SLIDEEFFECT_BULLDOZE, /* SLIDEEFFECT_ROLL*, */SLIDEEFFECT_WILD, SLIDEEFFECT_CUBE,
            SLIDEEFFECT_WAVE, SLIDEEFFECT_WAVE_2 };

	/** AUT: henryyu1986@163.com DATE: 2012-1-12 E */
	
	public static String mWorkspaceSlideEffectValue = SLIDEEFFECT_ROTATE;
	public static String mMenuSlideEffectValue = SLIDEEFFECT_SCALE;
	public static String PREF_SLIDE_EFFECT_INDEX = "Slide_effect_index";
	public static String PREF_SLIDE_MODE_INDEX = "Slide_mode_index";

	public static String PREF_SLIDE_EFFECTS = "pref_slide_effect";
	public static String PREF_SLIDE_CIRCLE = "pref_slide_circle";
	public static String PREF_LOTUS_ANIMATION = "pref_lotus_animation";
	public static String PREF_WEATHER_ANIMATION = "pref_weatheranimation";
	public static boolean mSlideCircle;
	public static boolean mLotusAnimation;
	public static boolean mWeatherAnimation;
	
	public static final String APPLIST_EDIT_STYLE_SCALE = "SCALE";
	public static final String APPLIST_EDIT_STYLE_ROTATE_Z = "ROTATE-Z";
	public static final String APPLIST_EDIT_STYLE_ALPHA = "ALPHA";
	public static final String APPLIST_EDIT_STYLE_TRANSLATE = "TRANSLATE";
	public static final String APPLIST_EDIT_STYLE_JUMP = "JUMP";
	private LauncherPersonalSettings() {
		// TODO Auto-generated constructor stub
	}
}
