package com.wokconns.customer.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by VARUN on 01/01/19.
 */

public class CustomAutoCompleteTextView extends AutoCompleteTextView {
    public CustomAutoCompleteTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("Montserrat-Light.otf", context);
        setTypeface(customFont);

    }
}
