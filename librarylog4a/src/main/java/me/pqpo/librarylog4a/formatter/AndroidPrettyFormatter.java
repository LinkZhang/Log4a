package me.pqpo.librarylog4a.formatter;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.pqpo.librarylog4a.Level;

/**
 * @author linkzhang
 * @describe
 * @date 2019-03-11 16:59
 */

public class AndroidPrettyFormatter implements Formatter{

    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */


    /**
     * 日志的打印方法名.
     */
    private static final String LOG_PRINT_METHOD_NAME = "logHeaderContent";

    private static final int CHUNK_SIZE = 4000;

    /**
     * The minimum stack trace index, starts at this class after two native calls.
     */
    private static final int MIN_STACK_OFFSET = 5;
    private static final int JSON_INDENT = 2;
    /**
     * Drawing toolbox
     */
    private static final char TOP_LEFT_CORNER = '┌';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char MIDDLE_CORNER = '├';
    private static final char HORIZONTAL_LINE = '│';
    private static final String DOUBLE_DIVIDER = "────────────────────────────────────────────────────────";
    private static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;
    private String mLogClassName;

    public AndroidPrettyFormatter(String name) {
        mLogClassName=name;
    }

    public AndroidPrettyFormatter() {
    }

    public static void log(String tag, int priority, String message) {

    }


    private static void logTopBorder(int logType, String tag) {
        logChunk(logType, tag, TOP_BORDER);
    }


    /**
     * 获取调用日志类输出方法的堆栈
     *
     * @return 堆栈
     */
    private StackTraceElement getTargetStackTraceElement() {
        // find the target invoked method
        StackTraceElement targetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(mLogClassName);
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    private void logHeaderContent(int logType, String tag) {
        String level = "";

        StackTraceElement element = getTargetStackTraceElement();
        StringBuilder builder = new StringBuilder();
        builder.append(HORIZONTAL_LINE)
                .append(' ')
                .append(level)
                .append(getSimpleClassName(element.getClassName()))
                .append(".")
                .append(element.getMethodName())
                .append(" ")
                .append(" (")
                .append(element.getFileName())
                .append(":")
                .append(element.getLineNumber())
                .append(")");
        logChunk(logType, tag, builder.toString());

    }

    private void logBottomBorder(int logType, String tag) {
        logChunk(logType, tag, BOTTOM_BORDER);
    }

    private void logDivider(int logType, String tag) {
        logChunk(logType, tag, MIDDLE_BORDER);
    }

    private static void logContent(int logType, String tag, String chunk) {
        if (logType == Level.JSON) {
            chunk = chunk.trim();
            String message = "";
            try {

                if (chunk.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(chunk);
                    message = jsonObject.toString(JSON_INDENT);
                } else if (chunk.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(chunk);
                    message = jsonArray.toString(JSON_INDENT);
                }

            } catch (JSONException e) {
                message = "";
            }

            if (!TextUtils.isEmpty(message)) {
                chunk = message;
            }
        }
        String[] lines = chunk.split(System.getProperty("line.separator"));
        for (String line : lines) {
            logChunk(logType, tag, HORIZONTAL_LINE + " " + line);
        }
    }

    private static void logChunk(int priority, String tag, String chunk) {
        Log.println(priority > Level.ERROR ? Log.DEBUG : priority, tag, chunk);
    }

    private static String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    @Override
    public String format(int priority, String tag, String message) {
        logTopBorder(priority, tag);
        logHeaderContent(priority, tag);

        //get bytes of message with system's default charset (which is UTF-8 for Android)
        byte[] bytes = message.getBytes();
        int length = bytes.length;
        if (length <= CHUNK_SIZE) {
            logContent(priority, tag, message);
            logBottomBorder(priority, tag);
            return "";
        }

        for (int i = 0; i < length; i += CHUNK_SIZE) {
            int count = Math.min(length - i, CHUNK_SIZE);
            //create a new String with system's default charset (which is UTF-8 for Android)
            logContent(priority, tag, new String(bytes, i, count));
        }
        logBottomBorder(priority, tag);
        return "";
    }
}
