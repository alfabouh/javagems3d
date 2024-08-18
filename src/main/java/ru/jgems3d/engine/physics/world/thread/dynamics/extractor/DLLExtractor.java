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

package ru.jgems3d.engine.physics.world.thread.dynamics.extractor;

import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.service.path.JGemsPath;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public abstract class DLLExtractor {
    public static void extractDll(Path pathToFile, String bits, String meta) throws IOException {
        String file = String.format("Windows%s%sSp_bulletjme.dll", bits, meta);
        pathToFile.toFile().mkdirs();

        if (new File(pathToFile.toString(), file).exists()) {
            return;
        }

        try (InputStream is = JGems3D.loadFileFromJar(new JGemsPath("assets", "dlls", file))) {
            Files.copy(is, Paths.get(pathToFile.toString(), file), StandardCopyOption.REPLACE_EXISTING);
            JGemsHelper.getLogger().log("Extracted DLL " + file);
        }
    }
}
