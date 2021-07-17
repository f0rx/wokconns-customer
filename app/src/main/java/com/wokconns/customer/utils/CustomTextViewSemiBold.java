package com.wokconns.customer.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by VARUN on 01/01/19.
 */
public class CustomTextViewSemiBold extends TextView {

    public CustomTextViewSemiBold(Context context) {

        super(context);
        applyCustomFont(context);
    }

    public CustomTextViewSemiBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CustomTextViewSemiBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    public CustomTextViewSemiBold(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("Montserrat-SemiBold.otf", context);
        setTypeface(customFont, Typeface.BOLD);
    }
}
