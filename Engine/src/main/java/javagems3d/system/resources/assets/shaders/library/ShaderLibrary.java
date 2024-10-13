package javagems3d.system.resources.assets.shaders.library;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.shaders.base.ShaderType;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ShaderLibrary {
    private final String libraryText;
    private final ShaderType shaderType;

    public ShaderLibrary(ShaderType shaderType, JGemsPath pathToLibrary) {
        this.shaderType = shaderType;
        this.libraryText = this.readLibrary(pathToLibrary);
    }

    private String readLibrary(JGemsPath pathToLibrary) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = JGems3D.loadFileFromJar(new JGemsPath(pathToLibrary, ShaderLibrary.interpretLibStr(this.getShaderType())))) {
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

    public static boolean checkIfShaderExistsInJar(JGemsPath directoryPath, ShaderType shaderType) {
        return JGems3D.checkFileExistsInJar(new JGemsPath(directoryPath, ShaderLibrary.interpretLibStr(shaderType)));
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
