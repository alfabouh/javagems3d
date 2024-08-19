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

package jgems_api.example.map;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render.RenderProp;
import ru.jgems3d.engine.graphics.opengl.rendering.items.props.SceneProp;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.system.map.MapInfo;
import ru.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderAttributes;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderData;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.toolbox.map_sys.save.objects.MapProperties;
import ru.jgems3d.toolbox.map_sys.save.objects.map_prop.FogProp;
import ru.jgems3d.toolbox.map_sys.save.objects.map_prop.SkyProp;

public class TestMap implements IMapLoader {
    @Override
    public void createMap(GameResources globalResources, GameResources localResources, PhysicsWorld world, SceneWorld sceneWorld) {
        MeshDataGroup meshDataGroup = localResources.createMesh(new JGemsPath(JGems3D.Paths.MODELS, "sponza/sponza.obj"));
        sceneWorld.addObjectInWorld(new SceneProp(new RenderProp(), new Model<>(new Format3D(new Vector3f(), new Vector3f(), new Vector3f(0.01f)), meshDataGroup),
                new MeshRenderData(new MeshRenderAttributes().setAlphaDiscard(0.7f), JGemsResourceManager.globalShaderAssets.world_gbuffer)));
    }

    @Override
    public void postLoad(PhysicsWorld world, SceneWorld sceneWorld) {
        JGemsHelper.CAMERA.enableFreeCamera(JGemsHelper.CONTROLLER.getCurrentController(), new Vector3f(), new Vector3f());
    }

    @Override
    public @NotNull MapInfo getLevelInfo() {
        SkyProp skyProp = new SkyProp();
        skyProp.setSunPos(new Vector3f(0.35f, 1.0f, 0.125f));
        skyProp.setSunColor(new Vector3f(1.0f, 0.95f, 0.91f));

        FogProp fogProp = new FogProp();
        return new MapInfo(new MapProperties("sponza", skyProp, fogProp));
    }
}