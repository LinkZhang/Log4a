package me.pqpo.log4a;

import android.content.Context;

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
 * Created by pqpo on 2017/11/24.
 */
public class LogInit {

    public static final int BUFFER_SIZE = 1024 * 400; //400k

    public static void init(Context context) {
        int level = Level.DEBUG;
        AndroidPrinter androidPrinter = new AndroidPrinter.Builder()
                .setFormatter(new AndroidPrettyFormatter())
                .setEnable(BuildConfig.DEBUG)
                .create();

        File log = FileUtils.getLogDir(context);
        String buffer_path = log.getAbsolutePath() + File.separator + ".logCache";
        String time = new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault()).format(new Date());
        String log_path = log.getAbsolutePath() + File.separator + time + ".txt";
        FilePrinter filePrinter = new FilePrinter.Builder(context)
                .setLogFilePath(log_path)
                .setLevel(level)
                .setBufferFilePath(buffer_path)
                .setFormatter(new DateFileFormatter())
                .setCompress(false)
                .setEnable(BuildConfig.DEBUG)
                .setBufferSize(BUFFER_SIZE)
                .create();
        LogConfig config = new LogConfig.Builder()
                .addPrinter(androidPrinter)
                .addPrinter(filePrinter)
                .build();

        Log4a.init(config);
    }

}
