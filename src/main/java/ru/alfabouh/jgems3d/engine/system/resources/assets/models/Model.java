package ru.alfabouh.jgems3d.engine.system.resources.assets.models;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.Material;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.IFormat;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.Mesh;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;

import java.io.Serializable;

public final class Model<T extends IFormat> implements Serializable {
    private static final long serialVersionUID = -228L;
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
            SystemLogging.get().getLogManager().warn("Trying to get data from NULL mesh!");
            return;
        }
        this.getMeshDataGroup().getModelNodeList().forEach(e -> e.getMesh().clean());
    }
}