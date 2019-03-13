package me.pqpo.librarylog4a;

import java.util.ArrayList;
import java.util.List;

import me.pqpo.librarylog4a.printer.AndroidPrinter;
import me.pqpo.librarylog4a.printer.Printer;

/**
 * @author linkzhang
 * @describe
 * @date 2019-03-13 11:17
 */

public class LogConfig {
    List<Printer> mPrinters;

    public LogConfig(Builder builder) {
        if (builder.mPrinters.size() == 0){
            mPrinters = new ArrayList<>();
            mPrinters.add(new AndroidPrinter.Builder().create());
        }else {
            mPrinters = builder.mPrinters;
        }
    }

    public static class Builder{

        List<Printer> mPrinters = new ArrayList<>();

        public Builder addPrinter(Printer printer){
            mPrinters.add(printer);
            return this;
        }


        public LogConfig build() {
            return new LogConfig(this);
        }
    }
}
