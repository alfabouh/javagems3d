package ru.BouH.engine.render.scene.scene_render.groups;

import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.math.Pair;
import ru.BouH.engine.physics.jb_objects.JBulletEntity;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.liquids.ILiquid;
import ru.BouH.engine.physics.triggers.ITriggerZone;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.objects.IModeledSceneObject;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.scene_render.RenderGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DebugRender extends SceneRenderBase {
    private final ShaderManager debugShaders;
    public static final Set<Pair<Model<Format3D>, Vector3d>> objectWires = new HashSet<>();

    public DebugRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(50, sceneRenderConveyor, new RenderGroup("DEBUG", true));
        this.debugShaders = ResourceManager.shaderAssets.debug;
    }

    public void onRender(double partialTicks) {
        if (this.getSceneRenderConveyor().getCurrentDebugMode() == 1) {
            this.debugShaders.bind();
            this.renderDebugSunDirection(this);
            this.debugShaders.getUtils().performProjectionMatrix();
            this.renderWires();
            this.debugShaders.unBind();
            DebugRender.objectWires.clear();
            if (!Game.getGame().getPhysicsWorld().getDynamicsWorld().isNull()) {
                Game.getGame().getPhysicsWorld().getDynamicsWorld().debugDrawWorld();
            }
        }
    }

    public void onStartRender() {
    }

    public void onStopRender() {
    }

    private void renderWires() {
        for (Pair<Model<Format3D>, Vector3d> model : DebugRender.objectWires) {
            this.debugShaders.getUtils().performModelViewMatrix3d(model.getKey());
            this.debugShaders.performUniform("colour", new Vector4d(model.getValue(), 1.0d));
            Scene.renderModel(model.getKey(), GL30.GL_LINES);
            model.getKey().clean();
        }
    }

    private void renderDebugSunDirection(SceneRenderBase sceneRenderBase) {
        Model<Format3D> model = MeshHelper.generateVector3DModel(new Vector3d(0.0d), new Vector3d(sceneRenderBase.getSceneWorld().getEnvironment().getSky().getSunAngle()).mul(1000.0f));
        this.debugShaders.getUtils().performModelViewMatrix3d(model);
        this.debugShaders.performUniform("colour", new Vector4f(1.0f, 1.0f, 0.0f, 1.0f));
        Scene.renderModel(model, GL30.GL_LINES);
        model.clean();
    }
}