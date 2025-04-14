package javagems3d.graphics.environment.skybox.background;

import javagems3d.graphics.camera.FixedCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.rendering.scene.culling.SceneCulling;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class JGemsSkyBackground extends SkyBoxBackground {
    public JGemsSkyBackground(IWorld world, float viewScaling) {
        super(world, viewScaling);
    }
}
