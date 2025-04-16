package jgems_app.map;

import api.application.workbench.resources.data.jgems.JGemsMarkerData;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.help.JGemsCoreHelper;
import javagems3d.help.JGemsUtils;
import javagems3d.mapping.IGameMap;
import javagems3d.mapping.data.templates.MapObjectTemplate;
import javagems3d.mapping.processing.ExternalMapProcessor;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.items.TagRadioBoolean;
import javagems3d.mapping.tags.items.TagString;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.path.JGemsPath;
import jgems_app.entities.TestPlayer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ExternalLoader extends ExternalMapProcessor.Default {
    private Vector3f playerSpawnPoint;
    private Vector3f playerSpawnRotation;

    public ExternalLoader(JGemsPath pathToJG3DFile, boolean inJar) {
        super(pathToJG3DFile, inJar);
    }

    @Override
    public @Nullable IGameMap.IPlayerConstructor getPlayerConstructor() {
        return (world -> new Pair<>(new TestPlayer(world, new Vector3f(this.playerSpawnPoint), new Vector3f(this.playerSpawnRotation)), JGemsResourceManager.globalRenderDataAssets.defaultPlayer));
    }

    @Override
    protected void onProcessMarker(MapObjectTemplate template, JGemsMarkerData markerData, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        super.onProcessMarker(template, markerData, physicsWorld, sceneWorld);
        final TagString tagId = template.getTagsContainer().getTagItem(TagID.DEFAULT.MARKER_STRING_ID);
        if (tagId != null && tagId.getText().equals("spawnp")) {
            Matrix4f rotMatrix = new Matrix4f().rotateXYZ(template.getRotation().x, template.getRotation().y, template.getRotation().z);
            Vector3f forward = new Vector3f(0, 0, -1);
            rotMatrix.transformDirection(forward);
            Vector3f flatForward = new Vector3f(forward.x, 0, forward.z).normalize();
            float angleY = (float)Math.atan2(-flatForward.x, -flatForward.z);

            this.playerSpawnPoint = template.getPosition();
            this.playerSpawnRotation = new Vector3f(0.0f, angleY, 0.0f);

            System.out.println(template.getRotation().x + " " + template.getRotation().y + " " + template.getRotation().z);
        }
    }
}
