package ru.jgems3d.engine.system.resources.assets.shaders;

import ru.jgems3d.engine.JGems;
import ru.jgems3d.engine.system.files.JGPath;

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
    private final JGPath pathToShader;
    private final ShaderType shaderType;
    private String shaderText;

    public Shader(ShaderType shaderType, JGPath pathToShader) {
        this.shaderType = shaderType;
        this.pathToShader = pathToShader;
        this.uniforms = new ArrayList<>();
        this.structs = new HashMap<>();
        this.shaderText = "";
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

    private void loadStructs(JGPath shaderPath) {
        try (InputStream inputStream = JGems.loadFileJar(new JGPath(shaderPath, this.getShaderType().getFile()))) {
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

    private String loadStream(JGPath shaderPath) {
        StringBuilder shaderSource = new StringBuilder();
        try (InputStream inputStream = JGems.loadFileJar(new JGPath(shaderPath, this.getShaderType().getFile()))) {
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
            System.err.println(ex.getMessage());
        }
        return shaderSource.toString();
    }

    private StringBuilder fillShader(String shaderStream) {
        StringBuilder shader = new StringBuilder();
        shader.append(Shader.VERSION);
        shader.append(shaderStream);
        return shader;
    }

    public List<Uniform> getUniforms() {
        return this.uniforms;
    }

    public String getShaderText() {
        return this.shaderText;
    }

    public JGPath getShaderPath() {
        return this.pathToShader;
    }

    public String toString() {
        return this.getShaderPath() + " - " + this.getShaderType().getFile();
    }

    public ShaderType getShaderType() {
        return this.shaderType;
    }

    public enum ShaderType {
        FRAGMENT("/fragment.frag", ShaderType.FRAGMENT_BIT),
        VERTEX("/vertex.vert", ShaderType.VERTEX_BIT),
        GEOMETRIC("/geometric.geom", ShaderType.GEOMETRIC_BIT),
        COMPUTE("/compute.comp", ShaderType.COMPUTE_BIT);
        public static final int
                FRAGMENT_BIT = (1 << 2),
                VERTEX_BIT = (1 << 3),
                GEOMETRIC_BIT = (1 << 4),
                COMPUTE_BIT = (1 << 5),

                DEFAULT = FRAGMENT_BIT | VERTEX_BIT;

        public static final int ALL = FRAGMENT.getBit() | VERTEX.getBit() | GEOMETRIC.getBit() | COMPUTE.getBit();

        public final String file;
        private final int flag;

        ShaderType(String file, int flag) {
            this.file = file;
            this.flag = flag;
        }

        public int getBit() {
            return this.flag;
        }

        public String getFile() {
            return this.file;
        }
    }
}
