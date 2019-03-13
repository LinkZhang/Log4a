package me.pqpo.log4a;


import  android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.pqpo.librarylog4a.Level;
import me.pqpo.librarylog4a.Log4a;
import me.pqpo.librarylog4a.LogConfig;
import me.pqpo.librarylog4a.formatter.AndroidPrettyFormatter;
import me.pqpo.librarylog4a.formatter.DateFileFormatter;
import me.pqpo.librarylog4a.printer.AndroidPrinter;
import me.pqpo.librarylog4a.printer.FilePrinter;


/**
 * 打印Log信息工具类
 * 支持打印error,debug,info,warning,verbose等级别日志
 * 支持打印json,array,xml,map等格式日志
 * Created by LinkZhang
 * on 18-03-05.
 */

public class L {

    public static final int BUFFER_SIZE = 1024 * 400; //400k

    public static void init(Context context) {
        AndroidPrinter androidPrinter = new AndroidPrinter.Builder()
                .setFormatter(new AndroidPrettyFormatter(L.class.getName()))
                .setEnable(BuildConfig.DEBUG)
                .create();

        File log = FileUtils.getLogDir(context);
        String buffer_path = log.getAbsolutePath() + File.separator + ".logCache";
        String time = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault()).format(new Date());
        String log_path = log.getAbsolutePath() + File.separator + time + ".txt";
        FilePrinter filePrinter = new FilePrinter.Builder(context)
                .setLogFilePath(log_path)
                .setLevel(Level.DEBUG)
                .setBufferFilePath(buffer_path)
                .setFormatter(new DateFileFormatter())
                .setCompress(false)
                .setEnable(!BuildConfig.DEBUG)
                .setBufferSize(BUFFER_SIZE)
                .create();
        LogConfig config = new LogConfig.Builder()
                .addPrinter(androidPrinter)
                .addPrinter(filePrinter)
                .build();

        Log4a.init(config);
    }

    private static String getLogTag() {
        String result;
        StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[2];
        result = thisMethodStack.getClassName();
        int lastIndex = result.lastIndexOf(".");
        result = result.substring(lastIndex + 1, result.length());
        return result;
    }


    public static void i(String msg, Object... params) {
        L.i(getLogTag(), msg, params);

    }

    public static void i(@NonNull String tag, String msg, Object... params) {
        Log4a.i(tag, String.format(msg, params));
    }

    public static void d(String msg, Object... params) {
        L.d(getLogTag(), msg, params);
    }

    public static void d(@NonNull String tag, String msg, Object... params) {
        Log4a.d(tag, String.format(msg, params));
    }

    public static void w(@NonNull String tag, String msg, Object... params) {
        Log4a.w(tag, String.format(msg, params));
    }

    public static void w(String msg, Object... params) {
        L.w(getLogTag(), msg, params);
    }

    public static void v(String msg, Object... params) {
        L.v(getLogTag(), msg, params);
    }

    public static void v(@NonNull String tag, String msg, Object... params) {
        Log4a.v(tag, String.format(msg, params));
    }

    public static void e(String e) {
        L.e(getLogTag(), e);
    }

    public static void e(Exception e) {
        L.e(getLogTag(), e.toString());
    }

    public static void e(Throwable e) {
        L.e(getLogTag(), e);
    }

    public static void e(@NonNull String tag, Exception e){
        L.e(tag,e.toString());
    }

    public static void e(@NonNull String tag, String e) {
        Log4a.e(tag,e);
    }

    public static void e(@NonNull String tag, Throwable e) {
        Log4a.e(tag,e);
    }

    public static void json(@NonNull String tag, String json) {
        Log4a.json(tag,json);
    }

    public static void json(String json) {
        L.json(getLogTag(), json);
    }

}
