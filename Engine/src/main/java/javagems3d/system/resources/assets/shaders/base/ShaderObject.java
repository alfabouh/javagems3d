package javagems3d.system.resources.assets.shaders.base;

import javagems3d.JGems3D;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.shaders.constants.ShaderStaticConstants;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesManager;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesContainer;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrary;
import javagems3d.system.resources.assets.shaders.uniform.Uniform;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import javagems3d.system.service.files.source.JGemsStringSource;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShaderObject {
    public static final String MAIN_PREFIX = "#version 460 core\n\n";
    private final Map<String, Set<String>> structs;
    private final List<Uniform> uniforms;
    private final JGemsPathSource pathToShader;
    private final ShaderType shaderType;
    private final ShaderLibrariesManager shaderLibrariesManager;
    private final ShaderStaticConstants shaderStaticConstants;
    private String shaderText;

    public ShaderObject(@NotNull JGemsPathSource pathToShader, ShaderStaticConstants shaderStaticConstants, ShaderLibrariesManager shaderLibrariesManager, ShaderType shaderType) {
        this.shaderType = shaderType;
        this.pathToShader = pathToShader;
        this.uniforms = new ArrayList<>();
        this.structs = new HashMap<>();
        this.shaderLibrariesManager = shaderLibrariesManager;
        this.shaderStaticConstants = shaderStaticConstants;
        this.shaderText = "";
    }

    public static boolean checkIfShaderExistsFile(@NotNull JGemsPathSource directoryPath, ShaderType shaderType) {
        return JGems3D.checkIfFileExists(new JGemsPathSource(new JGemsPath(directoryPath.getPath(), shaderType.getFile()), directoryPath.getSource()));
    }

    public Map<String, Set<String>> getStructs() {
        return this.structs;
    }

    public void init() {
        this.shaderText = this.fillShader(this.readShaderText(this.getShaderPath()));
        this.loadStructs(this.getShaderText());
        this.loadUniforms(this.getStructs(), this.getShaderText());
    }

    public void clear() {
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

    private void loadUniforms(Map<String, Set<String>> structs, String shaderText) {
        String[] lines = shaderText.split("\n");
        Pattern uniformPattern = Pattern.compile("\\s*(?:layout\\s*\\([^)]*\\)\\s*)?uniform\\s+(\\w+)\\s+(\\w+)(\\[\\s*\\d+\\s*])?\\s*;?\\s*");

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
                if (arraySize != null) {
                    size = Integer.parseInt(arraySize.replaceAll("[\\[\\]\\s]", ""));
                }

                Set<String> fields = structs.get(type);
                if (fields != null) {
                    if (size > 1) {
                        for (int i = 0; i < size; i++) {
                            for (String field : fields) {
                                this.getUniforms().add(new Uniform(name + "[" + i + "]." + field, 1));
                            }
                        }
                    } else {
                        for (String field : fields) {
                            this.getUniforms().add(new Uniform(name + "." + field, 1));
                        }
                    }
                } else {
                    if (size > 1) {
                        for (int i = 0; i < size; i++) {
                            this.getUniforms().add(new Uniform(name + "[" + i + "]", 1));
                        }
                    } else {
                        this.getUniforms().add(new Uniform(name, 1));
                    }
                }
            }
        }
    }


    private String readShaderText(JGemsPathSource shaderPath) {
        return JGemsHelper.files().readTextFromFile(new JGemsPathSource(new JGemsPath(shaderPath.getPath(), this.getShaderType().getFile()), shaderPath.getSource()));
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

            if (this.shaderLibrariesManager == null) {
                throw new JGemsNullException(this + " > Couldn't get global shader library data");
            }
            ShaderLibrariesContainer shaderLibrariesContainer = this.shaderLibrariesManager.getShaderLibrariesContainer(new JGemsStringSource(includePath, this.getShaderPath().getSource()));
            if (shaderLibrariesContainer == null) {
                throw new JGemsNullException(this + " > Couldn't find shader libraries container with key: " + includePath);
            }
            ShaderLibrary shaderLibrary = shaderLibrariesContainer.getShaderLibraryByType(this.getShaderType());
            if (shaderLibrary == null) {
                throw new JGemsNullException(this + " > Couldn't find shader library(" + this.getShaderType() + ") with key: " + includePath);
            }

            processedShader.append(shaderLibrary.libraryText()).append("\n");
            lastEnd = matcher.end();
        }

        processedShader.append(shaderCode.substring(lastEnd));
        return processedShader.toString();
    }

    private String processConstants(ShaderStaticConstants shaderStaticConstants, String shaderCode) {
        String regex = "CONST\\.([A-Z_]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(shaderCode);
        StringBuffer processedCode = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = shaderStaticConstants.getValue(key.replaceAll("CONST.", ""));
            if (value == null) {
                throw new JGemsNullException("Constant not found for key: " + key);
            }
            matcher.appendReplacement(processedCode, value);
        }
        matcher.appendTail(processedCode);
        return processedCode.toString();
    }

    private String fillShader(String shaderStream) {
        String shader = ShaderObject.MAIN_PREFIX + shaderStream;
        shader = this.processIncludes(shader);
        shader = this.processConstants(this.getShaderStaticConstants(), shader);
        shader = shader.replaceAll("/\\*[^*]*\\*+([^/*][^*]*\\*+)*/", "");
        shader += "\n/* Shader:::::" + this.getShaderPath() + " */ \n\n";
        return shader;
    }

    public ShaderStaticConstants getShaderStaticConstants() {
        return this.shaderStaticConstants;
    }

    public List<Uniform> getUniforms() {
        return this.uniforms;
    }

    public String getShaderText() {
        return this.shaderText;
    }

    public JGemsPathSource getShaderPath() {
        return this.pathToShader;
    }

    public String toString() {
        return this.getShaderPath() + " - " + this.getShaderType().getFile();
    }

    public ShaderType getShaderType() {
        return this.shaderType;
    }
}
