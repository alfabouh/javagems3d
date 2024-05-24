package ru.alfabouh.engine.game.map;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.map.loader.IMapLoader;
import ru.alfabouh.engine.game.map.loader.MapInfo;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.graph.Graph;
import ru.alfabouh.engine.inventory.items.ItemCrowbar;
import ru.alfabouh.engine.inventory.items.ItemEmp;
import ru.alfabouh.engine.inventory.items.ItemRadio;
import ru.alfabouh.engine.inventory.items.ItemZippo;
import ru.alfabouh.engine.physics.brush.WorldModeledBrush;
import ru.alfabouh.engine.physics.entities.Materials;
import ru.alfabouh.engine.physics.entities.enemy.EntityManiac;
import ru.alfabouh.engine.physics.entities.items.EntityCassetteItem;
import ru.alfabouh.engine.physics.entities.items.EntityCdItem;
import ru.alfabouh.engine.physics.entities.items.EntityItem;
import ru.alfabouh.engine.physics.entities.items.EntitySodaItem;
import ru.alfabouh.engine.physics.entities.prop.PhysCube;
import ru.alfabouh.engine.physics.entities.prop.PhysLightCube;
import ru.alfabouh.engine.physics.entities.prop.PhysPlank;
import ru.alfabouh.engine.physics.entities.prop.WorldDoor;
import ru.alfabouh.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.engine.physics.liquids.Water;
import ru.alfabouh.engine.physics.triggers.Zone;
import ru.alfabouh.engine.physics.world.World;
import ru.alfabouh.engine.render.environment.light.PointLight;
import ru.alfabouh.engine.render.scene.fabric.render.data.RenderObjectData;

import java.util.ArrayList;
import java.util.List;

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
        Game.getGame().getProxy().addItemInWorlds(worldModeledBrush, new RenderObjectData(ResourceManager.renderDataAssets.ground, ResourceManager.modelAssets.ground2));
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
