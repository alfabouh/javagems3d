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

package javagems3d.system.resources.assets.models.helper.forms;

import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.IFormat;
import javagems3d.system.resources.assets.models.mesh.Mesh;

public interface BasicMesh<T extends IFormat> {
    Model<T> generateModel();

    Mesh generateMesh();
}
