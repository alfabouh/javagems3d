package javagems3d.system.resources.assets.shaders;

public enum ShaderType {
    FRAGMENT("fragment.frag"),
    VERTEX("vertex.vert"),
    GEOMETRIC("geometric.geom"),
    COMPUTE("compute.comp"),
    TESS_CONTROL("tess.tesc"),
    TESS_EVALUATION("tess.tese");

    public final String file;

    ShaderType(String file) {
        this.file = file;
    }

    public String getFile() {
        return this.file;
    }
}
