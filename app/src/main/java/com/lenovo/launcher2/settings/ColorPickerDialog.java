/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lenovo.launcher2.settings;

import com.lenovo.launcher.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ColorPickerDialog extends Dialog {

	public interface OnColorChangedListener {
        void colorChanged(int color);
    }
	
	public interface OnColorConfirmListener {
        void colorConfirm(int color);
    }
	
	public interface OnColorCanelListener {
        void colorCanel();
    }

	private static int RADIUS = 100;

//    private OnColorChangedListener mListener;
    private int mInitialColor;

    private static class ColorPickerView extends View {
    	private Paint mPaint;  
        private Paint mCenterPaint;  
        private Paint mHSVPaint;  
        private final int[] mColors;  
        private int[] mHSVColors;  
        private boolean mRedrawHSV;  
        private OnColorChangedListener mListener;  
        private int CENTER_RADIUS = 0;
        private int PADDING_TOP = 18;
        private int RECT_TOP = 0;
        private int RECT_HEIGHT = 0;
   
        ColorPickerView(Context c, OnColorChangedListener l, int color) {  
            super(c);  
            mListener = l;  
            mColors = new int[] { 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF,  
                    0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 };  
            Shader s = new SweepGradient(0, 0, mColors, null);  
   
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
            mPaint.setShader(s);  
            mPaint.setStyle(Paint.Style.STROKE);  
            mPaint.setStrokeWidth((RADIUS / (float)2.0));  
   
            mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
            mCenterPaint.setColor(color);  
            mCenterPaint.setStrokeWidth((RADIUS / (float)4.0));  
   
            mHSVColors = new int[] { 0xFF000000, color, 0xFFFFFFFF };  
   
            mHSVPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
            mHSVPaint.setStrokeWidth(10);  
   
            mRedrawHSV = true;  
   
        }  
   
        public void reset() {
            mPaint.setStrokeWidth(RADIUS / 2.0f);
            mCenterPaint.setStrokeWidth(RADIUS / 4.0f - CENTER_RADIUS / 2.0f);
            CENTER_RADIUS = RADIUS / 3;
            RECT_TOP = RADIUS + PADDING_TOP;
            RECT_HEIGHT = RADIUS / 3;
        }
   
        private boolean mTrackingCenter;  
        private boolean mHighlightCenter;  
   
        public int getColor() {  
            return mCenterPaint.getColor();  
        }  
   
        @Override  
        protected void onDraw(Canvas canvas) {  
            float r = RADIUS - mPaint.getStrokeWidth() * 0.5f;  
   
            canvas.translate(RADIUS, RADIUS);  
            int c = mCenterPaint.getColor();  
   
            if (mRedrawHSV) {  
                mHSVColors[1] = c;  
                mHSVPaint.setShader(new LinearGradient(-RADIUS, 0, RADIUS, 0,  
                        mHSVColors, null, Shader.TileMode.CLAMP));  
            }  
   
            canvas.drawOval(new RectF(-r, -r, r, r), mPaint);  
            canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);  
            /*** AUT:zhaoxy . DATE:2012-02-28 . START***/
            canvas.drawRect(new RectF(-RADIUS, RECT_TOP, RADIUS, RECT_TOP + RECT_HEIGHT), mHSVPaint);  
            /*** AUT:zhaoxy . DATE:2012-02-28 . END***/
   
            if (mTrackingCenter) {  
                mCenterPaint.setStyle(Paint.Style.STROKE);  
   
                if (mHighlightCenter) {  
                    mCenterPaint.setAlpha(0xFF);  
                } else {  
                    mCenterPaint.setAlpha(0x80);  
                }  
                canvas.drawCircle(0, 0, CENTER_RADIUS  
                        + mCenterPaint.getStrokeWidth() / 2 - 1, mCenterPaint);  
   
                mCenterPaint.setStyle(Paint.Style.FILL);  
                mCenterPaint.setColor(c);  
            }  
   
            mRedrawHSV = true;  
        }  
   
        @Override  
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        /*** AUT:zhaoxy . DATE:2012-02-28 . START***/
//            setMeasuredDimension(CENTER_X * 2, (CENTER_Y + 25) * 2);  
            reset();
        	setMeasuredDimension(RADIUS * 2, (RADIUS + PADDING_TOP) * 2 + RECT_HEIGHT);
        /*** AUT:zhaoxy . DATE:2012-02-28 . END***/
        }  
   
        private int ave(int s, int d, float p) {  
            return s + java.lang.Math.round(p * (d - s));  
        }  
   
        private int interpColor(int colors[], float unit) {  
            if (unit <= 0) {  
                return colors[0];  
            }  
            if (unit >= 1) {  
                return colors[colors.length - 1];  
            }  
   
            float p = unit * (colors.length - 1);  
            int i = (int) p;  
            p -= i;  
   
            // now p is just the fractional part [0...1) and i is the index  
            int c0 = colors[i];  
            int c1 = colors[i + 1];  
            int a = ave(Color.alpha(c0), Color.alpha(c1), p);  
            int r = ave(Color.red(c0), Color.red(c1), p);  
            int g = ave(Color.green(c0), Color.green(c1), p);  
            int b = ave(Color.blue(c0), Color.blue(c1), p);  
   
            return Color.argb(a, r, g, b);  
        }  
   
        private static final float PI = 3.1415926f;  
   
        @Override  
        public boolean onTouchEvent(MotionEvent event) {  
            float x = event.getX() - RADIUS;  
            float y = event.getY() - RADIUS;  
            /*** AUT:zhaoxy . DATE:2012-02-28 . START***/
            //boolean inCenter = java.lang.Math.sqrt(x * x + y * y) <= CENTER_RADIUS;  
            float l = (float) java.lang.Math.sqrt(x * x + y * y);
            boolean inCenter = l <= CENTER_RADIUS;
            /*** AUT:zhaoxy . DATE:2012-02-28 . END***/
   
            switch (event.getAction()) {  
            case MotionEvent.ACTION_DOWN:  
                mTrackingCenter = inCenter;  
                if (inCenter) {  
                    mHighlightCenter = true;  
                    invalidate();  
                    break;  
                }  
            case MotionEvent.ACTION_MOVE:  
                if (mTrackingCenter) {  
                    if (mHighlightCenter != inCenter) {  
                        mHighlightCenter = inCenter;  
                        invalidate();  
                    }  
                /*** AUT:zhaoxy . DATE:2012-02-28 . START***/
                } else if ((x >= -RADIUS && x <= RADIUS) && (y <= RECT_TOP + RECT_HEIGHT && y >= RECT_TOP)) // see  
                /*** AUT:zhaoxy . DATE:2012-02-28 . END***/
                // if  
                // we're  
                // in  
                // the  
                // hsv  
                // slider  
                {  
                    int a, r, g, b, c0, c1;  
                    float p;  
   
                    // set the center paint to this color  
                    if (x < 0) {  
                        c0 = mHSVColors[0];  
                        c1 = mHSVColors[1];  
                        p = (x + RADIUS) / RADIUS;  
                    } else {  
                        c0 = mHSVColors[1];  
                        c1 = mHSVColors[2];  
                        p = x / RADIUS;  
                    }  
   
                    a = ave(Color.alpha(c0), Color.alpha(c1), p);  
                    r = ave(Color.red(c0), Color.red(c1), p);  
                    g = ave(Color.green(c0), Color.green(c1), p);  
                    b = ave(Color.blue(c0), Color.blue(c1), p);  
   
                    mCenterPaint.setColor(Color.argb(a, r, g, b));  
   
                    mRedrawHSV = false;  
                    invalidate();  
                /*** AUT:zhaoxy . DATE:2012-02-28 . START***/
                } else if (l <= RADIUS) {  
                /*** AUT:zhaoxy . DATE:2012-02-28 . END***/
                    float angle = (float) java.lang.Math.atan2(y, x);  
                    // need to turn angle [-PI ... PI] into unit [0....1]  
                    float unit = angle / (2 * PI);  
                    if (unit < 0) {  
                        unit += 1;  
                    }  
                    mCenterPaint.setColor(interpColor(mColors, unit));  
                    invalidate();  
                }  
                break;  
            case MotionEvent.ACTION_UP:  
                if (mTrackingCenter) {  
                    if (inCenter) {  
                        mListener.colorChanged(mCenterPaint.getColor());  
                    }  
                    mTrackingCenter = false; // so we draw w/o halo  
                    invalidate();  
                }  
                break;  
            default:
				break; 
            }  
            return true;  
        }  
    }

    public ColorPickerDialog(Context context,
                             OnColorChangedListener listener,
                             int initialColor, int theme) {
        super(context, theme);

//        mListener = listener;
        mInitialColor = initialColor;
    }
    
    private OnColorConfirmListener confirmListener;
    private OnColorCanelListener canelListener;

    public void setOnColorConfirmListener(OnColorConfirmListener l) {
    	confirmListener = l;
    }

    public void setOnColorCanelListener(OnColorCanelListener l) {
    	canelListener = l;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* RK_ID: RK_SETTINGS. AUT: liuli1 . DATE: 2012-07-11 . START */
        // fix 167202
        setCanceledOnTouchOutside(true);
        /* RK_ID: RK_SETTINGS. AUT: liuli1 . DATE: 2012-07-11 . END */
        OnColorChangedListener l = new OnColorChangedListener() {
            public void colorChanged(int color) {
                /*** AUT:zhaoxy . DATE:2012-02-28 . START***/
                //mListener.colorChanged(color);
                confirmListener.colorConfirm(color);
                /*** AUT:zhaoxy . DATE:2012-02-28 . END***/
                dismiss();
            }
        };

        Context context = getContext();
        final LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        /*** AUT:zhaoxy . DATE:2012-02-28 . START***/
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        /*** AUT:zhaoxy . DATE:2012-02-28 . END***/
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setLayoutParams(lp);
      
        //layout.setPadding(0, 6, 0, 0);//17); // modify by liuli1
      
        final ColorPickerView color = new ColorPickerView(context, l, mInitialColor);
        
        /* RK_ID: RK_SETTINGS. AUT: liuli1 . DATE: 2012-04-05 . START */

        LinearLayout buttonLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.buttonbar, null);
        buttonLayout.setPadding(0, 10, 0, 0);
        Button ok = (Button) buttonLayout.findViewById(R.id.addfinish);
        Button canel = (Button) buttonLayout.findViewById(R.id.canceladd);
        /* RK_ID: RK_SETTINGS. AUT: liuli1 . DATE: 2012-04-05 . END */

        ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(confirmListener != null) {
					confirmListener.colorConfirm(color.getColor());
				}
			}
		});
        canel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(canelListener != null) {
					canelListener.colorCanel();
				}
			}
		});

        ok.setText(context.getText(R.string.rename_action));
        canel.setText(context.getText(R.string.cancel_action));
        
        //test by dining
        //add the custom title
        LinearLayout titlelayer =  (LinearLayout) this.getLayoutInflater().inflate(R.layout.dialog_center_title, null);
        if(titlelayer!=null){
        	titlelayer.setPadding(0, 0, 0, 20);
        	TextView mDialogTitle=(TextView)titlelayer.findViewById(R.id.dialog_title);
        	if(mDialogTitle != null){
        		mDialogTitle.setText(context.getText(R.string.pref_color_picker_dialog_title));
        		layout.addView(titlelayer);
        	}
        	
        }
        //end test
        layout.addView(color);
        layout.addView(buttonLayout);
        
        setContentView(layout);
        /*** AUT:zhaoxy . DATE:2012-02-27 . START***/
        //setTitle(R.string.pref_color_picker_dialog_title);
        /*** AUT:zhaoxy . DATE:2012-02-27 . END***/
        
        this.setOnShowListener(new OnShowListener() {
            
            @Override
            public void onShow(DialogInterface dialog) {
                int w = getWindow().getDecorView().getWidth();
                RADIUS = w / 4;
                layout.requestLayout();
            }
        });
    }

}