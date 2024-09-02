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

package jgems_api.test.map;

import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.rendering.fabric.objects.render.RenderProp;
import javagems3d.graphics.opengl.rendering.items.props.SceneProp;
import javagems3d.graphics.opengl.world.SceneWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.map.MapInfo;
import javagems3d.system.map.loaders.IMapLoader;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.MeshDataGroup;
import javagems3d.system.resources.assets.models.mesh.data.render.MeshRenderAttributes;
import javagems3d.system.resources.assets.models.mesh.data.render.MeshRenderData;
import javagems3d.system.resources.manager.GameResources;
import javagems3d.system.resources.manager.JGemsResourceManager;
import javagems3d.system.service.path.JGemsPath;
import javagems3d.temp.map_sys.save.objects.MapProperties;
import javagems3d.temp.map_sys.save.objects.map_prop.FogProp;
import javagems3d.temp.map_sys.save.objects.map_prop.SkyProp;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class TestMap implements IMapLoader {
    @Override
    public void createMap(GameResources globalResources, GameResources localResources, PhysicsWorld world, SceneWorld sceneWorld) {
        MeshDataGroup meshDataGroup = localResources.createMesh(new JGemsPath("/assets/models/sponza/sponza.obj"));
        sceneWorld.addObjectInWorld(new SceneProp(new RenderProp(), new Model<>(new Format3D(new Vector3f(), new Vector3f(), new Vector3f(0.01f)), meshDataGroup),
                new MeshRenderData(new MeshRenderAttributes().setAlphaDiscard(0.7f), JGemsResourceManager.globalShaderAssets.world_gbuffer)));
    }

    @Override
    public void postLoad(PhysicsWorld world, SceneWorld sceneWorld) {
        JGemsHelper.CAMERA.enableFreeCamera(JGemsHelper.CONTROLLER.getCurrentController(), new Vector3f(), new Vector3f());
    }

    @Override
    public void preLoad(PhysicsWorld world, SceneWorld sceneWorld) {

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