package logger.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import logger.SystemLogging;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class LoggingManager {
    public static final StringJoiner consoleText = new StringJoiner("\n");
    public static boolean markConsoleDirty;
    private final Logger log;

    public LoggingManager(String loggerName) {
        this.log = loggerName == null ? LogManager.getRootLogger() : LogManager.getLogger(loggerName);
    }

    public Logger getLog() {
        return this.log;
    }

    public static void addTextInConsoleBuffer(CharSequence sequence) {
        LoggingManager.consoleText.add(sequence);
        LoggingManager.markConsoleDirty = true;
    }

    public static String consoleText() {
        return JGemsLogging.consoleText.toString();
    }

    public static void showExceptionDialog(String msg) {
        JButton openLogFolderButton = new JButton("Open logs");
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(SystemLogging.get().getLogManager().getLog().getName());
        FileAppender fileAppender = (FileAppender) loggerConfig.getAppenders().get("FileAppender");
        openLogFolderButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new File(fileAppender.getFileName()));
            } catch (IOException ex) {
                SystemLogging.get().getLogManager().error("Failed to open logs path", ex);
            }
        });

        JTextArea textArea = new JTextArea(10, 50);
        textArea.setEditable(false);
        textArea.setText(msg);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openLogFolderButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog((Frame) null, "Error", false);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.getContentPane().add(contentPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });
    }

    public static void showWindowInfo(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE));
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

    public void error(String message, Object... objects) {
        this.log.error(message, objects);
    }

    public void trace(String message, Object... objects) {
        this.log.trace(message, objects);
    }

    public void info(String message, Object... objects) {
        this.log.info(message, objects);
    }

    public void debug(String message, Object... objects) {
        this.log.debug(message, objects);
    }

    public void warn(String message, Object... objects) {
        this.log.warn(message, objects);
    }

    public void exception(Exception e) {
        this.fatal("Process caught an exception");
        System.err.println("\n****************************************Exception****************************************");
        e.printStackTrace(System.err);
        System.err.println("\n****************************************Exception****************************************");
    }

    public void fatal(String message, Object... objects) {
        this.log.fatal("****************************************");
        this.log.fatal("* " + message, objects);
        this.log.fatal("****************************************");
    }
}
