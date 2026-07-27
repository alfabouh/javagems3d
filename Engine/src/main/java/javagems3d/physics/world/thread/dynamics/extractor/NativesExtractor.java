/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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
    public static String extractNativesAndReturnPath(Path pathToFile, OS os) {
        Pair<String, String> file = NativesExtractor.getPath(os);
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
        return switch (os) {
            case Win64 -> new Pair<>("native/windows/x86_64/", "bulletjme.dll");
            case Lin64AMD -> new Pair<>("native/linux/x86_64/", "libbulletjme.so");
            case Lin64ARM -> new Pair<>("native/linux/arm64/", "libbulletjme.so");
        };
    }
}