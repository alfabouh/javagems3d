package ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.items;

import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.objects.base.IControllable;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.JBulletEntity;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.object.IWorldDynamic;
import ru.alfabouh.jgems3d.engine.physics.world.object.IWorldObject;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.render.opengl.environment.light.Light;
import ru.alfabouh.jgems3d.engine.render.opengl.frustum.RenderABB;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.base.IRenderFabric;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data.ModelRenderParams;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.IModeledSceneObject;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.SceneWorld;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.alfabouh.jgems3d.logger.SystemLogging;

public abstract class PhysicsObject implements IModeledSceneObject, IWorldObject, IWorldDynamic {
    private final RenderABB renderABB;
    private final SceneWorld sceneWorld;
    private final WorldItem worldItem;
    private final RenderObjectData renderData;
    protected Model<Format3D> model;
    protected Vector3f prevRenderPosition;
    protected Vector3f prevRenderRotation;
    protected Vector3f renderPosition;
    protected Vector3f renderRotation;
    private InterpolationPoints currentPositionInterpolation;
    private InterpolationPoints currentRotationInterpolation;
    private boolean isVisible;
    private boolean isDead;

    public PhysicsObject(@NotNull SceneWorld sceneWorld, @NotNull WorldItem worldItem, @NotNull RenderObjectData renderData) {
        this.renderABB = new RenderABB();
        this.worldItem = worldItem;
        this.renderPosition = new Vector3f(worldItem.getPosition());
        this.renderRotation = new Vector3f(worldItem.getRotation());
        this.prevRenderPosition = new Vector3f(worldItem.getPosition());
        this.prevRenderRotation = new Vector3f(worldItem.getRotation());
        this.sceneWorld = sceneWorld;
        this.renderData = renderData;
        this.isVisible = true;
        this.currentPositionInterpolation = new InterpolationPoints(this.getPrevRenderPosition(), this.getFixedPosition());
        this.currentRotationInterpolation = new InterpolationPoints(this.getPrevRenderRotation(), this.getFixedRotation());
    }

    public void setModel(Model<Format3D> model) {
        this.model = model;
    }

    protected void initModel() {
        this.setModel(new Model<>(new Format3D(), this.getRenderData().getMeshDataGroup()));
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        if (!this.getWorldItem().isParticle()) {
            SystemLogging.get().getLogManager().log("[ " + this.getWorldItem().toString() + " ]" + " - PreRender");
        }
        if (this.hasRender()) {
            if (this.getRenderData().getEntityModelConstructor() != null) {
                this.setModel(new Model<>(new Format3D(), this.getRenderData().getEntityModelConstructor().constructMeshDataGroup(this.getWorldItem())));
            } else {
                this.initModel();
            }
            this.renderFabric().onStartRender(this);
        }
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        if (!this.getWorldItem().isParticle()) {
            SystemLogging.get().getLogManager().log("[ " + this.getWorldItem().toString() + " ]" + " - PostRender");
        }
        if (this.hasRender()) {
            this.renderFabric().onStopRender(this);
        }
    }

    public void onAddLight(Light light) {
        SystemLogging.get().getLogManager().log("Add light to: " + this.getWorldItem().getItemName());
    }

    public void onRemoveLight(Light light) {
        SystemLogging.get().getLogManager().log("Removed light from: " + this.getWorldItem().getItemName());
    }

    protected Vector3f calcABBSize(WorldItem worldItem) {
        if (worldItem == null) {
            return null;
        }
        if (this.getWorldItem() instanceof JBulletEntity) {
            JBulletEntity jBulletEntity = (JBulletEntity) this.getWorldItem();
            if (jBulletEntity.isValid()) {
                btVector3 v1 = new btVector3();
                btVector3 v2 = new btVector3();
                jBulletEntity.getBulletObject().getCollisionShape().getAabb(jBulletEntity.getBulletObject().getWorldTransform(), v1, v2);
                Vector3f vector3f = new Vector3f((float) (v2.getX() - v1.getX()), (float) (v2.getY() - v1.getY()), (float) (v2.getZ() - v1.getZ()));
                v1.deallocate();
                v2.deallocate();
                return vector3f;
            }
        }
        return this.getModelRenderParams().getCustomCullingAABSize() != null ? this.getModelRenderParams().getCustomCullingAABSize() : new Vector3f(worldItem.getScale().add(1.0f, 1.0f, 1.0f));
    }

    @Override
    public boolean canBeCulled() {
        return true;
    }

    public RenderABB getRenderABB() {
        //return this.renderABB;
        return null;
    }

    public JGemsShaderManager getShaderManager() {
        return this.getModelRenderParams().getShaderManager();
    }

