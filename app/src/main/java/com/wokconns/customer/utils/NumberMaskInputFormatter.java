package com.wokconns.customer.utils;

import android.text.Editable;
import android.text.TextWatcher;

public class NumberMaskInputFormatter implements TextWatcher {
    public final int totalCount; // size of pattern 0000-0000-0000-0000 (Usually 23)
    public final int totalDigits; // max numbers of digits in pattern: 0000 x 4 (Usually 19)
    public final int dividerMultiplier; // means divider position is every 5th symbol beginning with 1
    public final char divider; // Can be (-)

    public NumberMaskInputFormatter(int totalCount, int totalDigits, int dividerMultiplier, char divider) {
        this.totalCount = totalCount;
        this.totalDigits = totalDigits;
        this.dividerMultiplier = dividerMultiplier;
        this.divider = divider;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!isInputCorrect(s, totalCount, dividerMultiplier, divider)) {
            s.replace(0, s.length(), buildCorrectString(getDigitArray(s, totalDigits), getDividerPosition(), divider));
        }
    }

    private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
        boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
        for (int i = 0; i < s.length(); i++) { // check that every element is right
            if (i > 0 && (i + 1) % dividerModulo == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }

    /// means divider position is every 4th symbol beginning with 0
    private int getDividerPosition() {
        return this.dividerMultiplier - 1;
    }
}
