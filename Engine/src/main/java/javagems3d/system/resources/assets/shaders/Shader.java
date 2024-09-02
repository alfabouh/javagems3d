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

package javagems3d.system.resources.assets.shaders;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.shaders.library.GlobalShaderLibrary;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.path.JGemsPath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        this.loadStructs(this.getShaderPath());
        this.shaderText = this.fillShader(this.loadStream(this.getShaderPath())).toString();
    }

    public void clean() {
        this.structs.clear();
        this.uniforms.clear();
    }

    private void loadStructs(JGemsPath shaderPath) {
        try (InputStream inputStream = JGems3D.loadFileFromJar(new JGemsPath(shaderPath, this.getShaderType().getFile()))) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
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
                    String memberType = memberMatcher.group(1);
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
            System.err.println(ex.getMessage());
        }
    }

    private String loadStream(JGemsPath shaderPath) {
        StringBuilder shaderSource = new StringBuilder();
        try (InputStream inputStream = JGems3D.loadFileFromJar(new JGemsPath(shaderPath, this.getShaderType().getFile()))) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;

            Pattern uniformPattern = Pattern.compile("\\s*(?:layout\\s*\\([^)]*\\)\\s*)?uniform\\s+(\\w+)\\s+(\\w+)\\s*(\\[\\s*\\d*\\s*\\])?\\s*;?\\s*");

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                shaderSource.append(line).append("\n");

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
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        return shaderSource.toString();
    }

    private Pair<String, Set<String>> extractIncludes(String in) {
        Set<String> includes = new HashSet<>();
        StringBuilder shader = new StringBuilder();
        shader.append(Shader.VERSION);

        String includePattern = "#include\\s+\"([^\"]+)\"";

        Pattern pattern = Pattern.compile(includePattern);
        Matcher matcher = pattern.matcher(in);

        while (matcher.find()) {
            String includePath = matcher.group(1);
            includes.add(includePath);

            in = matcher.replaceFirst("");
            matcher = pattern.matcher(in);
        }

        shader.append(in);
        return new Pair<>(shader.toString(), includes);
    }

    private String pasteIncludes(Pair<String, Set<String>> setPair) {
        String shader = setPair.getFirst();

        String mainFunctionPattern = "\\bvoid\\s+main\\s*\\(\\s*\\)\\s*\\{";
        Pattern pattern = Pattern.compile(mainFunctionPattern);
        Matcher matcher = pattern.matcher(shader);

        if (matcher.find()) {
            int insertPosition = matcher.start();
            StringBuilder beforeMain = new StringBuilder(shader.substring(0, insertPosition));

            for (String code : setPair.getSecond()) {
                beforeMain.append(code).append("\n");
            }

            beforeMain.append(shader.substring(insertPosition));
            shader = beforeMain.toString();
        }

        return shader;
    }

    private String fillShader(String shaderStream) {
        String shader = Shader.VERSION + shaderStream;
        Pair<String, Set<String>> shader0 = this.extractIncludes(shader);
        return this.pasteIncludes(shader0);
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
