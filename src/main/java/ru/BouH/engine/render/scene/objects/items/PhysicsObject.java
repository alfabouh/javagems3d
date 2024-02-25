package ru.BouH.engine.render.scene.objects.items;

import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physics.brush.WorldBrush;
import ru.BouH.engine.physics.entities.IControllable;
import ru.BouH.engine.physics.jb_objects.JBulletEntity;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.physics.world.object.IWorldObject;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.light.Light;
import ru.BouH.engine.render.frustum.RenderABB;
import ru.BouH.engine.render.scene.fabric.render.base.IRenderFabric;
import ru.BouH.engine.render.scene.fabric.render.data.ModelRenderParams;
import ru.BouH.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.BouH.engine.render.scene.objects.IModeledSceneObject;
import ru.BouH.engine.render.scene.world.SceneWorld;

public abstract class PhysicsObject implements IModeledSceneObject, IWorldObject, IWorldDynamic {
    private final RenderABB renderABB;
    private final SceneWorld sceneWorld;
    private final WorldItem worldItem;
    private final RenderObjectData renderData;
    protected Model<Format3D> model;
    protected Vector3d prevRenderPosition;
    protected Vector3d prevRenderRotation;
    protected Vector3d renderPosition;
    protected Vector3d renderRotation;
    private InterpolationPoints currentPositionInterpolation;
    private InterpolationPoints currentRotationInterpolation;
    private boolean isVisible;
    private boolean isDead;

