package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.objects.base.IControllable;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.object.IWorldDynamic;
import ru.alfabouh.jgems3d.engine.physics.world.object.IWorldObject;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.environment.light.Light;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.base.IRenderFabric;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.ModelRenderParams;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.IModeledSceneObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.world.SceneWorld;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.alfabouh.jgems3d.logger.SystemLogging;

public abstract class AbstractSceneItemObject implements IModeledSceneObject, IWorldObject, IWorldDynamic {
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

    public AbstractSceneItemObject(@NotNull SceneWorld sceneWorld, @NotNull WorldItem worldItem, @NotNull RenderObjectData renderData) {
        this.worldItem = worldItem;
        this.renderPosition = new Vector3f(worldItem.getPosition());
        this.renderRotation = new Vector3f(worldItem.getRotation());
        this.prevRenderPosition = new Vector3f(this.renderPosition);
        this.prevRenderRotation = new Vector3f(this.renderRotation);
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

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.getWorldItem().isDead()) {
            this.setDead();
        }
    }

    public RenderSphere calcRenderSphere() {
        if (!this.hasRender() || !this.hasModel()) {
            return null;
        }
        return new RenderSphere(this.getModel().getMeshDataGroup().calcDistanceToMostFarPoint(this.getScale()), this.getRenderPosition());
    }

    @Override
    public boolean canBeCulled() {
        return true;
    }

    public void updateRenderTranslation() {
        if (this.hasModel()) {
            Model<Format3D> model = this.getModel();
            model.getFormat().setScaling(new Vector3f(this.getScale()));
            model.getFormat().setPosition(this.getRenderPosition());
            model.getFormat().setRotation(this.getRenderRotation());
        }
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

    public void setPrevPos(Vector3f vector3f) {
        this.prevRenderPosition.set(new Vector3f(vector3f));
    }

    public void setPrevRot(Vector3f vector3f) {
        this.prevRenderRotation.set(new Vector3f(vector3f));
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isEntityUnderUserControl() {
        return this.getWorldItem() instanceof IControllable && ((IControllable) this.getWorldItem()).isValidController();
    }

    public JGemsShaderManager getShaderManager() {
        return this.getModelRenderParams().getShaderManager();
    }

    public Vector3f getScale() {
        return this.getWorldItem().getScale();
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

    public Vector3f getPrevRenderPosition() {
        return new Vector3f(this.prevRenderPosition);
    }

    public Vector3f getRenderPosition() {
        return new Vector3f(this.renderPosition);
    }

    public Vector3f getRenderRotation() {
        return new Vector3f(this.renderRotation);
    }

    public Model<Format3D> getModel() {
        return this.model;
    }

    @Override
    public ModelRenderParams getModelRenderParams() {
        return this.getRenderData().getModelRenderParams();
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    @Override
    public IRenderFabric renderFabric() {
        return this.getRenderData().getRenderFabric();
    }

    public boolean hasRender() {
        return this.renderFabric() != null;
    }

    public boolean hasModel() {
        return this.getModel() != null && this.getModel().isValid();
    }

    public RenderObjectData getRenderData() {
        return this.renderData;
    }

    public WorldItem getWorldItem() {
        return this.worldItem;
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