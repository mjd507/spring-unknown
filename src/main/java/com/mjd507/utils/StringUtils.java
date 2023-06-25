package com.mjd507.utils;

public class StringUtils {
    public static boolean isPalindrome(String candidate) {
        if (candidate == null || candidate.isEmpty()) return false;
        char[] chars = candidate.toCharArray();
        int i = 0, j = chars.length - 1;
        while (i <= j) {
            if (chars[i] != chars[j]) return false;
            i++;
            j--;
        }
        return true;
    }
}
