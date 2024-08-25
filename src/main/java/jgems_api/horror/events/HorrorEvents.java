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

package jgems_api.horror.events;

import jgems_api.horror.HorrorGame;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format2D;
import ru.jgems3d.engine.system.resources.assets.models.helper.MeshHelper;
import ru.jgems3d.engine.system.resources.assets.shaders.UniformString;
import ru.jgems3d.engine_api.events.SubscribeEvent;
import ru.jgems3d.engine_api.events.bus.Events;

public class HorrorEvents {
    @SubscribeEvent
    public static void postProcess(Events.RenderPostProcessing event) {
        event.setCancelled(true);
        try (Model<Format2D> screenModel = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(event.windowSize), 0.5f)){
            HorrorGame.get().horrorShaderLoader.post_sh1.bind();
            HorrorGame.get().horrorShaderLoader.post_sh1.getUtils().performOrthographicMatrix(screenModel);
            HorrorGame.get().horrorShaderLoader.post_sh1.performUniformTexture(new UniformString("texture_sampler"), event.sceneBufferTextureID, GL30.GL_TEXTURE_2D);
            JGemsSceneUtils.renderModel(screenModel, GL30.GL_TRIANGLES);
            HorrorGame.get().horrorShaderLoader.post_sh1.unBind();
        }
    }
}
