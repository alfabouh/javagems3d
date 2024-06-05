package ru.alfabouh.engine.system.resources.assets.models;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.resources.assets.materials.Material;
import ru.alfabouh.engine.system.resources.assets.models.formats.IFormat;
import ru.alfabouh.engine.system.resources.assets.models.mesh.Mesh;
import ru.alfabouh.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.engine.system.resources.assets.models.mesh.ModelNode;

public final class Model<T extends IFormat> {
    private final T format;
    private final MeshDataGroup meshDataGroup;

    @SuppressWarnings("unchecked")
    public Model(Model<?> model) {
        this.format = (T) model.getFormat().copy();
        this.meshDataGroup = model.getMeshDataGroup();
    }

    public Model(@NotNull T t, MeshDataGroup meshDataGroup) {
        this.format = t;
        this.meshDataGroup = meshDataGroup;
    }

    public Model(@NotNull T t, ModelNode modelNode) {
        this.format = t;
        this.meshDataGroup = new MeshDataGroup();
        this.meshDataGroup.putNode(modelNode);
    }

    public Model(@NotNull T t, Mesh mesh, Material material) {
        this.format = t;
        this.meshDataGroup = new MeshDataGroup();
        this.meshDataGroup.putNode(new ModelNode(mesh, material));
    }

    public Model(@NotNull T t, Mesh mesh) {
        this.format = t;
        this.meshDataGroup = new MeshDataGroup(mesh);
    }

    public Model(@NotNull T t) {
        this.format = t;
        this.meshDataGroup = null;
    }

    public MeshDataGroup getMeshDataGroup() {
        return this.meshDataGroup;
    }

    public int totalMeshGroups() {
        return this.getMeshDataGroup().getModelNodeList().size();
    }

    public T getFormat() {
        return this.format;
    }

    public void clean() {
        if (this.getMeshDataGroup() == null) {
            JGems.get().getLogManager().warn("Trying to get data from NULL mesh!");
            return;
        }
        this.getMeshDataGroup().getModelNodeList().forEach(e -> e.getMesh().clean());
    }
}