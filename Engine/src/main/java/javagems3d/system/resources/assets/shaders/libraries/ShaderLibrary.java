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

package javagems3d.system.resources.assets.shaders.libraries;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.shaders.base.ShaderType;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public record ShaderLibrary(String libraryText, ShaderType shaderType) {
    public ShaderLibrary(@NotNull JGemsPathSource libraryText, ShaderType shaderType) {
        this(ShaderLibrary.readLibrary(libraryText, shaderType), shaderType);
    }

    private static String readLibrary(@NotNull JGemsPathSource pathToLibrary, ShaderType shaderType) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = JGems3D.getInputStream(new JGemsPathSource(new JGemsPath(pathToLibrary.getPath(), ShaderLibrary.interpretLibStr(shaderType)), pathToLibrary.getSource()))) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
        return stringBuilder.toString();
    }

    public static boolean checkIfShaderExistsInJar(@NotNull JGemsPathSource directoryPath, ShaderType shaderType) {
        return JGems3D.checkIfFileExists(new JGemsPathSource(new JGemsPath(directoryPath.getPath(), ShaderLibrary.interpretLibStr(shaderType)), directoryPath.getSource()));
    }

    public static String interpretLibStr(ShaderType s) {
        return "lib." + s.getFile();
    }

    @Override
    public @NotNull String toString() {
        return this.libraryText();
    }
}
