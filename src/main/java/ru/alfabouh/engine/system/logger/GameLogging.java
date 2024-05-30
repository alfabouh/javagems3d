package ru.alfabouh.engine.system.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.EngineSystem;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class GameLogging {
    private final Logger log;

    public GameLogging() {
        this.log = LogManager.getRootLogger();
        try {
            this.initStreams(this.log);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initStreams(final Logger log) throws IOException {
        try (StreamOutputTranslation streamOutputTranslation = new StreamOutputTranslation(log)) {
            System.setOut(new PrintStream(streamOutputTranslation, true));
            System.setErr(new PrintStream(streamOutputTranslation, true));
        }
    }

    public void log(String message, Object... objects) {
        this.log.info(message, objects);
    }

    public void debug(String message, Object... objects) {
        this.log.debug(message, objects);
    }

    public void warn(String message, Object... objects) {
        this.log.warn(message, objects);
    }

    public void exception(Exception e) {
        this.bigWarn("Process caught an exception!");
        System.err.println("\n****************************************Exception****************************************");
        e.printStackTrace(System.err);
        System.err.println("\n****************************************Exception****************************************");
    }

    public void bigWarn(String message, Object... objects) {
        this.log.error("****************************************");
        this.log.error("* " + message, objects);
        this.log.error("****************************************");
    }

    public static void showExceptionDialog(String msg) {
        JButton openLogFolderButton = new JButton("Open logs");
        openLogFolderButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new File(JGems.getGameFilesFolder().toFile(), "/log/"));
            } catch (IOException ignored) {
                JGems.get().getLogManager().warn("Failed to open logs file");
            }
        });

        final String threadName = Thread.currentThread().getName();
        SwingUtilities.invokeLater(() -> JOptionPane.showOptionDialog(null, "[" + threadName + "]: " + msg, EngineSystem.ENG_NAME, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{openLogFolderButton}, openLogFolderButton));
    }

    private static class StreamOutputTranslation extends OutputStream {
        private final Logger logger;
        private final StringBuilder builder = new StringBuilder();

        public StreamOutputTranslation(Logger logger) {
            this.logger = logger;
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
                this.logger.info(this.builder.toString());
            }
            this.builder.setLength(0);
        }
    };
}
