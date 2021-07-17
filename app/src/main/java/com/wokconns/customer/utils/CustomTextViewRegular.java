package com.wokconns.customer.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by VARUN on 01/01/19.
 */
public class CustomTextViewRegular extends TextView {

    public CustomTextViewRegular(Context context) {

        super(context);
        applyCustomFont(context);
    }

    public CustomTextViewRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CustomTextViewRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    public CustomTextViewRegular(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("Montserrat-Regular.otf", context);
        setTypeface(customFont, Typeface.BOLD);
    }
}
