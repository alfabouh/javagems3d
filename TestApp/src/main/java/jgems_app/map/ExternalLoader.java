package jgems_app.map;

import api.application.workbench.resources.data.jgems.JGemsMarkerData;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.mapping.IGameMap;
import javagems3d.mapping.data.templates.MapObjectTemplate;
import javagems3d.mapping.processing.ExternalMapProcessor;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.path.JGemsPath;
import jgems_app.entities.TestPlayer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ExternalLoader extends ExternalMapProcessor.Default {
    private Vector3f playerSpawnPoint;
    private Vector3f playerSpawnRotation;

    public ExternalLoader(JGemsPath pathToJG3DFile, boolean inJar) {
        super(pathToJG3DFile, inJar);
    }

    @Override
    public @Nullable IGameMap.IPlayerConstructor getPlayerConstructor() {
        return (world -> new Pair<>(new TestPlayer(world, this.playerSpawnPoint, this.playerSpawnRotation), JGemsResourceManager.globalRenderDataAssets.defaultPlayer));
    }

    @Override
    protected void onProcessMarker(MapObjectTemplate template, JGemsMarkerData markerData, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        super.onProcessMarker(template, markerData, physicsWorld, sceneWorld);
        if (template.getObjectNameId().equals("spawn")) {
            this.playerSpawnPoint = template.getPosition();
            this.playerSpawnRotation = template.getRotation();
        }
    }
}
