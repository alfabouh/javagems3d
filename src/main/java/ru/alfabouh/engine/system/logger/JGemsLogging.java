package ru.alfabouh.engine.system.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.EngineSystem;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringJoiner;

public class JGemsLogging {
    private final Logger log;
    public static final StringJoiner consoleText = new StringJoiner("\n");

    public JGemsLogging() {
        this.log = LogManager.getLogger("JGemsLogger");
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
}
