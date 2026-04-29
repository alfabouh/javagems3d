package javagems3d.physics.world.thread.dynamics.extractor;

import javagems3d.JGems3D;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import javagems3d.system.service.os.OS;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.JGemsPath;
import logger.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public abstract class NativesExtractor {
    public static String extractNativesAndReturnPath(Path pathToFile, OS os) throws IOException {
        Pair<String, String> file = NativesExtractor.getPath(os);
        if (file == null) {
            throw new JGemsIOException("Internal Native lib picking error");
        }
        pathToFile.toFile().mkdirs();
        try (InputStream is = JGems3D.getInputStream(new JGemsPathSource(new JGemsPath(file.first() + file.second()), ISource.Source.INSIDE_JAR))) {
            Path path = Paths.get(pathToFile.toString(), file.second());
            Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
            Log.get().info("Extracted Native: " + file);
            return path.toString();
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    private static Pair<String, String> getPath(OS os) {
        switch (os) {
            case Win64: {
                return new Pair<>("native/windows/x86_64/", "bulletjme.dll");
            }
            case Lin64AMD: {
                return new Pair<>("native/linux/x86_64/", "libbulletjme.so");
            }
            case Lin64ARM: {
                return new Pair<>("native/linux/arm64/", "libbulletjme.so");
            }
        }
        return null;
    }
}