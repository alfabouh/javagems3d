package A_default_app.map;

import api.application.workbench.resources.data.jgems.JGemsMarkerData;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.external.mapping.IGameMap;
import javagems3d.system.external.mapping.data.templates.RowMapObjectData;
import javagems3d.system.external.mapping.processing.ExternalMapProcessor;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.service.files.JGemsPath;
import A_default_app.entities.TestPlayer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ExternalLoader extends ExternalMapProcessor.Default {
    public ExternalLoader(JGemsPath pathToJG3DFile, @Nullable IGameMap.IPlayerConstructor playerConstructor) {
        super(pathToJG3DFile, playerConstructor);
    }

    protected IPlayer createPlayer(PhysicsWorld world, Vector3f pos, Vector3f rot) {
        return new TestPlayer(world, new Vector3f(pos), new Vector3f(rot));
    }

    @Override
    protected void onProcessMarker(RowMapObjectData template, JGemsMarkerData markerData, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        super.onProcessMarker(template, markerData, physicsWorld, sceneWorld);
    }
}