    public Vector3f getScale() {
        return this.getWorldItem().getScale();
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.getRenderABB() != null) {
            Vector3f size = this.calcABBSize(this.getWorldItem());
            if (size != null) {
                this.getRenderABB().setAbbForm(this.getRenderPosition(), size);
            }
        }
        if (this.getWorldItem().isDead()) {
            this.setDead();
        }
    }

    public void updateRenderTranslation() {
        if (this.isHasModel()) {
            Model<Format3D> model = this.getModel3D();
            model.getFormat().setScaling(new Vector3f(this.getScale()));
            model.getFormat().setPosition(this.getRenderPosition());
            model.getFormat().setRotation(this.getRenderRotation());
        }
    }

    public boolean isEntityUnderUserControl() {
        return this.getWorldItem() instanceof IControllable && ((IControllable) this.getWorldItem()).isValidController();
    }

    public void updateRenderPos(float partialTicks) {
        Vector3f pos = this.getFixedPosition();
        Vector3f rot = this.getFixedRotation();
        if (this.getModelRenderParams().isShouldInterpolateMovement()) {
            this.renderPosition.set(this.getCurrentPosState().interpolatedPoint(partialTicks));
            if (this.isEntityUnderUserControl()) {
                this.renderRotation.set(rot);
            } else {
                Vector3f newRotation = new Vector3f();
                Quaternionf result = this.getQuaternionInterpolated(partialTicks);
                result.getEulerAnglesXYZ(newRotation);
                this.renderRotation.set(new Vector3f(newRotation.x, newRotation.y, newRotation.z));
            }
        } else {
            this.renderPosition.set(pos);
            this.renderRotation.set(rot);
        }
    }

    private Quaternionf getQuaternionInterpolated(float partialTicks) {
        Quaternionf start = new Quaternionf();
        Quaternionf end = new Quaternionf();

        start.rotateXYZ(this.getCurrentRotState().getStartPoint().x, this.getCurrentRotState().getStartPoint().y, this.getCurrentRotState().getStartPoint().z);
        end.rotateXYZ(this.getCurrentRotState().getEndPoint().x, this.getCurrentRotState().getEndPoint().y, this.getCurrentRotState().getEndPoint().z);

        Quaternionf res = new Quaternionf();
        end.slerp(start, partialTicks, res);
        return res;
    }

    public void refreshInterpolatingState() {
        this.currentPositionInterpolation = new InterpolationPoints(this.getWorldItem().getPosition(), this.currentPositionInterpolation.getStartPoint());
        this.currentRotationInterpolation = new InterpolationPoints(this.getWorldItem().getRotation(), this.currentRotationInterpolation.getStartPoint());
    }

    private InterpolationPoints getCurrentPosState() {
        return this.currentPositionInterpolation;
    }

    private InterpolationPoints getCurrentRotState() {
        return this.currentRotationInterpolation;
    }

    protected Vector3f getFixedPosition() {
        return this.getWorldItem().getPosition();
    }

    protected Vector3f getFixedRotation() {
        return this.getWorldItem().getRotation();
    }

    public Vector3f getPrevRenderRotation() {
        return new Vector3f(this.prevRenderRotation);
    }

    public void setPrevPos(Vector3f vector3f) {
        this.prevRenderPosition.set(new Vector3f(vector3f));
    }

    public void setPrevRot(Vector3f vector3f) {
        this.prevRenderRotation.set(new Vector3f(vector3f));
    }

    public Vector3f getPrevRenderPosition() {
        return new Vector3f(this.prevRenderPosition);
    }

    public Vector3f getRenderPosition() {
        return new Vector3f(this.renderPosition);
    }

    public Vector3f getRenderRotation() {
        return new Vector3f(this.renderRotation);
    }

    public Model<Format3D> getModel3D() {
        return this.model;
    }

    @Override
    public ModelRenderParams getModelRenderParams() {
        return this.getRenderData().getModelRenderParams();
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    @Override
    public IRenderFabric renderFabric() {
        return this.getRenderData().getRenderFabric();
    }

    public boolean hasRender() {
        return this.renderFabric() != null;
    }

    public RenderObjectData getRenderData() {
        return this.renderData;
    }

    public WorldItem getWorldItem() {
        return this.worldItem;
    }

    public boolean isHasModel() {
        return this.hasRender() && this.getModel3D() != null;
    }

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    public void setDead() {
        this.isDead = true;
    }

    public boolean isDead() {
        return this.isDead;
    }

    public static final class InterpolationPoints {
        private final Vector3f startPoint;
        private final Vector3f endPoint;

        public InterpolationPoints(Vector3f startPoint, Vector3f endPoint) {
            this.startPoint = startPoint;
            this.endPoint = endPoint;
        }

        public Vector3f interpolatedPoint(float partialTicks) {
            Vector3f newP = new Vector3f(this.getEndPoint());
            return newP.lerp(this.getStartPoint(), partialTicks);
        }

        public Vector3f getStartPoint() {
            return this.startPoint;
        }

        public Vector3f getEndPoint() {
            return this.endPoint;
        }
    }
}
