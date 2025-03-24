package javagems3d.help;

import javagems3d.JGems3D;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class JGemsFilesHelper {
    public static String readTextFromFile(JGemsPath path) {
        StringBuilder textBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(JGems3D.loadFileFromJar(path), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                textBuilder.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
        return textBuilder.toString();
    }

    public static byte[] toByteArray(InputStream inputStream, int size) throws IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] data = new byte[size];
            int bytesRead;
            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            return buffer.toByteArray();
        }
    }

    public static String openFolderViewer(@Nullable String defaultStr) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Choose folder");
        int returnValue = chooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = chooser.getSelectedFile();
            return selectedFolder.getAbsolutePath();
        }
        return defaultStr;
    }
}
