package javagems3d.system.core.player;

import javagems3d.help.JGemsWorldHelper;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.joml.Vector3f;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.service.collections.Pair;

public final class LocalPlayer {
    private final IPlayerConstructor playerConstructor;
    private IPlayer player;

    public LocalPlayer(IPlayerConstructor playerConstructor) {
        this.playerConstructor = playerConstructor;
    }

    public void addPlayerInWorlds(PhysicsWorld world, Vector3f startPos, Vector3f startRot) {
        Pair<IPlayer, EntityRenderData> dynamicPlayer = this.playerConstructor.constructPlayer(world, new Vector3f(startPos), new Vector3f(startRot));
        this.player = dynamicPlayer.getFirst();
        JGemsWorldHelper.addItemInWorld((WorldItem) dynamicPlayer.getFirst(), dynamicPlayer.getSecond() == null ? JGemsResourceManager.globalRenderDataAssets.defaultPlayer : dynamicPlayer.getSecond());
    }

    public IPlayer getEntityPlayer() {
        return this.player;
    }
}