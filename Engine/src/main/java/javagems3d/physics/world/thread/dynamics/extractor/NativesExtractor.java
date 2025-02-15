/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.physics.world.thread.dynamics.extractor;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.system.os.OS;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;

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
        try (InputStream is = JGems3D.loadFileFromJar(new JGemsPath(file.getFirst() + file.getSecond()))) {
            Path path = Paths.get(pathToFile.toString(), file.getSecond());
            Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
            JGemsHelper.getLogger().info("Extracted Native: " + file);
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
