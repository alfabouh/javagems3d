package ru.alfabouh.engine.system.logger;

import org.apache.logging.log4j.Logger;

import java.io.OutputStream;

public class StreamOutputTranslation extends OutputStream {
    private final Logger logger;
    private final StringBuilder builder = new StringBuilder();
    private final boolean error;

    public StreamOutputTranslation(boolean error, Logger logger) {
        this.logger = logger;
        this.error = error;
    }

    @Override
    public void write(int b) {
        char c = (char) b;
        if (c == '\n') {
            this.logAndClear();
        } else {
            this.builder.append(c);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) {
        for (int i = off; i < off + len; i++) {
            this.write(b[i]);
        }
    }

    @Override
    public void write(byte[] b) {
        this.write(b, 0, b.length);
    }

    private void logAndClear() {
        if (this.builder.length() > 0) {
            String s = this.builder.toString();
            if (this.error) {
                this.logger.error(s);
            } else {
                this.logger.info(s);
            }
        }
        this.builder.setLength(0);
    }
}