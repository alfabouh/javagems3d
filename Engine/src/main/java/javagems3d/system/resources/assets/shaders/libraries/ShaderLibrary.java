package javagems3d.system.resources.assets.shaders.libraries;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.shaders.base.ShaderType;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import javagems3d.system.service.files.source.JGemsStringSource;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ShaderLibrary {
    private final String libraryText;
    private final ShaderType shaderType;

    public ShaderLibrary(@NotNull JGemsPathSource pathToLibrary, ShaderType shaderType) {
        this.shaderType = shaderType;
        this.libraryText = this.readLibrary(pathToLibrary);
    }

    private String readLibrary(@NotNull JGemsPathSource pathToLibrary) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = JGems3D.getInputStream(new JGemsPathSource(new JGemsPath(pathToLibrary.getPath(), ShaderLibrary.interpretLibStr(this.getShaderType())), pathToLibrary.getSource()))) {
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
    public String toString() {
        return this.getLibraryText();
    }

    public String getLibraryText() {
        return this.libraryText;
    }

    public ShaderType getShaderType() {
        return this.shaderType;
    }
}
