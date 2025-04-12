package javagems3d.help;

import javagems3d.JGems3D;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import javax.swing.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public abstract class JGemsFilesHelper {
    public static String readTextFromFileInJar(JGemsPath path) {
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

    public static String readTextFromFileOutsideJar(JGemsPath path) {
        StringBuilder textBuilder = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(path.toFile().toPath(), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                textBuilder.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
        return textBuilder.toString();
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] data = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            return buffer.toByteArray();
        }
    }

    public static ByteBuffer toByteBuffer(InputStream inputStream) throws IOException {
        final int BUFFER_SIZE = 8 * 1024;
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(BUFFER_SIZE);

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            if (byteBuffer.remaining() < bytesRead) {
                ByteBuffer newBuffer = MemoryUtil.memAlloc(byteBuffer.capacity() * 2);
                byteBuffer.flip();
                newBuffer.put(byteBuffer);
                MemoryUtil.memFree(byteBuffer);
                byteBuffer = newBuffer;
            }
            byteBuffer.put(buffer, 0, bytesRead);
        }

        byteBuffer.flip();
        return byteBuffer;
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
