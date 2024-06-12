package ru.alfabouh.jgems3d.engine.system.map;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.brush.WorldModeledBrush;
import ru.alfabouh.jgems3d.engine.physics.entities.Materials;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.system.map.loader.IMapLoader;
import ru.alfabouh.jgems3d.engine.system.map.loader.MapInfo;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;

public class Map02 implements IMapLoader {
    public Map02() {
    }

    @Override
    public void onMapUpdate(World world) {
    }

    @Override
    public void addEntities(World world) {
    }

    @Override
    public void addBrushes(World world) {
        WorldModeledBrush worldModeledBrush = new WorldModeledBrush(world, ResourceManager.modelAssets.ground2, RigidBodyObject.PhysProperties.createProperties(Materials.grassGround), "grass");
        JGems.get().getProxy().addItemInWorlds(worldModeledBrush, new RenderObjectData(ResourceManager.renderDataAssets.ground, ResourceManager.modelAssets.ground2));
        worldModeledBrush.setDebugDrawing(false);
    }

    @Override
    public void addLiquids(World world) {
    }

    @Override
    public void addTriggers(World world) {
    }

    @Override
    public void addSounds(World world) {
    }

    @Override
    public void readNavMesh(World world) {
    }

    @Override
    public MapInfo levelInfo() {
        return new MapInfo(new Vector4d(0.0d, 0.0d, 0.0d, 0.0d), false, new Vector3d(0.0d, 1.5d, 0.0d), 1.0f, new Vector3f(1.0f, 1.0f, 1.0f), "map02");
    }
}
