package com.example.tools;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author: BYDylan
 * @date: 2022/10/17
 * @description:
 */
public class TimeTool {

    public static final long ONE_SEC_MILLIS = 1000;
    public static final long TWO_SEC_MILLIS = 2 * ONE_SEC_MILLIS;
    public static final long TEN_SEC_MILLIS = 10 * ONE_SEC_MILLIS;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentDateTime() {
        return DATE_TIME_FORMATTER.format(LocalDateTime.now());
    }

    public static String convertStandardTime(long time) {
        return DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }

    public static long costTime(long startTime) {
        return (System.currentTimeMillis() - startTime) / 1000;
    }
}
