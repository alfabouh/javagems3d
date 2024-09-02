/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.core.player;

import org.joml.Vector3f;
import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import javagems3d.physics.entities.player.Player;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.service.collections.Pair;

public final class LocalPlayer {
    private final IPlayerConstructor playerConstructor;
    private Player player;

    public LocalPlayer(IPlayerConstructor playerConstructor) {
        this.playerConstructor = playerConstructor;
    }

    public void addPlayerInWorlds(PhysicsWorld world, Vector3f startPos, Vector3f startRot) {
        Pair<Player, RenderEntityData> dynamicPlayer = this.playerConstructor.constructPlayer(world, new Vector3f(startPos), new Vector3f(startRot));
        this.player = dynamicPlayer.getFirst();
        JGemsHelper.WORLD.addItemInWorld(dynamicPlayer.getFirst(), dynamicPlayer.getSecond());
    }

    public Player getEntityPlayer() {
        return this.player;
    }
}