package com.lenovo.launcher2.customizer;


import java.io.IOException;
import java.io.InputStream;
import com.lenovo.launcher.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.ShapeDrawable;
//import android.graphics.drawable.shapes.OvalShape;
//import android.graphics.drawable.shapes.RectShape;
import android.util.Log;
import android.util.TypedValue;
/**
 * @Description <p>get circle icon </p>
 * @author  xhm
 * @Create Date:2011-6-2
 * @Modified By:xhm
 * @Modified Date:2011-7-28
 * @Why  
 * @Version 1.2
 */
public final class LenovoIconConvert implements ImageConvert {
	//private int[] backGroundColor = { R.drawable.black,R.drawable.blue,R.drawable.green,R.drawable.orange,R.drawable.pink,R.drawable.purple,R.drawable.white,R.drawable.yellow};
	private Bitmap[] backBitMaps = null;
	
	private Bitmap shadow = null;
	private Bitmap light = null;
	private boolean isInited = false;
    private int sIconWidth = -1;
	private int sIconHeight = -1;
    private int sIconTextureWidth = -1;
    private int sIconTextureHeight = -1;
    /*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . START***/
    private static int sIconSWidth = -1;
    private static int sIconSHeight = -1;
    /*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . END***/
    private int skipAlpha = 0;
    private int skipRound = 0;
    private TypedValue value = new TypedValue();
    private BitmapFactory.Options opts = new BitmapFactory.Options();
    private int pixelNums[];
    private byte bitCompareFlag[][] = new byte[2][];
    private Bitmap compare[] = new Bitmap[2];
    private byte bitCompareFlagLe[][] = new byte[2][];
    private double radiusLe[][] = new double[2][];
    private double radiusRound[][] = new double[2][];
    private Bitmap compareLe[] = new Bitmap[2];
    private int compareAlphaLe [][] =new int[2][];
    double crLe[] = new double[2];
    
    private int skipOutRoundboder = 0;
    private int skipInnerRoundboder = 0;
    private int skipAlphaBoder = 0;
    private int maxCenterMatchPix = 0;
    private int maxBoderMatchPix = 0;
    

	private int[] fitWidths;
	private int[] fitHeights;
	
