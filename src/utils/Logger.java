package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public static void log(String userId, String action) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        FileHandler.appendLine("activity_log.txt", timeStamp, userId, action);
    }
}