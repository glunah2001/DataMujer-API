package com.UNED.APIDataMujer.exception;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Clase estática simple de utilidad que averigua cual
 * constraint de la base de datos se violó
 * @author AHKolodin
 * */
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
