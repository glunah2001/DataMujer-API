package com.UNED.APIDataMujer.exception;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstraintUtils {

    public static String extractFieldFromMessage(String message) {
        Pattern pattern = Pattern.compile("for key '(.+?)'");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String keyName = matcher.group(1);
            return keyName.replace("_UNIQUE", "");
        }
        return null;
    }

}
