package ru.BouH.engine.render.scene.scene_render.groups;

import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.joml.Vector3d;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physics.jb_objects.JBulletEntity;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.triggers.ITriggerZone;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.objects.IModeledSceneObject;
import ru.BouH.engine.render.scene.objects.items.PhysicsObjectModeled;
import ru.BouH.engine.render.scene.scene_render.RenderGroup;

import java.util.ArrayList;
import java.util.List;

public class DebugRender extends SceneRenderBase {
    private final ShaderManager debugShaders;

    public DebugRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(2, sceneRenderConveyor, new RenderGroup("DEBUG", true));
        this.debugShaders = ResourceManager.shaderAssets.debug;
    }

    public void onRender(double partialTicks) {
        if (this.getSceneRenderConveyor().getCurrentDebugMode() == 1) {
            this.debugShaders.bind();
            this.renderDebugSunDirection(this);
            this.renderTriggers(partialTicks, this);
            GL30.glEnable(GL30.GL_DEPTH_TEST);
            this.debugShaders.getUtils().performProjectionMatrix();
            for (IModeledSceneObject entityItem : this.getSceneWorld().getFilteredEntityList()) {
                if (entityItem instanceof PhysicsObjectModeled) {
                    this.renderHitBox(partialTicks, this, (PhysicsObjectModeled) entityItem);
                }
            }
            GL30.glDisable(GL30.GL_DEPTH_TEST);
            this.debugShaders.unBind();
        }
    }

    public void onStartRender() {
    }

    public void onStopRender() {
    }

    private void renderDebugSunDirection(SceneRenderBase sceneRenderBase) {
        Model<Format3D> model = MeshHelper.generateVector3DModel(new Vector3d(0.0d), new Vector3d(sceneRenderBase.getSceneWorld().getEnvironment().getSky().getSunAngle()).mul(1000.0f));
        this.debugShaders.getUtils().performModelViewMatrix3d(model);
        this.debugShaders.performUniform("colour", new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        Scene.renderModel(model, GL30.GL_LINES);
        model.clean();
    }

    private void renderTriggers(double partialTicks, SceneRenderBase sceneRenderBase) {
        List<ITriggerZone> triggerZones = new ArrayList<>(this.getSceneWorld().getWorld().getTriggerZones());
        for (ITriggerZone triggerZone : triggerZones) {
            Model<Format3D> form = MeshHelper.generateWirebox3DModel(new Vector3d(triggerZone.getZone().getSize()).mul(-0.5f), new Vector3d(triggerZone.getZone().getSize()).mul(0.5f));
            form.getFormat().setPosition(triggerZone.getZone().getLocation());
            this.debugShaders.getUtils().performModelViewMatrix3d(form);
            this.debugShaders.performUniform("colour", new Vector4f(1.0f, 1.0f, 0.0f, 1.0f));
            Scene.renderModel(form, GL30.GL_LINES);
            form.clean();
        }
    }

    private void renderHitBox(double partialTicks, SceneRenderBase sceneRenderBase, PhysicsObjectModeled physicsObject) {
        WorldItem worldItem = physicsObject.getWorldItem();
        if (worldItem instanceof JBulletEntity) {
            JBulletEntity jBulletEntity = (JBulletEntity) worldItem;
            RigidBodyObject rigidBodyObject = jBulletEntity.getRigidBodyObject();
            if (jBulletEntity.isValid()) {
                Model<Format3D> form = this.constructForm(rigidBodyObject);
                form.getFormat().setPosition(physicsObject.getRenderPosition());
                this.debugShaders.getUtils().performModelViewMatrix3d(form);
                this.debugShaders.performUniform("colour", new Vector4f(1.0f, 0.0f, 1.0f, 1.0f));
                Scene.renderModel(form, GL30.GL_LINES);
                form.clean();
            }
        }
    }

    private Model<Format3D> constructForm(btCollisionObject btCollisionObject) {
        btVector3 min = new btVector3();
        btVector3 max = new btVector3();
        btTransform transform = new btTransform();
        transform.setIdentity();
        btCollisionObject.getCollisionShape().getAabb(transform, min, max);
        transform.deallocate();
        Model<Format3D> form = MeshHelper.generateWirebox3DModel(MathHelper.convert(min), MathHelper.convert(max));
        min.deallocate();
        max.deallocate();
        return form;
    }
}