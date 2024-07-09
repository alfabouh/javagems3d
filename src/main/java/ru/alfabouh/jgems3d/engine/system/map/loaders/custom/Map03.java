package ru.alfabouh.jgems3d.engine.system.map.loaders.custom;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.models.SceneProp;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.ModelRenderParams;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.objects.RenderProp;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.system.map.MapInfo;
import ru.alfabouh.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.alfabouh.jgems3d.map_sys.save.objects.MapProperties;
import ru.alfabouh.jgems3d.map_sys.save.objects.map_prop.FogProp;
import ru.alfabouh.jgems3d.map_sys.save.objects.map_prop.SkyProp;

public class Map03 implements IMapLoader {
    public Map03() {
    }

    @Override
    public void createMap(World world) {
       //PhysStaticEntity worldModeledBrush = (PhysStaticEntity) new PhysStaticEntity(world, "grass", RigidBodyObject.PhysProperties.createProperties(Materials.grassGround), new Vector3f(0.0f), JGemsResourceManager.modelAssets.sponza).setCanBeDestroyed(false);
       //worldModeledBrush.setScale(new Vector3f(0.01f));
       //JGems.get().getProxy().addItemInWorlds(worldModeledBrush, new RenderObjectData(JGemsResourceManager.renderDataAssets.ground, JGemsResourceManager.modelAssets.sponza));
       //worldModeledBrush.setDebugDrawing(false);

        JGems.get().getScreen().getScene().getSceneWorld().addRenderObjectInScene(new SceneProp(new RenderProp(), new Model<>(new Format3D(new Vector3f(), new Vector3f(), new Vector3f(0.01f)), JGemsResourceManager.modelAssets.sponza), ModelRenderParams.defaultModelRenderConstraints(JGemsResourceManager.globalShaderAssets.world_gbuffer).setAlphaDiscard(0.7f)));
    }

    @Override
    public void postLoad(World world) {
        JGems.get().getScreen().getScene().enableFreeCamera(JGems.get().getScreen().getControllerDispatcher().getCurrentController(), new Vector3f(), new Vector3f());
    }

    @Override
    public MapInfo getLevelInfo() {
        SkyProp skyProp = new SkyProp();
        skyProp.setSunPos(new Vector3f(0.8f, 1.0f, 0.3f));
        return new MapInfo(new MapProperties("default", skyProp, new FogProp()));
    }
}