    public PhysicsObject(@NotNull SceneWorld sceneWorld, @NotNull WorldItem worldItem, @NotNull RenderObjectData renderData) {
        this.renderABB = new RenderABB();
        this.worldItem = worldItem;
        this.renderPosition = new Vector3d(worldItem.getPosition());
        this.renderRotation = new Vector3d(worldItem.getRotation());
        this.prevRenderPosition = new Vector3d(worldItem.getPosition());
        this.prevRenderRotation = new Vector3d(worldItem.getRotation());
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
            Game.getGame().getLogManager().debug("[ " + this.getWorldItem().toString() + " ]" + " - PreRender");
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
            Game.getGame().getLogManager().debug("[ " + this.getWorldItem().toString() + " ]" + " - PostRender");
        }
        if (this.hasRender()) {
            this.renderFabric().onStopRender(this);
        }
    }

    public void onAddLight(Light light) {
        Game.getGame().getLogManager().debug("Add light to: " + this.getWorldItem().getItemName());
    }

    public void onRemoveLight(Light light) {
        Game.getGame().getLogManager().debug("Removed light from: " + this.getWorldItem().getItemName());
    }

    protected Vector3d calcABBSize(WorldItem worldItem) {
        if (worldItem == null) {
            return null;
        }
        if (this.getWorldItem() instanceof JBulletEntity) {
            JBulletEntity jBulletEntity = (JBulletEntity) this.getWorldItem();
            if (jBulletEntity.isValid()) {
                btVector3 v1 = new btVector3();
                btVector3 v2 = new btVector3();
                btVector3 v3 = MathHelper.convert(this.getRenderPosition());
                btTransform transform = new btTransform();
                transform.setIdentity();
                transform.setOrigin(v3);
                jBulletEntity.getBulletObject().getCollisionShape().getAabb(transform, v1, v2);
                Vector3d vector3d = new Vector3d(v2.getX() - v1.getX(), v2.getY() - v1.getY(), v2.getZ() - v1.getZ());
                v1.deallocate();
                v2.deallocate();
                v3.deallocate();
                transform.deallocate();
                return vector3d;
            }
        }
        return new Vector3d(worldItem.getScale() + 1.0d);
    }

    @Override
    public boolean canBeCulled() {
        return true;
    }

    public RenderABB getRenderABB() {
        return this.getWorldItem() instanceof WorldBrush ? null : this.renderABB;
    }

    public ShaderManager getShaderManager() {
        return this.getModelRenderParams().getShaderManager();
    }

    public double getScale() {
        return this.getWorldItem().getScale();
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.getRenderABB() != null) {
            Vector3d size = this.calcABBSize(this.getWorldItem());
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
            model.getFormat().setScale(new Vector3d(this.getScale()));
            model.getFormat().setPosition(this.getRenderPosition());
            model.getFormat().setRotation(this.getRenderRotation());
        }
    }

    public boolean isEntityUnderUserControl() {
        return this.getWorldItem() instanceof IControllable && ((IControllable) this.getWorldItem()).isValidController();
    }

    public void updateRenderPos(double partialTicks) {
        Vector3d pos = this.getFixedPosition();
        Vector3d rot = this.getFixedRotation();
        this.renderPosition.set(this.getCurrentPosState().interpolatedPoint(partialTicks));
        if (this.isEntityUnderUserControl()) {
            this.renderRotation.set(rot);
        } else {
            Vector3d newRotation = new Vector3d();
            Quaterniond result = this.getQuaternionInterpolated(partialTicks);
            result.getEulerAnglesXYZ(newRotation);
            this.renderRotation.set(new Vector3d(newRotation.x, newRotation.y, newRotation.z));
        }
    }

    private Quaterniond getQuaternionInterpolated(double partialTicks) {
        Quaterniond start = new Quaterniond();
        Quaterniond end = new Quaterniond();

        start.rotateXYZ(this.getCurrentRotState().getStartPoint().x, this.getCurrentRotState().getStartPoint().y, this.getCurrentRotState().getStartPoint().z);
        end.rotateXYZ(this.getCurrentRotState().getEndPoint().x, this.getCurrentRotState().getEndPoint().y, this.getCurrentRotState().getEndPoint().z);

        Quaterniond res = new Quaterniond();
        end.slerp(start, partialTicks, res);
        return res;
    }

    public void refreshInterpolatingState() {
        this.currentPositionInterpolation = new InterpolationPoints(this.getWorldItem().getPosition(), this.getPrevRenderPosition());
        this.currentRotationInterpolation = new InterpolationPoints(this.getWorldItem().getRotation(), this.getPrevRenderRotation());
    }

    private InterpolationPoints getCurrentPosState() {
        return this.currentPositionInterpolation;
    }

    private InterpolationPoints getCurrentRotState() {
        return this.currentRotationInterpolation;
    }

    protected Vector3d getFixedPosition() {
        return this.getWorldItem().getPosition();
    }

    protected Vector3d getFixedRotation() {
        return this.getWorldItem().getRotation();
    }

    public Vector3d getPrevRenderRotation() {
        return new Vector3d(this.prevRenderRotation);
    }

    public void setPrevPos(Vector3d vector3d) {
        this.prevRenderPosition.set(new Vector3d(vector3d));
    }

    public void setPrevRot(Vector3d vector3d) {
        this.prevRenderRotation.set(new Vector3d(vector3d));
    }

    public Vector3d getPrevRenderPosition() {
        return new Vector3d(this.prevRenderPosition);
    }

    public Vector3d getRenderPosition() {
        return new Vector3d(this.renderPosition);
    }

    public Vector3d getRenderRotation() {
        return new Vector3d(this.renderRotation);
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
        private final Vector3d startPoint;
        private final Vector3d endPoint;

        public InterpolationPoints(Vector3d startPoint, Vector3d endPoint) {
            this.startPoint = startPoint;
            this.endPoint = endPoint;
        }

        public Vector3d interpolatedPoint(double partialTicks) {
            Vector3d newP = new Vector3d(this.getEndPoint());
            return newP.lerp(this.getStartPoint(), partialTicks);
        }

        public Vector3d getStartPoint() {
            return this.startPoint;
        }

        public Vector3d getEndPoint() {
            return this.endPoint;
        }
    }
}
