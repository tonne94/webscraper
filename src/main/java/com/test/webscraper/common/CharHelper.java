package com.test.webscraper.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CharHelper {

    public static String cleanWierdLetters(String input){
        input = input.replaceAll("\\u0111", "dj");
        input = input.replaceAll("\\u0110", "Dj");
        input = input.replaceAll("\\u0106", "C");
        input = input.replaceAll("\\u0107", "c");
        input = input.replaceAll("\\u010C", "C");
        input = input.replaceAll("\\u010D", "c");
        input = input.replaceAll("\\u0160", "S");
        input = input.replaceAll("\\u0161", "s");
        input = input.replaceAll("\\u017D", "Z");
        input = input.replaceAll("\\u017E", "z");
        return input;
    }

    public static Double extractEuroPrice(String input) {
        String[] split = input.split("/");
        String output = split[0].replaceAll("\\.", "").replaceAll("[^0-9\\.]", "");
        return Double.parseDouble(output);
    }
}
