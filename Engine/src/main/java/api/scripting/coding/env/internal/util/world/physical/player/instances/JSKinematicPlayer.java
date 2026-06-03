package api.scripting.coding.env.internal.util.world.physical.player.instances;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.controlling.JSControllableItem;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldObjectI;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSCollisionType;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSEntityState;
import api.scripting.coding.env.internal.util.world.physical.player.real.JSPlayer;
import javagems3d.physics.entities.kinematic.player.JGemsKinematicPlayer;
import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.entities.properties.controller.IControllable;
import javagems3d.physics.world.basic.IWorldObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSPlayer", description = "Wrapper for JGemsKinematicPlayer providing full access to movement, physics, collisions, inventory, and more.")
public class JSKinematicPlayer implements JSWorldObjectI, JSControllableItem, JSPlayer {
    @JSCodingField(description = "Underlying Java player object")
    private final JGemsKinematicPlayer player;

    @JSHideFromDoc
    public JSKinematicPlayer(JGemsKinematicPlayer player) {
        this.player = player;
    }

    @JSCodingConstructor(description = "Create player with custom position, rotation and name", paramNames = {"world", "position", "rotation", "itemName"})
    public JSKinematicPlayer(JSPhysicsWorld world, @NotNull JSVector3f pos, @NotNull JSVector3f rot, String itemName) {
        this.player = new JGemsKinematicPlayer(world.getJavaPhysicsWorld(), pos.getJavaVector3f(), rot.getJavaVector3f(), itemName);
    }

    @JSCodingConstructor(description = "Create player with custom position and rotation", paramNames = {"world", "position", "rotation"})
    public JSKinematicPlayer(JSPhysicsWorld world, @NotNull JSVector3f pos, @NotNull JSVector3f rot) {
        this.player = new JGemsKinematicPlayer(world.getJavaPhysicsWorld(), pos.getJavaVector3f(), rot.getJavaVector3f(), "player");
    }

    @JSCodingConstructor(description = "Create player with custom position", paramNames = {"world", "position"})
    public JSKinematicPlayer(JSPhysicsWorld world, @NotNull JSVector3f pos) {
        this.player = new JGemsKinematicPlayer(world.getJavaPhysicsWorld(), pos.getJavaVector3f(), new Vector3f(0.0f), "player");
    }

    @JSCodingFunctionOrMethod(description = "Get player position")
    public JSVector3f getPosition() {
        return new JSVector3f(this.player.getPosition());
    }

    @JSCodingFunctionOrMethod(description = "Set player position", paramNames = {"position"})
    public void setPosition(JSVector3f pos) {
        this.player.setPosition(pos.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get player movement vector")
    public JSVector3f getMoveVector() {
        return new JSVector3f(this.player.getMoveVector());
    }

    @JSCodingFunctionOrMethod(description = "Get slope angle of player")
    public float getSlopeAngle() {
        return (float) this.player.getSlopeAngle();
    }

    @JSCodingFunctionOrMethod(description = "Get scalar speed of player")
    public float getScalarSpeed() {
        return this.player.getScalarSpeed();
    }

    // --- Player size ---
    @JSCodingFunctionOrMethod(description = "Get player height")
    public float getPlayerHeight() {
        return this.player.getPlayerHeight();
    }

    @JSCodingFunctionOrMethod(description = "Get eye height")
    public float getEyeHeight() {
        return this.player.getEyeHeight();
    }

    // --- Physics & Motion ---
    @JSCodingFunctionOrMethod(description = "Check if player is on ground")
    public boolean isOnGround() {
        return this.player.isOnGround();
    }

    @JSCodingFunctionOrMethod(description = "Set player walk speed", paramNames = {"speed"})
    public void setWalkSpeed(float speed) {
        this.player.setWalkSpeed(speed);
    }

    @JSCodingFunctionOrMethod(description = "Get player walk speed")
    public float getWalkSpeed() {
        return this.player.getWalkSpeed();
    }

    @JSCodingFunctionOrMethod(description = "Set player jump height", paramNames = {"height"})
    public void setJumpHeight(float height) {
        this.player.setJumpHeight(height);
    }

    @JSCodingFunctionOrMethod(description = "Get player jump height")
    public float getJumpHeight() {
        return this.player.getJumpHeight();
    }

    @JSCodingFunctionOrMethod(description = "Make player jump")
    public void jump() {
        this.player.jump(this.player.getGravity(), this.player.getJumpHeight());
    }

    @JSCodingFunctionOrMethod(description = "Get entity state flags")
    public JSEntityState getState() {
        return new JSEntityState(this.player.getEntityState());
    }

    @JSCodingFunctionOrMethod(description = "Set collision groups", paramNames = {"types"})
    public void setCollisionGroup(JSCollisionType... types) {
        CollisionType[] javaTypes = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) {
            javaTypes[i] = types[i].getJavaType();
        }
        this.player.setCollisionGroup(javaTypes);
    }

    @JSCodingFunctionOrMethod(description = "Get collision group mask")
    public int getCollisionGroup() {
        return this.player.getCollisionGroup();
    }

    @JSCodingFunctionOrMethod(description = "Set collision filter", paramNames = {"types"})
    public void setCollisionFilter(JSCollisionType... types) {
        CollisionType[] javaTypes = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) {
            javaTypes[i] = types[i].getJavaType();
        }
        this.player.setCollideWithGroups(javaTypes);
    }

    @JSCodingFunctionOrMethod(description = "Get collision filter mask")
    public int getCollisionFilter() {
        return this.player.getCollisideWithGroups();
    }

    @JSCodingFunctionOrMethod(description = "Set slope angle for movement", paramNames = {"angle"})
    public void setSlopeAngle(float angle) {
        this.player.setSlopeAngle(angle);
    }

    @JSCodingFunctionOrMethod(description = "Set capsule shape (radius, height)", paramNames = {"radius", "height"})
    public void setCapsuleShape(float radius, float height) {
        this.player.setCapsuleShape(radius, height);
    }

    @JSCodingFunctionOrMethod(description = "Set box shape (xz, height)", paramNames = {"xz", "height"})
    public void setBoxShape(float xz, float height) {
        this.player.setBoxShape(xz, height);
    }

    @JSCodingFunctionOrMethod(description = "Set gravity multiplier", paramNames = {"gravity"})
    public void setGravity(float gravity) {
        this.player.setGravity(gravity);
    }

    @JSCodingFunctionOrMethod(description = "Rewarp player to position")
    public void rewarp() {
        this.player.resetWarp();
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java player object")
    public JGemsKinematicPlayer getJavaPlayer() {
        return this.player;
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    @Override
    public IWorldObject getJavaWorldObject() {
        return this.player;
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    @Override
    public IControllable getJavaControllable() {
        return this.player;
    }
}