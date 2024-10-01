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

package javagems3d.physics.entities.ai;

import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.JGems3D;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.ai.navigation.MTNavigationAI;
import javagems3d.physics.world.ai.navigation.NavigationAI;
import javagems3d.physics.world.basic.AIBasedWorldItem;

public class CubeAI extends AIBasedWorldItem {
    private NavigationAI<CubeAI> ai;

    public CubeAI(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scaling, String itemName) {
        super(world, pos, rot, scaling, itemName);
    }

    public CubeAI(PhysicsWorld world, Vector3f pos, Vector3f rot, String itemName) {
        super(world, pos, rot, itemName);
    }

    public CubeAI(PhysicsWorld world, Vector3f pos, String itemName) {
        super(world, pos, itemName);
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        if (!this.ai.hasPath()) {
            this.ai.setDestination((WorldItem) JGems3D.get().getPlayer());
        }
    }

    @Override
    public void init(AIBasedWorldItem aiBasedWorldItem) {
        this.ai = new MTNavigationAI<>(this, 0);
        this.addNewAI(this.ai);
    }
}
