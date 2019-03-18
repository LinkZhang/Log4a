package me.pqpo.librarylog4a.printer;


import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import me.pqpo.librarylog4a.Level;
import me.pqpo.librarylog4a.LogBuffer;
import me.pqpo.librarylog4a.formatter.Formatter;
import me.pqpo.librarylog4a.interceptor.Interceptor;

/**
 * Created by pqpo on 2017/11/16.
 */
public class FilePrinter extends AbsPrinter {

    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private final boolean mEnable;
    private final String mLogFileDir;
    private String mLogFilePath;
    private LogBuffer logBuffer;
    private static final long DAYS = 24 * 60 * 60 * 1000; //天
    private static final long M = 1024 * 1024; //M
    private static final long DEFAULT_DAY = 7 * DAYS; //默认删除天数
    private static final long DEFAULT_FILE_SIZE = 10 * M;
    private Formatter formatter;

    protected FilePrinter(Builder builder) {
        sDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        mLogFileDir = builder.logFileDir;
        mLogFilePath = new File(mLogFileDir, getCurrentTime() + ".txt").getAbsolutePath();
        logBuffer = new LogBuffer(builder.bufferFilePath, builder.bufferSize, mLogFilePath, builder.compress);
        setMaxSingleLength(builder.bufferSize);
        setLevel(builder.level);
        addInterceptor(builder.interceptors);
        setFormatter(builder.formatter);
        mEnable = builder.enable;
        deleteExpiredFile(System.currentTimeMillis()-DEFAULT_DAY);
    }

    public String getBufferPath() {
        return logBuffer.getBufferPath();
    }

    public int getBufferSize() {
        return logBuffer.getBufferSize();
    }

    public String getLogPath() {
        return logBuffer.getLogPath();
    }

    public void changeLogPath(String logPath) {
        logBuffer.changeLogPath(logPath);
    }

    public void setFormatter(Formatter formatter) {
        if (formatter != null) {
            this.formatter = formatter;
        }
    }

    private void deleteExpiredFile(long deleteTime) {
        File dir = new File(mLogFileDir);
        if (dir.isDirectory()) {
            String[] files = dir.list();
            if (files != null) {
                for (String item : files) {
                    if (TextUtils.isEmpty(item) || new File(item).isDirectory() || !item.endsWith
                            (".txt")) {
                        continue;
                    }
                    String[] longStrArray = item.split("\\.");
                    if (longStrArray.length > 0) {  //小于时间就删除
                        try {
                            long longItem = Long.valueOf(longStrArray[0]);
                            if (longItem <= deleteTime && longStrArray.length == 2) {
                                new File(mLogFileDir, item).delete(); //删除文件
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        }
    }


    @Override
    protected void log(int logLevel, String tag, String msg) {
        if (mEnable) {
            if (logBuffer.getLogPathDirDate() != getCurrentTime()) {
                logBuffer.flushAsync();
                mLogFilePath = new File(mLogFileDir, getCurrentTime() + ".txt").getAbsolutePath();
                logBuffer.changeLogPath(mLogFilePath);
            }
            logBuffer.write(formatter.format(logLevel, tag, msg));
        }
    }

    @Override
    public void flush() {
        super.flush();
        logBuffer.flushAsync();
    }

    @Override
    public void release() {
        super.release();
        logBuffer.release();
    }

    public static class Builder {

        private Context context;

        private String bufferFilePath;
        private String logFileDir;
        private int bufferSize = 4096;
        private int level = Level.VERBOSE;
        private List<Interceptor> interceptors;
        private Formatter formatter;
        private boolean compress;
        private boolean enable;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder setBufferFilePath(String bufferFilePath) {
            this.bufferFilePath = bufferFilePath;
            return this;
        }

        public Builder setLogFileDir(String logFilePath) {
            this.logFileDir = logFilePath;
            return this;
        }

        public Builder setLevel(int level) {
            this.level = level;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            if (interceptors == null) {
                interceptors = new ArrayList<>();
            }
            interceptors.add(interceptor);
            return this;
        }

        public Builder setFormatter(Formatter formatter) {
            this.formatter = formatter;
            return this;
        }

        public Builder setCompress(boolean compress) {
            this.compress = compress;
            return this;
        }

        public FilePrinter create() {
            if (logFileDir == null) {
                throw new IllegalArgumentException("logFilePath cannot be null");
            }
            if (bufferFilePath == null) {
                bufferFilePath = getDefaultBufferPath(context);
            }
            if (formatter == null) {
                formatter = new Formatter() {
                    @Override
                    public String format(int logLevel, String tag, String msg) {
                        return String.format("%s/%s: %s\n", Level.getShortLevelName(logLevel), tag, msg);
                    }
                };
            }
            return new FilePrinter(this);
        }

        private String getDefaultBufferPath(Context context) {
            File bufferFile;
            if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)
                    && context.getExternalFilesDir("log4a") != null) {
                bufferFile = context.getExternalFilesDir("log4a");
            } else {
                bufferFile = new File(context.getFilesDir(), "log4a");
            }
            if (bufferFile != null && !bufferFile.exists()) {
                bufferFile.mkdirs();
            }
            return new File(bufferFile, ".log4aCache").getAbsolutePath();
        }

        public Builder setEnable(boolean enable) {
            this.enable = enable;
            return this;
        }
    }


    public static long getCurrentTime() {
        long currentTime = System.currentTimeMillis();
        long tempTime = 0;
        try {
            String dataStr = sDateFormat.format(new Date(currentTime));
            tempTime = sDateFormat.parse(dataStr).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempTime;
    }

    public static String getDateStr(long time) {
        return sDateFormat.format(new Date(time));
    }

}
