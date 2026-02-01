package jgems_app.map;

import api.application.workbench.resources.data.jgems.JGemsMarkerData;
import javagems3d.JGems3D;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.mapping.data.templates.RowMapObjectData;
import javagems3d.mapping.processing.ExternalMapProcessor;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.service.path.JGemsPath;
import jgems_app.entities.TestPlayer;
import org.joml.Vector3f;

public class ExternalLoader extends ExternalMapProcessor.Default {
    public ExternalLoader(JGemsPath pathToJG3DFile, JGems3D.GetSource source) {
        super(pathToJG3DFile, source);
    }

    protected IPlayer createPlayer(PhysicsWorld world, Vector3f pos, Vector3f rot) {
        return new TestPlayer(world, new Vector3f(pos), new Vector3f(rot));
    }

    @Override
    protected void onProcessMarker(RowMapObjectData template, JGemsMarkerData markerData, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        super.onProcessMarker(template, markerData, physicsWorld, sceneWorld);
    }
}
