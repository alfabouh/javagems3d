package jgems_app.map;

import api.application.workbench.resources.data.jgems.JGemsMarkerData;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.mapping.IGameMap;
import javagems3d.mapping.data.templates.MapObjectTemplate;
import javagems3d.mapping.processing.ExternalMapProcessor;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.items.TagString;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.entities.kinematic.player.JGemsKinematicPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.path.JGemsPath;
import jgems_app.entities.TestPlayer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ExternalLoader extends ExternalMapProcessor.Default {
    public ExternalLoader(JGemsPath pathToJG3DFile, boolean inJar) {
        super(pathToJG3DFile, inJar);
    }

    protected IPlayer createPlayer(PhysicsWorld world, Vector3f pos, Vector3f rot) {
        return new TestPlayer(world, new Vector3f(pos), new Vector3f(rot));
    }

    @Override
    protected void onProcessMarker(MapObjectTemplate template, JGemsMarkerData markerData, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        super.onProcessMarker(template, markerData, physicsWorld, sceneWorld);
    }
}
