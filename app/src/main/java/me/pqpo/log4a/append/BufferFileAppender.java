package me.pqpo.log4a.append;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.pqpo.librarylog4a.printer.AbsPrinter;

/**
 * Created by pqpo on 2017/11/28.
 */
public class BufferFileAppender extends AbsPrinter {

    private File logFile;
    private BufferedOutputStream outputStream;
    private int bufferSize;

    public BufferFileAppender(File logFile, int bufferSize) {
        this.logFile = logFile;
        this.bufferSize = bufferSize;
        openFileOutputStream();
    }

    private void openFileOutputStream() {
        if (!logFile.exists()) {
            try {
                if(!logFile.createNewFile()) {
                    return;
                }
                FileOutputStream os = new FileOutputStream(logFile);
                outputStream = new BufferedOutputStream(os, bufferSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void log(int logLevel, String tag, String msg) {
        if (outputStream == null) {
            return;
        }
//        String logStr = String.format("%s/%s: %s\n", Level.getShortLevelName(logLevel), tag, msg);
        try {
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() {
        super.release();
        if (outputStream != null) {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
