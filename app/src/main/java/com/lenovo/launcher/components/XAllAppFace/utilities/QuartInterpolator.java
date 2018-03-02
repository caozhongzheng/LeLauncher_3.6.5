package com.lenovo.launcher.components.XAllAppFace.utilities;

import android.view.animation.Interpolator;

public class QuartInterpolator implements Interpolator {
    
    /**
     * value is 0
     */
    public static final byte IN = 0;
    /**
     * value is 1
     */
    public static final byte OUT = 1;
    /**
     * value is 2
     */
    public static final byte INOUT = 2;
    
    byte _mode = 0;
    
    /**
     * @param mode one of {@link #IN}, {@link #OUT} or {@link #INOUT}
     */
    public QuartInterpolator(byte mode) {
        if (mode > -1 && mode < 3) {
            _mode = mode;
        } else {
            throw new IllegalArgumentException("The mode must be 0, 1 or 2. See the doc");
        }
    }

    @Override
    public float getInterpolation(float input) {
        switch (_mode) {
        case IN:
            return input * input * input * input;
        case OUT:
            return -((input-=1)*input*input*input - 1);
        case INOUT:
            if ((input *= 2) < 1) return 0.5f * input * input * input * input;
            return -0.5f * ((input -= 2) * input * input * input - 2);
        }
        return input;
    }

}