	double maxFlagAlphaCr = 0;
    private final static LenovoIconConvert convert = new LenovoIconConvert();
    private LenovoIconConvert() {
    }
    /**
     * 
     * 
     */
	public void init(Context context) {
		if(!isInited) {
			Resources resources = context.getResources();
			opts.inScaled = false;
			/*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . START***/
			//sIconWidth = sIconHeight =  resources.getInteger(R.integer.config_iconWidth/*android.R.dimen.app_icon_size*/);
		    //sIconTextureWidth = sIconTextureHeight =  resources.getInteger(R.integer.config_IconTextureWidth);//sIconWidth + 2;
			sIconWidth = sIconHeight = (int) resources.getDimensionPixelSize(R.dimen.app_icon_size);
		    sIconSWidth = sIconSHeight = SettingsValue.RES_ICON_TEXTURE_SIZE;
		    pixelNums = new int[]{sIconSWidth * sIconSHeight,sIconSWidth * sIconSHeight * 9 /4};
		    /*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . END***/
		    skipRound =  resources.getInteger(R.integer.config_skipRound);
		    skipAlpha = resources.getInteger(R.integer.config_skipAlpha);
		    skipOutRoundboder =  resources.getInteger(R.integer.config_skipOutRoundboder);
		    skipInnerRoundboder =  resources.getInteger(R.integer.config_skipInnerRoundboder);
		    skipAlphaBoder =  resources.getInteger(R.integer.config_skipAlphaBoder);
		    maxCenterMatchPix =  resources.getInteger(R.integer.config_maxCenterMatchPix);
		    maxBoderMatchPix = resources.getInteger(R.integer.config_maxBoderMatchPix);
		    /*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . START***/
		    fitWidths  = new int[]{sIconSWidth,sIconSWidth*3/2};
		    fitHeights  =  new int[]{sIconSHeight,sIconSHeight*3/2};
		    /*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . END***/
			InputStream is = resources.openRawResource(R.raw.icon_background, value);
			int[] lens = { 6180, 9623, 9328, 9200, 9143, 9660, 6511, 9335,5903, 6540 };
			/* */
			try {
				backBitMaps = new Bitmap[lens.length-2];
				is.read(new byte[16],0,16);
				int redCount = 1024;
				int readTotal = 0;
				for(int i =0;i<lens.length;i++) {
					byte b[] = new byte[lens[i]];
					readTotal = 0;
					redCount = 1024;
					while(readTotal < lens[i]) {
						if(readTotal + redCount > lens[i])
							redCount = lens[i] - readTotal;
						redCount = is.read(b,readTotal,redCount);
						readTotal += redCount;
					}
					if(i == lens.length - 1)
						light = getFitSizeBitmap(resources, b,lens[i], sIconWidth, sIconHeight);
					else if(i == lens.length - 2) {
						shadow = getFitSizeBitmap(resources, b, lens[i],sIconWidth, sIconHeight);
					}
					else
					backBitMaps[i] = getFitSizeBitmap(resources, b,lens[i], sIconWidth, sIconHeight);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			isInited = true;
		}
	}
	/**
	 * 
	 */
	public int[] getMaxAlpha(Context context,Bitmap icon) {
		int result[] = {0,0,0,0};
		int index = 0;
		//Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.shadow);
		int pixels[] = new int[pixelNums[index]];
		icon.getPixels(pixels, 0, fitWidths[index], 0, 0, fitWidths[index], fitHeights[index]);
		for(int i = 0; i< pixelNums[index];i++) {
//			int x = i % fitWidths[index];
			int y = i / fitWidths[index];
			int alpha = pixels[i]>>>24;
			if(alpha > 0) {
				result[0] = y;
				break;
			}
		}
		for(int i = pixelNums[index] -1; i>=0;i--) {
//			int x = i % fitWidths[index];
			int y = i / fitWidths[index];
			int alpha = pixels[i]>>>24;
			if(alpha > 0) {
				result[1] =  fitWidths[index] - 1 - y;
				break;
			}
		}
		int left = fitWidths[index];
		for(int i = 0; i< pixelNums[index];i++) {
			int x = i % fitWidths[index];
//			int y = i / fitWidths[index];
			int alpha = pixels[i]>>>24;
			if(alpha > 0) {
				if(left > x)
					left = x;
			}
		}
		result[2] = left;
		int right = fitWidths[index];
		for(int i = 0; i< pixelNums[index];i++) {
			int x = i % fitWidths[index];
//			int y = i / fitWidths[index];
			int alpha = pixels[i]>>>24;
			if(alpha > 0) {
				if(right > ( fitWidths[index] - 1 - x))
					right = fitWidths[index] - 1 - x;
			}
		}
		result[3] = right;
		return result;
	}
	/**
	 */
	private int isFitLeStyle(int index,Context context,Bitmap icon) {			
		int boderNotfit = 0;
		int centNotFit = 0;		
		byte [] isC = getCompareFlagLe(context,index);
		int pixels[] = new int[pixelNums[index]];
		int skipBoder = 4;
		int skipBoders[] = {skipBoder,skipBoder*3/2};
		int skipCenter = 3;
		int skipCenters[] = {skipCenter,skipCenter*3/2};
		double cr = fitWidths[index] / 2.0;
		double maxCenLen = cr;
		icon.getPixels(pixels, 0, fitWidths[index], 0, 0, fitWidths[index], fitHeights[index]);
		for(int i = 0; i< pixelNums[index];i++) {
//			int x = i % fitWidths[index];
//			int y = i / fitWidths[index];
			//double r = Math.sqrt(Math.pow(Math.abs(x-cx),2)+Math.pow( Math.abs(y-cy),2));
			int alpha = pixels[i]>>>24;
			if(alpha > 0 && maxCenLen < radiusRound[index][i]) {
				maxCenLen = radiusRound[index][i];
			}
			if(radiusLe[index][i] < crLe[index] - skipBoders[index] -1) {
				if(alpha == 0) {
					centNotFit++;
					//Log.d("isFitLeStyle","radiusLe[index][i]="+radiusLe[index][i]+" crLe[index]="+crLe[index]+" alpha="+alpha);
				}
				continue;
			}
			if(radiusLe[index][i] <= crLe[index]+skipBoders[index] && radiusLe[index][i] >= crLe[index] -1) {
				continue;
			}
			if(radiusRound[index][i] <= crLe[index] -skipCenters[index] -1/*cr - skipBoders[index]*/) {
				if(alpha == 0) {
					centNotFit++;
					//Log.d("isFitLeStyle","alpha="+alpha+" radiusRound["+index+"][i]="+radiusRound[index][i]+" crLe[index]="+crLe[index]);
				}
				continue;
			}
			
			if(radiusRound[index][i] > maxFlagAlphaCr + skipCenters[index]) {
				if(alpha > skipAlpha) {
					boderNotfit++;
					//Log.d("isFitLeStyle","alpha="+alpha+" radiusRound[index][i]="+radiusRound[index][i]+" temp="+maxFlagAlphaCr+" skipAlpha="+skipAlpha);
				}
				continue;
			}
			if(alpha == 0)
				continue;
			//if(compareAlphaLe[index][i] +10< alpha)
			switch(isC[i]) {
				//case 1: if(alpha > 0)boderNotfit++;
				//	break;
				//case 2: if(alpha > 20){boderNotfit++;Log.d("isFitLeStyle","alpha="+alpha+" isC[i]="+isC[i]+" radiusRound[index][i]="+radiusRound[index][i]);}
				//	break; 
				case 2:
				case 3: if(radiusRound[index][i] > cr && alpha > 55){
					boderNotfit++;
					//Log.d("isFitLeStyle","alpha="+alpha+" isC[i]="+isC[i]+" radiusRound[index][i]="+radiusRound[index][i]+" y="+y+" x="+x);
					}
					break;
				case 4: if((alpha > 80  && radiusRound[index][i] < crLe[index] -1)|| (alpha < 60 && radiusRound[index][i] >= cr + skipBoders[index] *5)){boderNotfit++;
				//Log.d("isFitLeStyle","index="+index+" alpha="+alpha+" isC[i]="+isC[i]+" radiusRound[index][i]="+radiusRound[index][i]);
				}
					break;
				case 5: //if(alpha == 0)centNotFit++;
					break;
				default:
					break;
			}
			
		}
		//if(!(boderNotfit == 0 && centNotFit == 0))
		//	Log.d("isFitLeStyle","boderNotfit="+boderNotfit+" centNotFit="+centNotFit+" maxCenLen="+maxCenLen);
		if(boderNotfit <= maxBoderMatchPix && centNotFit <= maxCenterMatchPix) {
			return -1;
		}
		return (int)Math.ceil(maxCenLen);
	}
	/**
	 *
	 * @param backColor 0 ~ 6  black,blue,green,orange,pink,purple,white,yellow
	 * @param icon
	 * @param context android.content.Context
	 * 
	 */
	public Bitmap getFitLeIcon(Context context,Bitmap icon,int backColor) {
		int width = icon.getWidth();
		int heigth = icon.getHeight();
		int index = -1;
		if(width == sIconWidth && heigth == sIconWidth) {
			index = 0;
		}
		else if(width == sIconWidth*3/2 && heigth == sIconWidth*3/2) {
			index = 1;
		}
		/*if(true) {
			return getCompareObjLe(context,0);
		}*/
		int maxCenLen = 0;
		if(index >=0) {
			maxCenLen = isFitLeStyle(index, context, icon);
			if(maxCenLen < 0) {
				if(index==0)
					return icon;
				return getFitSizeBitmap(context.getResources(), icon, sIconWidth, sIconWidth);
			}
		}
		 if(index < 0){
			 index = 0;
			icon = getFitSizeBitmap(context.getResources(), icon, sIconWidth, sIconWidth);
			maxCenLen = isFitLeStyle(index, context, icon);
			if(maxCenLen < 0)
				return icon;
			//return getCompareObjLe(context,0);
		}

		double scale = crLe[index] / maxCenLen;
		//double v = (double)fitWidths[index] * scale ;		
		int newWidth = (int)Math.floor(sIconWidth * scale);
		float newCr = ((float)newWidth) / 2;
		if(newCr >= crLe[0] - 2)
			newWidth -= 2;
		if(newWidth % 2 != 0)
			newWidth --;
		icon = getFitSizeBitmap(context.getResources(), icon,0,0, newWidth, newWidth);
		int x = (int)(44 - newCr);
		int y = (int)(44 - newCr);
		return drawNewIcon(icon, context, backColor, x,y,new Canvas());
		//return drawNewIcon(icon, context, backColor, 0,0,new Canvas());
	}
	
	@Deprecated
	/**
	 *
	 */
	public Bitmap getFitLeIcon1(Context context,Bitmap icon,int backColor,boolean isCheckLeIcon) {
		int width = icon.getWidth();
		int heigth = icon.getHeight();
		int index = 0;
		boolean isFitSize = true;
		if(width == sIconWidth && heigth == sIconWidth) {
			index = 0;
		}
		else if(width == sIconWidth*3/2 && heigth == sIconWidth*3/2) {
			index = 1;
			//return getCompareObj(context, 0);
		}
		else  {
			icon = getFitSizeBitmap(context.getResources(), icon, sIconWidth, sIconWidth);
			width = sIconWidth;
			isFitSize = false;
			//return icon;
		}
		final double cx = (fitWidths[index] - 1) / 2.0;
		final double cy = (fitHeights[index] - 1) / 2.0;
		double cr = fitWidths[index] / 2.0;
		double boder[] = {skipAlphaBoder,(double)skipAlphaBoder*3/2};
		
		double maxCenLen = cr;
		
		int boderNotfit = 0;
		int centNotFit = 0;

		
		byte [] isC = getCompareFlag(context,index);
		int pixels[] = new int[pixelNums[index]];
		
		icon.getPixels(pixels, 0, width, 0, 0, width, width);
		for(int i = 0; i< pixelNums[index];i++) {
			/*
        	 */
			int x = i % fitWidths[index];
			int y = i / fitWidths[index];
			double r = Math.sqrt(Math.pow(Math.abs(x-cx),2)+Math.pow( Math.abs(y-cy),2));
			if(isCheckLeIcon){
				if(isC[i] == 2) {
					int alpha = pixels[i]>>>24;
					if(alpha <= 0)
						continue;
	
					
					if(isFitSize && r <= cr + boder[index]) {
						if(alpha > skipAlpha) {
							boderNotfit++;
						}
					}
					else {
						boderNotfit++;
					}
					if(maxCenLen < r)
						maxCenLen = r;
					
				}
				else if(isC[i] == 3) {
					int alpha = pixels[i]>>>24;
					if(alpha == 0) {
						centNotFit++;
					}
				}
			}
			else {
				int alpha = pixels[i]>>>24;
				if(alpha == 0)
					continue;
				if(maxCenLen < r)
					maxCenLen = r;
			}
		}
		
		if(centNotFit <1 && isCheckLeIcon &&(boderNotfit < 1 || (isFitSize && maxCenLen <= cr + skipRound))) {
			//if(index > 0) return getFitSizeBitmap(context.getResources(), icon, sIconWidth, sIconWidth);
			//return icon;
			if(index > 0) icon = getFitSizeBitmap(context.getResources(), icon, sIconWidth, sIconWidth);
			return drawNewIcon(icon, context, backColor, 0,0,new Canvas());
		}
		//int width = (int)Math.floor((float)(centX*sIconWidth)/(float)maxCenLen);
		//int gap = getGap(context);
		/*double R = centX *Math.sqrt(2);
		double cgap = (centX*(R - maxCenLen))/(Math.sqrt(2)*maxCenLen);
		int width = centX*sIconWidth/maxCenLen;*/
		width = sIconWidth - skipInnerRoundboder - skipOutRoundboder;
		int newWidth = Math.abs((int)android.util.FloatMath.floor((float)(cr*width)/(float)maxCenLen));
		if(newWidth %2 == 1)
			newWidth--;
		//width = width - skipRound * 2;
		//icon = getFitSizeBitmap(context.getResources(), icon, newWidth, newWidth);
		//double gap = ((double) sIconWidth / Math.sqrt(2) - maxCenLen) / Math.sqrt(2);
		icon = getFitSizeBitmap(context.getResources(), icon,0,0, newWidth, newWidth);
		//icon = cuteImage(null, icon, (int)cgap,(int)cgap, (int)(width-cgap), (int)(width-cgap));
		
		//int gap = (int)((sIconWidth - (width-2*cgap))/2);//(int)Math.ceil((double)(maxCenLen - centX)/Math.sqrt(2));
		//int width = sIconWidth - (gap * 2);
//		int y = (sIconWidth - newWidth) /2 -skipInnerRoundboder/2;
//		int x = y +3;
		return drawNewIcon(icon, context, backColor, 6,2,new Canvas());
		//cut pic here
		//canvas
		//if()
		//return result;
	}

	public Bitmap cuteImage(Paint paint, Bitmap imgBit, int x,int y, int width, int height) {
		if(paint != null)
			paint.setAlpha(0);
		Bitmap dist = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(dist);
		canvas.clipRect(0, 0, width, height);
		canvas.drawBitmap(imgBit, x, y, paint);
		return dist;
	}
	private byte [] getCompareFlagLe(Context context,int index) {
		if(bitCompareFlagLe[index] != null)
			return bitCompareFlagLe[index];
		getCompareFlag(context, index);
		bitCompareFlagLe[index] = new byte [pixelNums[index]];
		radiusLe[index] = new double [pixelNums[index]];
		compareAlphaLe[index] = new int [pixelNums[index]];
		compareLe[index] = Bitmap.createBitmap(fitWidths[index],fitHeights[index] ,Bitmap.Config.ARGB_8888);
		Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.flag);
		if(icon.getWidth() != fitWidths [index]) {
			icon = this.getFitSizeBitmap(context.getResources(), icon, fitWidths[index], fitHeights[index]);
		}
		final float gaps [] = {10,10*3/2};
		final float cx = (fitWidths[index] - 1) / 2.0f;
		final float cy = (fitHeights[index] - 1 - gaps[index]) / 2.0f;
		crLe[index] = ((double)(fitWidths[index] -gaps[index]))/ 2.0;
		int pixels[] = new int[pixelNums[index]];
		icon.getPixels(pixels, 0, fitWidths[index], 0, 0, fitWidths[index], fitHeights[index]);
		for(int i = 0 ; i< pixelNums[index];i++) {
			int x = i % fitWidths[index];
			int y = i / fitWidths[index];
			double r = Math.sqrt(Math.pow(Math.abs(x-cx),2)+Math.pow( Math.abs(y-cy),2));
			radiusLe[index][i] = r;
			int alpha = pixels[i]>>>24;
			compareAlphaLe[index][i] = alpha;
			if(r < crLe[index]) {
				compareLe[index].setPixel(x, y, Color.RED);
				bitCompareFlagLe[index][i] = 5;
			}
			else if(alpha == 0) {
				compareLe[index].setPixel(x, y, Color.WHITE);
				bitCompareFlagLe[index][i] = 1;
			}
			else if(alpha > 0 && alpha <= 20) {
				if(maxFlagAlphaCr < radiusRound[index][i])
					maxFlagAlphaCr = radiusRound[index][i];
				compareLe[index].setPixel(x, y, Color.BLACK);
				bitCompareFlagLe[index][i] = 2;
			}
			else if(alpha > 20 && alpha <= 35) {
				compareLe[index].setPixel(x, y, Color.GREEN);
				bitCompareFlagLe[index][i] = 3;
			}
			else if(alpha > 35 && alpha <= 60) {
				compareLe[index].setPixel(x, y, Color.YELLOW);
				bitCompareFlagLe[index][i] = 4;
			}
			else if(alpha > 60) {
				compareLe[index].setPixel(x, y, Color.RED);
				bitCompareFlagLe[index][i] = 5;
			}
		}
		Log.d("isFitLeStyle","temp="+maxFlagAlphaCr);
		return bitCompareFlagLe[index];
	}
//	private Bitmap getCompareObjLe(Context context,int index) {
//		getCompareFlagLe(context,index);
//		return compareLe[index];
//	}
//	private Bitmap getCompareObj(Context context,int index) {
//		getCompareFlag(context,index);
//		return compare[index];
//	}
	private byte [] getCompareFlag(Context context,int index) {
		if(bitCompareFlag[index] != null)
			return bitCompareFlag[index];
		bitCompareFlag[index] = new byte [pixelNums[index]];
		radiusRound[index] = new double [pixelNums[index]];
		compare[index] = Bitmap.createBitmap(fitWidths[index],fitHeights[index] ,Bitmap.Config.ARGB_8888);
		/*
    	 */
		final double outRoundboder[] = {skipOutRoundboder,(double)skipOutRoundboder*3/2};
		final double innerRoundboder[] = {skipInnerRoundboder,(double)skipInnerRoundboder*3/2};
		final double cx = (fitWidths[index] - 1) / (double)2.0;
		final double cy = (fitHeights[index] - 1) / (double)2.0;
		double cr = fitWidths[index] / 2.0;
		
		for(int i = 0 ; i< pixelNums[index];i++) {
			int x = i % fitWidths[index];
			int y = i / fitWidths[index];
			double r = Math.sqrt(Math.pow(Math.abs(x-cx),2)+Math.pow( Math.abs(y-cy),2));
			radiusRound[index][i] = r;
			if(((r <= (cr + outRoundboder[index])) && r >=(cr - innerRoundboder[index])) 
					|| Math.round(r) == Math.round(cr + outRoundboder[index])
					|| Math.round(r) == Math.round(cr - innerRoundboder[index])
					|| Math.floor(r) == Math.floor(cr + outRoundboder[index])
					|| Math.floor(r) == Math.floor(cr - innerRoundboder[index])
					|| Math.ceil(r) == Math.ceil(cr - innerRoundboder[index])
					) {
				compare[index].setPixel(x, y, Color.RED);
				bitCompareFlag[index][i] = 1;
			}
			else if(r < cr - innerRoundboder[index]) {
				compare[index].setPixel(x, y, Color.BLUE);
				bitCompareFlag[index][i] = 3;
			}
			else { 
				compare[index].setPixel(x, y, Color.GREEN);
				bitCompareFlag[index][i] = 2;
			}
		}
		
		return bitCompareFlag[index];
	}
//	private byte [] getCompareFlag2(Context context,int index) {
//		if(bitCompareFlag[index] != null)
//			return bitCompareFlag[index];
//		//int pixelNums [] = {pixelNum,pixelNum*9/4};
//		int width = fitWidths[index] ;//+ skipRound*2;
//		
//		/*  */
//    	ShapeDrawable d = new ShapeDrawable(new OvalShape ());
//    	int gap = getGap(context);
//    	//int width = sIconWidth + skipRound*2;//= sIconWidth - 2 * gap + 4;
//    	int skipRW = width + skipRound*2;
//    	d.getShape().resize(skipRW, skipRW);
//        d.getPaint().setColor(Color.RED);
//    	Bitmap oval = Bitmap.createBitmap(skipRW, skipRW,
//                Bitmap.Config.ARGB_8888);   	
//        d.draw(new Canvas(oval));
//        
//        /*  */
//        ShapeDrawable r = new ShapeDrawable(new RectShape());
//        r.getShape().resize(fitWidths[index], fitHeights[index]);
//        r.getPaint().setColor(Color.GREEN);
//        
//        compare[index] = Bitmap.createBitmap(fitWidths[index],fitHeights[index] ,Bitmap.Config.ARGB_8888);
//        r.draw(new Canvas(compare[index]));
//        //new BitmapDrawable(color.white);
//        //tmp = LeIconUtil.getFitSizeBitmap(context.getResources(), backGroundColor[Math.abs(random.nextInt())%backGroundColor.length], 50, 50);
//        //tmp = BitmapFactory.decodeResource(getResources(),backGroundColor[Math.abs(random.nextInt())%backGroundColor.length]);
//        new Canvas(compare[index]).drawBitmap(oval, -skipRound,-skipRound, null);
//        if(skipRound > 0) {
//        	/*  */
//        	skipRW = width - skipRound*2;
//        	d.getShape().resize(skipRW, skipRW);
//            d.getPaint().setColor(Color.BLUE);
//            oval = Bitmap.createBitmap(skipRW, skipRW,
//                    Bitmap.Config.ARGB_8888);   	
//            d.draw(new Canvas(oval));
//            new Canvas(compare[index]).drawBitmap(oval, skipRound,skipRound, null);
//        }
//        //new Canvas(compare).drawBitmap(tmp, (sIconWidth - width)/2, (sIconWidth - width)/2, null);
//    	
//        int pixels[] = new int[pixelNums[index]];
//        bitCompareFlag[index] = new byte [pixelNums[index]];
//        compare[index].getPixels(pixels, 0, fitWidths[index], 0, 0, fitWidths[index], fitHeights[index]);
//        int other = 0;
//        for(int i = 0; i < pixelNums[index];i++) {
//        	/*
//        	 */
//        	if( pixels[i] ==  Color.RED) {
//        		bitCompareFlag[index][i] = 1;
//        	}
//        	else if(pixels[i] == Color.GREEN ) {
//        		bitCompareFlag[index][i] = 2;
//        	}
//        	else if(pixels[i] == Color.BLUE) {
//        		bitCompareFlag[index][i] = 3;
//        	}
//        	else {
//        		bitCompareFlag[index][i] = 4;
//        		other++;
//        	}
//        }
//        return bitCompareFlag[index];   	
//    
//	}
	/**
	 * @param resources android.content.res.Resources
	 * @param bitmap 
	 * @param x 
	 * @param y 
	 * @param width 
	 * @param heitht 
	 * @return
	 */
	public Bitmap getFitSizeBitmap(Resources resources, Bitmap bitmap,int x,int y,
			int width, int heitht) {

		try {
			//opts.inTargetDensity = value.density;
			if (width != bitmap.getWidth()) {
				Matrix matrix = new Matrix();
				float scaleX = ((float) width) / bitmap.getWidth();
				float scaleY = ((float) heitht) / bitmap.getHeight();
				matrix.postScale(scaleX, scaleY);
				return Bitmap.createBitmap(bitmap, x, y, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);
				// return bitmap.copy(Bitmap.Config.ARGB_8888, true);
			}
			return bitmap.copy(Bitmap.Config.ARGB_8888, true);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	
	}
	/**
	 * 
	 * @param resources android.content.res.Resources
	 * @param bitmap 
	 * @param width 
	 * @param heitht 
	 * @return 
	 */
	public Bitmap getFitSizeBitmap(Resources resources, Bitmap bitmap,
			int width, int heitht) {
		return getFitSizeBitmap(resources,bitmap,0,0,width,heitht);
	}
	/**
	 * 
	 * @param resources android.content.res.Resources
	 * @param lbytes 
	 * @param width 
	 * @param heitht
	 * @return 
	 */
	public Bitmap getFitSizeBitmap(Resources resources, byte[] lbytes,int length,int width, int heitht) {
		try {//, 0, lbytes.length, opts
			Bitmap bitmap = BitmapFactory.decodeByteArray(lbytes,0,length,opts);
			return getFitSizeBitmap(resources, bitmap,0,0, width, heitht);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 
	 * @param resources android.content.res.Resources
	 */
	public Bitmap getFitSizeBitmap(Resources resources, int id, int width, int heitht) {
		InputStream is = resources.openRawResource(id, value);
		try {
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);
			return getFitSizeBitmap(resources, bitmap,0,0, width, heitht);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		// BitmapFactory.decodeResource(resources,id,opts);
	}
	
	/**
	 */
	private Bitmap drawNewIcon(Bitmap bitmap,Context context,int backColor,int x,int y,Canvas canvas) {
		backColor = Math.abs(backColor) % backBitMaps.length;
		Bitmap background = backBitMaps[backColor];
		final Bitmap result = Bitmap.createBitmap(sIconWidth, sIconHeight,
                Bitmap.Config.ARGB_8888);

		
		//final Canvas canvas = new Canvas(result);
		canvas.setBitmap(result);
		Paint p = new Paint(); 
		//canvas.drawBitmap(getCompareObjLe(context, 0),0 ,0, p);
		canvas.drawBitmap(shadow,0 ,0, p);
		canvas.drawBitmap(background,0 ,0, p);
		canvas.drawBitmap(bitmap,x ,y, p);
		canvas.drawBitmap(light,0 ,0, p);
		return result;
	}
	/**
	 */
//	private Bitmap drawNewIcon(Bitmap bitmap,Context context,int backColor,int gap,Canvas canvas) {
//		return drawNewIcon(bitmap, context, backColor, gap, gap, canvas);
//	}
//	/**
//	 * @param context android.content.Context
//	 */
//	private int getGap(Context context) {
//		int width = sIconWidth;
//		double smallWidth = (float)width / Math.sqrt(2);
//		return (int)(Math.ceil((width  - smallWidth)/2));
//	}
    public int getsIconWidth() {
		return sIconWidth;
	}
	public int getsIconHeight() {
		return sIconHeight;
	}
	public int getsIconTextureWidth() {
		return sIconTextureWidth;
	}
	public int getsIconTextureHeight() {
		return sIconTextureHeight;
	}

	
	/**
	 * @param context android.content.Context
	 * 
	 */
	public Bitmap convert(Context context, Bitmap bitmap, int backGround) {
		if(!this.isInited) {
			this.init(context);
		}
		return this.getFitLeIcon(context, bitmap, backGround);
	}

    private Bitmap drawNewIcon(Bitmap bitmap, Context context, Bitmap bg, int x, int y, Canvas canvas) {
        //bugfix zhanglz1
    	int textureWidth = //sIconTextureWidth;
        		sIconWidth;
        int textureHeight =// sIconTextureHeight;
        		sIconHeight;
        if (Utilities.FLAG_DRAWABLE_PADDING) {
            // added by liuli1, for bug 7079.
            textureWidth += 2;
            textureHeight += 2;
        }
    	final Bitmap result = Bitmap.createBitmap(textureWidth, textureHeight, Bitmap.Config.ARGB_8888);

        // final Canvas canvas = new Canvas(result);
        canvas.setBitmap(result);
        int width = sIconWidth;
        int height = sIconHeight;
        final int left = (textureWidth-width) / 2;
        final int top = (textureHeight-height) / 2;
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        bd.setBounds(left, top, left+width, top+height);
        bd.setBounds(bd.getBounds());
    	
        // final Canvas canvas = new Canvas(result);
        canvas.setBitmap(result);
        Paint p = new Paint();
        // canvas.drawBitmap(getCompareObjLe(context, 0),0 ,0, p);
//        canvas.drawBitmap(shadow, 0, 0, p);
        if (bg != null) {
            bg = getFitSizeBitmap(context.getResources(), bg,0,0, sIconWidth, sIconHeight);
            canvas.drawBitmap(bg, 0, 0, p);
        }
        canvas.drawBitmap(bitmap, left+x, top+y, p);
        canvas.setBitmap(null);

//        canvas.drawBitmap(light, 0, 0, p);
        return result;
    }
	
	   /**
    *
    * @param backColor 0 ~ 6  black,blue,green,orange,pink,purple,white,yellow
    * @param icon
    * @param context android.content.Context
    * 
    */
   public Bitmap getFitLeIcon(Context context,Bitmap icon,Bitmap bg) {
       int width = icon.getWidth();
       int heigth = icon.getHeight();
       int index = -1;
       /*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . START***/
       if(width == sIconSWidth && heigth == sIconSWidth) {
           index = 0;
       }
       else if(width == sIconSWidth*3/2 && heigth == sIconSWidth*3/2) {
           index = 1;
       }
       /*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . END***/
       /*if(true) {
           return getCompareObjLe(context,0);
       }*/
       int maxCenLen = 0;
       if(index >=0) {
           maxCenLen = isFitLeStyle(index, context, icon);
           if(maxCenLen < 0) {
               if(index==0)
                   /*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . START***/
                   return null;
                   /*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . END***/
               }
       }
        if(index < 0){
            index = 0;
           icon = getFitSizeBitmap(context.getResources(), icon, sIconWidth, sIconWidth);
           maxCenLen = isFitLeStyle(index, context, icon);
           if(maxCenLen < 0)
               /*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . START***/
               return null;
               /*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . END***/
           //return getCompareObjLe(context,0);
       }

       double scale = crLe[index] / maxCenLen;
       //double v = (double)fitWidths[index] * scale ;     
       int newWidth = (int)Math.floor(sIconWidth * scale);
       float newCr = ((float)newWidth) / 2;
       if(newCr >= crLe[0] - 2)
           newWidth -= 2;
       if(newWidth % 2 != 0)
           newWidth --;
       icon = getFitSizeBitmap(context.getResources(), icon,0,0, newWidth, newWidth);
       /*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . START***/
       bg = getFitSizeBitmap(context.getResources(), bg,0,0, sIconWidth, sIconHeight);
       int x = (int)(sIconWidth / 2.0f - newCr);
       int y = (int)(sIconHeight / 2.0f - newCr);
       /*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . END***/
       return drawNewIcon(icon, context, bg, x,y,new Canvas());
   }
	
	   /**
     * @param context android.content.Context
     * 
     */
    public Bitmap convert(Context context, Bitmap bitmap, Bitmap bg) {
    	/***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.S***/       
   	 if (sIconWidth == -1 /*|| sIconWidth != SettingsValue.getIconSizeValue(context)*/ ) {
            initStatics(context);
        }
		fitWidths = new int[] { sIconSWidth, sIconSWidth * 3 / 2 };
		fitHeights = new int[] { sIconSHeight, sIconSHeight * 3 / 2 };
		/***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.E***/       
       
        if(!this.isInited) {
            this.init(context);
        }
        return this.getFitLeIcon(context, bitmap, bg);
    }
    
	/**
	 */
	public static LenovoIconConvert getInstance() {
		return convert;
	}
	/***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.S***/       
	private void initStatics(Context context) {
        int iconsize = SettingsValue.getIconSizeValue(context);
        sIconWidth = sIconHeight = iconsize;
        sIconTextureWidth = sIconTextureHeight = sIconWidth;
        int defaultIconSHeigt = (int) context.getResources().getDimension(R.dimen.app_icon_texture_size);
        sIconSWidth = sIconSHeight = defaultIconSHeigt;
    }
	/***RK_ID:RK_CHANGE_ICON_SIZE AUT:zhanglz1@lenovo.com. DATE:2013-01-14.E***/
	/***RK_ID:RK_SQURE_ICONSTYLE AUT:zhanglz1@lenovo.com DATE: 2013-04-12 S***/ 
	public boolean isCirle(Context context,Bitmap icon){
		if(icon ==null) return false;
		if(!this.isInited) {
			this.init(context);
		}
		if (sIconWidth == -1
				/*|| sIconWidth != SettingsValue.getIconSizeValue(context)*/) {
			initStatics(context);
		}
		int width = icon.getWidth();
		int heigth = icon.getHeight();
		int index = -1;
		if(width == sIconSWidth && heigth == sIconSWidth) {
			index = 0;
		}
		else if(width == sIconSWidth*3/2 && heigth == sIconSWidth*3/2) {
			index = 1;
		}
		int maxCenLen = 0;
		if(index >=0) {
			maxCenLen = isFitLeStyle(index, context, icon);
			if(maxCenLen < 0) {
				if(index==0)
					return true;
				return false;
			}
		}else if(index < 0){
			 index = 0;
			icon = getFitSizeBitmap(context.getResources(), icon, sIconWidth, sIconWidth);
			maxCenLen = isFitLeStyle(index, context, icon);
			if(maxCenLen < 0)
				return true;
			return false;
		}
		return false;
	}
	/***RK_ID:RK_SQURE_ICONSTYLE AUT:zhanglz1@lenovo.com DATE: 2013-04-12 E***/ 
}
