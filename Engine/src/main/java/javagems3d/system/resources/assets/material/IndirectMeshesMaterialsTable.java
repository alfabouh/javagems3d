package javagems3d.system.resources.assets.material;

import java.util.ArrayList;
import java.util.List;

public final class IndirectMeshesMaterialsTable {
    public static final int START_IDX = 0;
    private final List<Material> materialList;

    public IndirectMeshesMaterialsTable() {
        this.materialList = new ArrayList<>();
        this.addMaterial(Material.createDefault());
    }

    public void addMaterial(Material material) {
        this.getMaterialList().add(material);
        material.setId(this.getMaterialList().size() - 1);
    }

    public Material getMaterialByID(int id) {
        return this.getMaterialList().get(id);
    }

    public List<Material> getMaterialList() {
        return this.materialList;
    }
}
