package javagems3d.physics.entities.kinematic.player;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.ConvexShape;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.triggers.ITriggerAction;
import javagems3d.system.inventory.IInventoryOwner;
import javagems3d.system.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class JGemsKinematicPlayer extends JGemsKinematicControlledItem implements IPlayer, IInventoryOwner {
    private Inventory inventory;

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
        this.startPos.y += this.getPlayerHeight();
        super.createObject();
    }

    @Override
    protected void setDefaults() {
        super.setDefaults();
        this.createInventory();
    }

    protected void createInventory() {
        this.inventory = new Inventory(this, 4);
    }

    protected Vector2f shapeSize() {
        return new Vector2f(0.4f, 1.4f);
    }

    @Override
    protected ConvexShape createGhostShape() {
        return new CapsuleCollisionShape(this.shapeSize().x, this.shapeSize().y, 1);
    }

    @Override
    protected Vector3f getMoveVector() {
        return this.getControllerMoveMotion();
    }

    @Override
    public float getScalarSpeed() {
        return this.getBodyVelocity().length();
    }

    @Override
    public float getPlayerHeight() {
        return this.shapeSize().y;
    }

    @Override
    public float getEyeHeight() {
        return this.getPlayerHeight() / 2.0f - 0.16f;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public ITriggerAction onColliding() {
        return null;
    }
}
