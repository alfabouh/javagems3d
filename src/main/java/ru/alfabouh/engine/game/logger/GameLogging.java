package ru.alfabouh.engine.game.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.GameSystem;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class GameLogging {
    private final Logger log;

    public GameLogging() {
        this.log = LogManager.getRootLogger();
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

    public void error(Exception e) {
        this.log.error("\n****************************************Exception****************************************", e);
        this.log.error("\n****************************************Exception****************************************");
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
                Desktop.getDesktop().open(new File(Game.getGameFilesFolder().toFile(), "/log/"));
            } catch (IOException ignored) {
                Game.getGame().getLogManager().warn("Failed to open logs file");
            }
        });

        JOptionPane.showOptionDialog(null, "[" + Thread.currentThread().getName() + "]: " + msg, GameSystem.ENG_NAME, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{openLogFolderButton}, openLogFolderButton);
    }

    private StringBuilder getStringBuilder(String message, Object[] objects, StackTraceElement[] trace) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append("****************************************");
        stringBuilder.append("\n");
        stringBuilder.append(String.format("* " + message, objects));
        stringBuilder.append("\n");
        for (int i = 2; i < 8 && i < trace.length; i++) {
            stringBuilder.append(String.format("* at %s%s", trace[i].toString(), i == 7 ? "..." : ""));
            stringBuilder.append("\n");
        }
        stringBuilder.append("****************************************");
        return stringBuilder;
    }
}
