package javagems3d.physics.entities.kinematic.player;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.ConvexShape;
import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.triggers.ITriggerAction;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class JGemsKinematicPlayer extends JGemsKinematicControlledItem implements IPlayer {
    private final Object transformLock = new Object();

    private float scalarSpeed;

    public JGemsKinematicPlayer(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, String itemName) {
        super(world, pos, rot, itemName);
    }

    public JGemsKinematicPlayer(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot) {
        super(world, pos, rot, "player");
    }

    public JGemsKinematicPlayer(PhysicsWorld world, @NotNull Vector3f pos) {
        super(world, pos, new Vector3f(0.0f), "player");
    }

    @Override
    protected void createObject() {
        super.createObject();
        this.startPosition.y += this.getPlayerHeight();
        this.setCollisionGroup(CollisionType.PLAYER);
    }

    @Override
    protected void setShapeAfterInit() {
        super.setCapsuleShape(0.4f, 1.4f);
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
    }

    @Override
    public Vector3f getMoveVector() {
        synchronized (transformLock) {
            return this.getControllerMoveMotion();
        }
    }

    @Override
    public void onTick(IWorld iWorld) {
        super.onTick(iWorld);
        this.scalarSpeed = this.getPrevPosition().distance(this.getPosition());
    }

    @Override
    public synchronized float getScalarSpeed() {
        return this.scalarSpeed;
    }

    @Override
    public float getPlayerHeight() {
        BoundingBox boundingBox = new BoundingBox();
        this.getGhostBody().boundingBox(boundingBox);
        return boundingBox.getYExtent() * 2.0f;
    }

    @Override
    public float getEyeHeight() {
        return this.getPlayerHeight() / 2.0f - 0.16f;
    }

    @Override
    public ITriggerAction onColliding() {
        return null;
    }
}
