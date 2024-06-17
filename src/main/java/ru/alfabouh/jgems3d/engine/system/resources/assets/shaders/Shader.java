package ru.alfabouh.jgems3d.engine.system.resources.assets.shaders;

import ru.alfabouh.jgems3d.engine.JGems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Shader {
    public static final String VERSION = "#version 460 core\n\n";
    private final Map<String, Set<String>> structs;
    private final List<Uniform> uniforms;
    private final String shaderName;
    private final ShaderType shaderType;
    private String shaderText;

    public Shader(ShaderType shaderType, String shaderName) {
        this.shaderType = shaderType;
        this.shaderName = shaderName;
        this.uniforms = new ArrayList<>();
        this.structs = new HashMap<>();
        this.shaderText = "";
    }

    public Map<String, Set<String>> getStructs() {
        return this.structs;
    }

    public void init() {
        this.loadStructs();
        this.shaderText = this.fillShader(this.loadStream(shaderName)).toString();
    }

    private void loadStructs() {
        try (InputStream inputStream = JGems.loadFileJar("/assets/", shaderName + this.getShaderType().getFile())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            String structName = null;
            Set<String> args = new HashSet<>();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String[] subStrings = line.split(" ");
                if (structName != null && subStrings.length > 1) {
                    String getSubStr = subStrings[1];
                    args.add(getSubStr.replace(";", ""));
                }
                if (subStrings[0].equals("struct")) {
                    structName = subStrings[1];
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

    private String loadStream(String shaderName) {
        StringBuilder shaderSource = new StringBuilder();
        try (InputStream inputStream = JGems.loadFileJar("/assets/", shaderName + this.getShaderType().getFile())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String[] subStrings = line.split(" ");
                if (subStrings.length > 0 && subStrings[0].equals("uniform")) {
                    String arg = subStrings[2].replace(";", "");
                    if (arg.contains("[")) {
                        String name = arg.substring(0, arg.indexOf('['));
                        String cut = arg.substring(arg.indexOf('[') + 1).replace("]", "");
                        Uniform uni = new Uniform(name, Integer.parseInt(cut));
                        Set<String> fields = this.structs.get(subStrings[1]);
                        if (fields != null) {
                            uni.getFields().addAll(fields);
                        }
                        this.getUniforms().add(uni);
                    } else {
                        this.getUniforms().add(new Uniform(subStrings[2].replace(";", ""), 1));
                    }
                }
                shaderSource.append(line).append("\n");
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

    public String getShaderName() {
        return this.shaderName;
    }

    public ShaderType getShaderType() {
        return this.shaderType;
    }

    public enum ShaderType {
        FRAGMENT("/fragment.frag", ShaderType.FRAGMENT_BIT),
        VERTEX("/vertex.vert", ShaderType.VERTEX_BIT),
        GEOMETRIC("/geometric.geom", ShaderType.GEOMETRIC_BIT);
        public static final int
                FRAGMENT_BIT = (1 << 2),
                VERTEX_BIT = (1 << 3),
                GEOMETRIC_BIT = (1 << 4);

        public static final int ALL = FRAGMENT.getBit() | VERTEX.getBit() | GEOMETRIC.getBit();

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
