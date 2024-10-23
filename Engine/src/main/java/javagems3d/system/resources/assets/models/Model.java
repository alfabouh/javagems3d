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

package javagems3d.system.resources.assets.models;

import javagems3d.system.resources.assets.models.mesh.Mesh;
import org.jetbrains.annotations.NotNull;
import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.models.formats.IFormat;
import javagems3d.system.resources.assets.models.mesh.MeshGroup;

import java.io.Serializable;

public final class Model<T extends IFormat> implements Serializable, AutoCloseable {
    private static final long serialVersionUID = -228L;
    private final T format;
    private MeshGroup meshGroup;

    @SuppressWarnings("unchecked")
    public Model(Model<?> model) {
        this.format = (T) model.getFormat().copy();
        this.meshGroup = model.getMeshDataGroup();
    }

    public Model(Model<?> model, T format) {
        this.format = format;
        this.meshGroup = model.getMeshDataGroup();
    }

    public Model(@NotNull T t, MeshGroup meshGroup) {
        this.format = t;
        this.meshGroup = meshGroup;
    }

    public Model(@NotNull T t, MeshGroup.Node meshNode) {
        this.format = t;
        this.meshGroup = new MeshGroup();
        this.meshGroup.putNode(meshNode);
    }

    public Model(@NotNull T t, Mesh mesh, Material material) {
        this.format = t;
        this.meshGroup = new MeshGroup();
        this.meshGroup.putNode(new MeshGroup.Node(mesh, material));
    }

    public Model(@NotNull T t, Mesh mesh) {
        this.format = t;
        this.meshGroup = new MeshGroup(mesh);
    }

    public Model(@NotNull T t) {
        this.format = t;
        this.meshGroup = null;
    }

    public boolean isValid() {
        return this.getMeshDataGroup() != null;
    }

    public MeshGroup getMeshDataGroup() {
        return this.meshGroup;
    }

    public int totalMeshGroups() {
        return this.getMeshDataGroup().getModelNodeList().size();
    }

    public T getFormat() {
        return this.format;
    }

    public void clean() {
        if (this.getMeshDataGroup() == null) {
            return;
        }
        this.getMeshDataGroup().clean();
        this.meshGroup = null;
    }

    @Override
    public void close() {
        this.clean();
    }
}
