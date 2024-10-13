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

package javagems3d.system.resources.assets.shaders.base;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.shaders.library.GlobalShaderLibrary;
import javagems3d.system.resources.assets.shaders.library.ShaderLibrariesContainer;
import javagems3d.system.resources.assets.shaders.library.ShaderLibrary;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.path.JGemsPath;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shader {
    public static final String VERSION = "#version 460 core\n\n";
    private final Map<String, Set<String>> structs;
    private final List<Uniform> uniforms;
    private final JGemsPath pathToShader;
    private final ShaderType shaderType;
    private final GlobalShaderLibrary globalShaderLibrary;
    private String shaderText;

    public Shader(GlobalShaderLibrary globalShaderLibrary, ShaderType shaderType, JGemsPath pathToShader) {
        this.shaderType = shaderType;
        this.pathToShader = pathToShader;
        this.uniforms = new ArrayList<>();
        this.structs = new HashMap<>();
        this.globalShaderLibrary = globalShaderLibrary;
        this.shaderText = "";
    }

    public static boolean checkIfShaderExistsInJar(JGemsPath directoryPath, ShaderType shaderType) {
        return JGems3D.checkFileExistsInJar(new JGemsPath(directoryPath, shaderType.getFile()));
    }

    public Map<String, Set<String>> getStructs() {
        return this.structs;
    }

    public void init() {
        this.shaderText = this.fillShader(this.readShaderText(this.getShaderPath()));
        this.loadStructs(this.getShaderText());
        this.loadUniforms(this.getShaderText());
    }

    public void clean() {
        this.structs.clear();
        this.uniforms.clear();
    }

    private void loadStructs(String shaderText) {
        try {
            BufferedReader reader = new BufferedReader(new StringReader(shaderText));
            String line;
            String structName = null;
            Set<String> args = new HashSet<>();

            Pattern structPattern = Pattern.compile("\\s*struct\\s+(\\w+)\\s*\\{?\\s*");
            Pattern memberPattern = Pattern.compile("\\s*(\\w+)\\s+(\\w+)\\s*;?\\s*");

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                Matcher structMatcher = structPattern.matcher(line);
                if (structMatcher.matches()) {
                    structName = structMatcher.group(1);
                    continue;
                }

                Matcher memberMatcher = memberPattern.matcher(line);
                if (structName != null && memberMatcher.matches()) {
                    String memberName = memberMatcher.group(2);
                    args.add(memberName);
                }

                if (structName != null && line.contains("}")) {
                    this.structs.put(structName, new HashSet<>(args));
                    structName = null;
                    args.clear();
                }
            }

            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void loadUniforms(String shaderText) {
        String[] lines = shaderText.split("\n");
        Pattern uniformPattern = Pattern.compile("\\s*(?:layout\\s*\\([^)]*\\)\\s*)?uniform\\s+(\\w+)\\s+(\\w+)\\s*(\\[\\s*\\d*\\s*\\])?\\s*;?\\s*");

        for (String line : lines) {
            line = line.trim();

            int uniformIndex = line.indexOf("uniform");
            if (uniformIndex != -1) {
                line = line.substring(uniformIndex);
            }

            Matcher uniformMatcher = uniformPattern.matcher(line);
            if (uniformMatcher.matches()) {
                String type = uniformMatcher.group(1);
                String name = uniformMatcher.group(2);
                String arraySize = uniformMatcher.group(3);

                int size = 1;
                if (arraySize != null && !arraySize.isEmpty()) {
                    size = Integer.parseInt(arraySize.replaceAll("[\\[\\]\\s]", ""));
                }

                Uniform uniform = new Uniform(name, size);
                Set<String> fields = this.structs.get(type);
                if (fields != null) {
                    uniform.getFields().addAll(fields);
                }
                this.getUniforms().add(uniform);
            }
        }
    }

    private String readShaderText(JGemsPath shaderPath) {
        StringBuilder textBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(JGems3D.loadFileFromJar(new JGemsPath(shaderPath, this.getShaderType().getFile())), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                textBuilder.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
        return textBuilder.toString();
    }

    private String processIncludes(String shaderCode) {
        String includePattern = "#include\\s+\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(includePattern);
        Matcher matcher = pattern.matcher(shaderCode);
        StringBuilder processedShader = new StringBuilder();

        int lastEnd = 0;

        while (matcher.find()) {
            String includePath = matcher.group(1);

            processedShader.append(shaderCode, lastEnd, matcher.start());

            if (this.globalShaderLibrary == null) {
                throw new JGemsNullException(this + " > Couldn't get global shader library data");
            }
            ShaderLibrariesContainer shaderLibrariesContainer = this.globalShaderLibrary.getShaderLibrariesContainer(new JGemsPath(includePath).getFullPath());
            if (shaderLibrariesContainer == null) {
                throw new JGemsNullException(this + " > Couldn't find shader libraries container with key: " + includePath);
            }
            ShaderLibrary shaderLibrary = shaderLibrariesContainer.getShaderLibraryByType(this.getShaderType());
            if (shaderLibrary == null) {
                throw new JGemsNullException(this + " > Couldn't find shader library(" + this.getShaderType() + ") with key: " + includePath);
            }

            processedShader.append(shaderLibrary.getLibraryText()).append("\n");
            lastEnd = matcher.end();
        }

        processedShader.append(shaderCode.substring(lastEnd));
        return processedShader.toString();
    }

    private String fillShader(String shaderStream) {
        String shader = Shader.VERSION + shaderStream;
        return this.processIncludes(shader);
    }

    public List<Uniform> getUniforms() {
        return this.uniforms;
    }

    public String getShaderText() {
        return this.shaderText;
    }

    public JGemsPath getShaderPath() {
        return this.pathToShader;
    }

    public String toString() {
        return this.getShaderPath() + " - " + this.getShaderType().getFile();
    }

    public ShaderType getShaderType() {
        return this.shaderType;
    }
}
