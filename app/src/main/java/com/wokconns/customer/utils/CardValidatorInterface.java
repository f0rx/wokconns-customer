package com.wokconns.customer.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public interface CardValidatorInterface {
    String kBlankSpacePattern = " ";
    String kNumberPattern = "^[0-9]*$";
    String kITag = "card-validator";

    default boolean isValidCardNumber(String cardNumber) {
        String clean = cleanCardNumber(cardNumber);
        return onlyDigits(clean) && validateWithLuhnAlgorithm(clean);
    }

    default boolean isValidCardExpiryYear(String cardExpiry) {
        String[] cardExpiryArray = cardExpiry.split("/");
        int expiryYear = Integer.parseInt(cardExpiryArray[1]);

        return isValidYear(expiryYear);
    }

    default boolean validateWithLuhnAlgorithm(String input) {
        String clean = input.trim();

        int sum = 0;
        int length = clean.length();

        for (int i = 0; i < length; i++) {
            // get digits in reverse order
            int digit = Integer.parseInt(clean.split("")[length - i - 1]);

            // every 2nd number multiply with 2
            if (i % 2 == 1)
                digit *= 2;

            sum += digit > 9 ? (digit - 9) : digit;
        }

        return sum % 10 == 0;
    }

    default String cleanCardNumber(String cardNumber) {
        return cardNumber.trim().replaceAll(kBlankSpacePattern, "");
    }

    default int[] cleanExpiryDate(@NotNull String expiryDate) {
        if (expiryDate.trim().isEmpty()) return new int[] {0, 0};

        try {
            String[] cardExpiryArray = expiryDate.split("/");
            int month = Integer.parseInt(cardExpiryArray[0]);
            int year = Integer.parseInt(cardExpiryArray[1]);
            return new int[]{month, year};
        } catch (Throwable e) {
            return new int[] {0, 0};
        }
    }

    default boolean onlyDigits(String input) {
        String clean = input.trim();

        return Pattern.compile(kNumberPattern).matcher(clean).matches();
    }

    default boolean isValidYear(Integer year) {
        if (year < 100 && year >= 0 && onlyDigits(year.toString())) {
            String currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());

            String prefix = currentYear.substring(0, currentYear.length() - 2);

            String fourDigitsYear = prefix + year;

            return Integer.parseInt(fourDigitsYear) >= Integer.parseInt(currentYear);
        }
        return false;
    }
}
