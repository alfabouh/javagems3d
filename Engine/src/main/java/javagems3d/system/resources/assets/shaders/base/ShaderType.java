package javagems3d.system.resources.assets.shaders.base;

public enum ShaderType {
    FRAGMENT("fragment.frag"),
    VERTEX("vertex.vert"),
    GEOMETRIC("geometric.geom"),
    COMPUTE("compute.comp");

    public final String file;

    ShaderType(String file) {
        this.file = file;
    }

    public String getFile() {
        return this.file;
    }
}
