package javagems3d.graphics.objects.entities;

import api.events.EventBus;
import api.events.EventLauncher;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.entities.bullet.JGemsBody;
import javagems3d.physics.entities.properties.controller.IControllable;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.physics.world.basic.WorldItem;

import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.graphics.objects.rendering.constructors.IModelConstructor;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class SceneEntity extends SceneObject implements IWorldTicked {
    private final IModelConstructor<WorldItem> entityModelConstructor;
    private final WorldItem worldItem;
    private boolean isVisible;
    private boolean isDead;
    protected Vector3f renderPosition;
    protected Vector3f renderRotation;
    protected InterpolationPoints currentPositionInterpolation;
    protected InterpolationPoints currentRotationInterpolation;

    public SceneEntity(@NotNull SceneWorld sceneWorld, @NotNull WorldItem worldItem, @NotNull EntityRenderData renderData) {
        super(sceneWorld, new Model3D(new Pose3D(), renderData.getMeshStructure()), renderData.getObjectRenderAttributes());
        this.entityModelConstructor = renderData.getEntityModelConstructor();
        this.worldItem = worldItem;
        this.renderPosition = new Vector3f(worldItem.getPosition());
        this.renderRotation = new Vector3f(worldItem.getRotation());
        this.currentPositionInterpolation = new InterpolationPoints(this.getFixedPosition(), this.getFixedPosition());
        this.currentRotationInterpolation = new InterpolationPoints(this.getFixedRotation(), this.getFixedRotation());
        this.isVisible = true;
        this.isDead = false;
    }

    @Override
    public AnimationData setAnimationByID(int id) {
        AnimationData animationData = super.setAnimationByID(id);
        if (animationData != null) {
            if (this.getWorldItem() instanceof JGemsBody) {
                final JGemsBody jGemsBody = (JGemsBody) this.getWorldItem();
                final MeshStructure3D<?> meshStructure3D = this.getModel().getMeshStructure();
                final MeshCollisionData meshCollisionData = meshStructure3D.getMeshCollisionData();
                jGemsBody.getPhysicsRigidBody().setCollisionShape(meshCollisionData.getAnimationAABBShapes().get(id));
            }
        }
        return animationData;
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        Log.get().trace("[ " + this + " ]" + " - PreRender");
        if (this.canBeRendered()) {
            if (!this.hasModel() && this.getEntityModelConstructor() != null) {
                this.setModel(new Model3D(new Pose3D(), this.getEntityModelConstructor().constructMeshDataGroup(this.getWorldItem())));
            }
            this.getRenderFabricsSet().forEach(e -> e.createResources(this));
        }
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
        Log.get().trace("[ " + this + " ]" + " - PostRender");
        if (this.canBeRendered()) {
            this.getRenderFabricsSet().forEach(e -> e.destroyResources(this));
        }
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        this.setCullingData(this.pickAABBDataFromMesh());
        if (this.getWorldItem().isDead()) {
            this.setDead();
        }
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public void setDead() {
        this.isDead = true;
    }

    public void refreshInterpolatingState() {
        this.currentPositionInterpolation = new InterpolationPoints(this.getCurrentPosState().getEndPoint(), this.getWorldItem().getPosition());
        this.currentRotationInterpolation = new InterpolationPoints(this.getCurrentRotState().getEndPoint(), this.getWorldItem().getRotation());
    }

    public void updateModelTranslation() {
        if (this.hasModel()) {
            Model3D model = this.getModel();
            model.getPose().setScaling(new Vector3f(this.getScaling()));
            model.getPose().setPosition(this.getRenderPosition());
            model.getPose().setRotation(this.getRenderRotation());
        }
    }

    public void updateRenderPos(float physicsSyncTicks) {
        Vector3f pos = this.getFixedPosition();
        Vector3f rot = this.getFixedRotation();
        if (!this.canBeRendered() || this.getRenderAttributes().getProperties().getBool(JGemsRenderProperties.KEY_ALLOW_MOVEMENT_INTERPOLATION)) {
            this.renderPosition.set(this.getCurrentPosState().interpolatedPoint(physicsSyncTicks));
            if (this.isEntityUnderUserControl()) {
                this.renderRotation.set(rot);
            } else {
                Vector3f newRotation = new Vector3f();
                Quaternionf result = this.getQuaternionInterpolated(this.getCurrentRotState(), physicsSyncTicks);
                result.getEulerAnglesXYZ(newRotation);
                this.renderRotation.set(new Vector3f(newRotation.x, newRotation.y, newRotation.z));
            }
        } else {
            this.renderPosition.set(pos);
            this.renderRotation.set(rot);
        }
    }

    private Quaternionf getQuaternionInterpolated(InterpolationPoints rotation, float physicsSyncTicks) {
        Quaternionf start = new Quaternionf();
        Quaternionf end = new Quaternionf();
        start.rotateXYZ(rotation.getStartPoint().x, rotation.getStartPoint().y, rotation.getStartPoint().z);
        end.rotateXYZ(rotation.getEndPoint().x, rotation.getEndPoint().y, rotation.getEndPoint().z);
        Quaternionf res = new Quaternionf();
        start.slerp(end, physicsSyncTicks, res);
        return res;
    }

    @Override
    public Vector3f getPositionToAttachLights() {
        return this.getRenderPosition();
    }

    protected IModelConstructor<WorldItem> getEntityModelConstructor() {
        return this.entityModelConstructor;
    }

    public Vector3f getScaling() {
        return this.getWorldItem().getScaling();
    }

    protected InterpolationPoints getCurrentPosState() {
        return this.currentPositionInterpolation;
    }

    protected InterpolationPoints getCurrentRotState() {
        return this.currentRotationInterpolation;
    }

    protected Vector3f getFixedPosition() {
        return this.getWorldItem().getPosition();
    }

    protected Vector3f getFixedRotation() {
        return this.getWorldItem().getRotation();
    }

    public Vector3f getRenderPosition() {
        return new Vector3f(this.renderPosition);
    }

    public Vector3f getRenderRotation() {
        return new Vector3f(this.renderRotation);
    }

    public boolean isEntityUnderUserControl() {
        return this.getWorldItem() instanceof IControllable && ((IControllable) this.getWorldItem()).isValidController();
    }

    @Override
    public SceneWorld getWorld() {
        return (SceneWorld) super.getWorld();
    }

    @Override
    public boolean canBeRendered() {
        return super.canBeRendered() && this.isVisible;
    }

    public WorldItem getWorldItem() {
        return this.worldItem;
    }

    @Override
    public boolean isDead() {
        return this.isDead;
    }

    @Override
    public String toString() {
        return this.getWorldItem().toString() + " - SceneObject";
    }

    public static final class InterpolationPoints {
        private final Vector3f endPoint;
        private final Vector3f startPoint;

        public InterpolationPoints(Vector3f startPoint, Vector3f endPoint) {
            this.startPoint = startPoint;
            this.endPoint = endPoint;
        }

        public Vector3f interpolatedPoint(float physicsSyncTicks) {
            Vector3f newP = new Vector3f(this.getStartPoint());
            return newP.lerp(this.getEndPoint(), physicsSyncTicks);
        }

        public Vector3f getEndPoint() {
            return this.endPoint;
        }

        public Vector3f getStartPoint() {
            return this.startPoint;
        }
    }
}
