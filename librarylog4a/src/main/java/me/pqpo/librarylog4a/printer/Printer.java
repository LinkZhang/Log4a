package me.pqpo.librarylog4a.printer;

/**
 * Created by pqpo on 2017/11/16.
 */
public interface Printer {

    void print(int logLevel, String tag, String msg);
    void flush();
    void release();

}
