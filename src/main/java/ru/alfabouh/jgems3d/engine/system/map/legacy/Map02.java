package ru.alfabouh.jgems3d.engine.system.map.legacy;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.props.PhysStaticProp;
import ru.alfabouh.jgems3d.engine.physics.objects.materials.Materials;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.system.map.MapInfo;
import ru.alfabouh.jgems3d.engine.system.map.legacy.loader.IMapLoader;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;
import ru.alfabouh.jgems3d.mapsys.file.save.objects.MapProperties;
import ru.alfabouh.jgems3d.mapsys.file.save.objects.map_prop.FogProp;
import ru.alfabouh.jgems3d.mapsys.file.save.objects.map_prop.SkyProp;

public class Map02 implements IMapLoader {
    public Map02() {
    }

    @Override
    public void createMap(World world) {
        PhysStaticProp worldModeledBrush = (PhysStaticProp) new PhysStaticProp(world, "grass", RigidBodyObject.PhysProperties.createProperties(Materials.grassGround), new Vector3f(0.0f), ResourceManager.modelAssets.ground2).setCanBeDestroyed(false);
        JGems.get().getProxy().addItemInWorlds(worldModeledBrush, new RenderObjectData(ResourceManager.renderDataAssets.ground, ResourceManager.modelAssets.ground2));
        worldModeledBrush.setDebugDrawing(false);
    }

    @Override
    public MapInfo getLevelInfo() {
        return new MapInfo(new MapProperties("default", new SkyProp(), new FogProp()));
    }
}
