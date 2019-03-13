package me.pqpo.librarylog4a.printer;

import java.util.ArrayList;
import java.util.List;

import me.pqpo.librarylog4a.Level;
import me.pqpo.librarylog4a.formatter.Formatter;
import me.pqpo.librarylog4a.interceptor.Interceptor;

/**
 * Created by pqpo on 2017/11/16.
 */
public class AndroidPrinter extends AbsPrinter {

    private final boolean mEnable;
    private Formatter formatter;

    protected AndroidPrinter(Builder builder) {
        setLevel(builder.level);
        addInterceptor(builder.interceptors);
        setFormatter(builder.formatter);
        mEnable = builder.enable;
    }

    public void setFormatter(Formatter formatter) {
        if (formatter != null) {
            this.formatter = formatter;
        }
    }

    @Override
    protected void log(int logLevel, String tag, String msg) {
        if (mEnable) {
            if (formatter == null) {
                android.util.Log.println(logLevel, tag, msg);
            } else {
                formatter.format(logLevel, tag, msg);
            }
        }
    }

    public static class Builder {

        private int level = Level.VERBOSE;
        private List<Interceptor> interceptors;
        private Formatter formatter;
        private boolean enable;


        public Builder setFormatter(Formatter formatter) {
            this.formatter = formatter;
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

        public AndroidPrinter create() {
            return new AndroidPrinter(this);
        }

        public Builder setEnable(boolean enable) {
            this.enable = enable;
            return this;
        }
    }

}
