package ru.alfabouh.jgems3d.logger.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.system.EngineSystem;
import ru.alfabouh.jgems3d.logger.SystemLogging;
import ru.alfabouh.jgems3d.logger.translators.StreamOutputTranslation;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class LoggingManager {
    private final Logger log;
    public static final StringJoiner consoleText = new StringJoiner("\n");

    public LoggingManager(String loggerName) {
        this.log = loggerName == null ? LogManager.getRootLogger() : LogManager.getLogger(loggerName);
        try {
            this.initStreams(this.log);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String consoleText() {
        return JGemsLogging.consoleText.toString();
    }

    private void initStreams(final Logger log) throws IOException {
        try (StreamOutputTranslation streamOutputTranslation = new StreamOutputTranslation(false, log)) {
            System.setOut(new PrintStream(streamOutputTranslation, true));
        }
        try (StreamOutputTranslation streamOutputTranslation = new StreamOutputTranslation(true, log)) {
            System.setErr(new PrintStream(streamOutputTranslation, true));
        }
    }

    public void error(String message, Object... objects) {
        this.log.error(message, objects);
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
                Desktop.getDesktop().open(new File(JGems.getFilesFolder().toFile(), "/log/"));
            } catch (IOException ignored) {
                SystemLogging.get().getLogManager().error("Failed to open logs file");
            }
        });

        final String threadName = Thread.currentThread().getName();
        SwingUtilities.invokeLater(() -> JOptionPane.showOptionDialog(null, "[" + threadName + "]: " + msg, EngineSystem.ENG_NAME, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{openLogFolderButton}, openLogFolderButton));
    }

    public static void showWindowInfo(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message));
    }

    public static boolean showConfirmationWindowDialog(String message) {
        final AtomicInteger integer = new AtomicInteger(0);
        try {
            SwingUtilities.invokeAndWait(() -> {
                integer.set(JOptionPane.showConfirmDialog(null, message));
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return integer.get() == 0;
    }
}
