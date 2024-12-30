package javagems3d.system.resources.assets.models.mesh.structures;

public enum MeshDataType {
    GROUP("_gr"),
    BUFFER("_bff");

    private final String suffix;

    MeshDataType(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }
}
