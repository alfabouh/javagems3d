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

import javagems3d.system.resources.assets.models.mesh.structures.MeshDataType;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;
import javagems3d.system.resources.assets.models.formats.IFormat;

import java.io.Serializable;

public final class Model<T extends IFormat> implements Serializable, AutoCloseable {
    private static final long serialVersionUID = -228L;
    private final T format;
    private MeshStructure<?> meshStructure;

    @SuppressWarnings("unchecked")
    public Model(Model<?> model) {
        this.format = (T) model.getFormat().copy();
        this.meshStructure = model.getMeshStructure();
    }

    public Model(Model<?> model, T format) {
        this.format = format;
        this.meshStructure = model.getMeshStructure();
    }

    public Model(@NotNull T t, MeshStructure<?> meshStructure) {
        this.format = t;
        this.meshStructure = meshStructure;
    }

    public Model(@NotNull T t) {
        this.format = t;
        this.meshStructure = null;
    }

    public void clear() {
        if (this.getMeshStructure() == null) {
            return;
        }
        this.getMeshStructure().clear();
        this.meshStructure = null;
    }

    @Override
    public void close() {
        this.clear();
    }

    public boolean isValid() {
        return this.getMeshStructure() != null;
    }

    public MeshDataType getMeshDataType() {
        return this.getMeshStructure().getMeshDataType();
    }

    @SuppressWarnings("all")
    public <R extends MeshStructure<?>> R getMeshStructureWithUnSafeCast() {
        try {
            return (R) this.meshStructure;
        } catch (ClassCastException e) {
            throw new JGemsRuntimeException("Unable to cast!\n" + e.getMessage());
        }
    }

    public MeshStructure<?> getMeshStructure() {
        return this.meshStructure;
    }

    public T getFormat() {
        return this.format;
    }
}
