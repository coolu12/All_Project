package com.warehouse.utils;

import java.util.Random;
/**
 * Utility class for generating verification codes.
 * @author Lu Cheng
 */
public class VerifyCodeUtils {

    private static Random random = new Random();
    /**
     * Generates a random four-digit verification code.
     *
     * @return A string representing the generated verification code.
     */
    public static String generateVerifyCode() {
        int r= 1000 + random.nextInt(9000);
        String s = String.valueOf(r);

        return s ;
    }
}