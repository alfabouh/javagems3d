package logger.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import logger.SystemLogging;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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

    private static void appendException(StringBuilder err, Exception ex) {
        err.append(ex.getClass().getSimpleName());
        String message = ex.getMessage();
        if (message != null && !message.isEmpty()) {
            err.append(": ").append(message);
        }
        err.append(System.lineSeparator());
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace != null) {
            for (StackTraceElement stackTraceElement : stackTrace) {
                err.append("-> ").append(stackTraceElement.getClassName()).append(".").append(stackTraceElement.getMethodName()).append("(").append(stackTraceElement.getFileName()).append(":").append(stackTraceElement.getLineNumber()).append(")").append(System.lineSeparator());
            }
        }
        err.append(System.lineSeparator());
    }

    public static void showExceptionDialog(@Nullable String msg, @NotNull Exception exception) {
        LoggingManager.showExceptionDialog(msg, new ArrayList<Exception>() {{ add(exception); }});
    }

    public static void showExceptionDialog(@Nullable String msg, @NotNull ArrayList<Exception> exceptions) {
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
        textArea.setFont(new Font("Arial", Font.BOLD, 14));
        StringBuilder stringBuilder = new StringBuilder();
        for (Exception e : exceptions) {
            LoggingManager.appendException(stringBuilder, e);
        }

        textArea.setEditable(false);
        textArea.setText(stringBuilder.toString());
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setBackground(Color.LIGHT_GRAY);
        textArea.setCaretPosition(0);
        textArea.setForeground(Color.RED);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JPanel contentPanel = new JPanel(new BorderLayout());
        if (msg != null) {
            final JLabel label = new JLabel("<html>" + msg + "</html>");
            label.setFont(new Font("Arial", Font.BOLD, 14));
            label.setForeground(Color.BLACK);
            contentPanel.add(label, BorderLayout.NORTH);
        }
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

    public void separator() {
        this.log.info("======================================================");
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
