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

package ru.jgems3d.engine.system.resources.assets.models.helper.forms.D2;

import org.joml.Vector2f;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.helper.forms.BasicMesh;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.Mesh;

public class VectorModel2D implements BasicMesh<Format2D> {
    private final Vector2f v1;
    private final Vector2f v2;

    public VectorModel2D(Vector2f v1, Vector2f v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public Model<Format2D> generateModel() {
        return new Model<>(new Format2D(), this.generateMesh());
    }

    @Override
    public Mesh generateMesh() {
        Mesh mesh = new Mesh();
        mesh.pushPosition(this.v1.x);
        mesh.pushPosition(this.v1.y);

        mesh.pushPosition(this.v2.x);
        mesh.pushPosition(this.v2.y);

        mesh.pushIndex(0);
        mesh.pushIndex(1);

        mesh.bakeMesh();
        return mesh;
    }
}
