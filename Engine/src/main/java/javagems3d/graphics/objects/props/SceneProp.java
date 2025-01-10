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

package javagems3d.graphics.objects.props;

import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.fabric.IRenderFabric;
import org.jetbrains.annotations.NotNull;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

public class SceneProp extends AbstractSceneProp {
    public SceneProp(IRenderFabric renderFabric, Model<Format3D> model, @NotNull RenderAttributes objectRenderingConfiguration) {
        super(renderFabric, model, objectRenderingConfiguration);
    }

    public SceneProp(IRenderFabric renderFabric, Model<Format3D> model, @NotNull JGemsShaderManager shaderManager) {
        super(renderFabric, model, shaderManager);
    }

    @Override
    public boolean isDead() {
        return false;
    }
}
