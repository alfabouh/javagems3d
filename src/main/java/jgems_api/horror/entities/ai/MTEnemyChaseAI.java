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

package jgems_api.horror.entities.ai;

import jgems_api.horror.HorrorGamePlayerState;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.physics.world.ai.navigation.MTNavigationAI;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.system.graph.GraphVertex;

public class MTEnemyChaseAI<T extends WorldItem> extends MTNavigationAI<T> {
    private WorldItem follow;
    private int superAngryCd;
    private int runCd;
    private int flag;

    public MTEnemyChaseAI(T owner, int priority) {
        super(owner, priority);
        this.follow = null;
    }

    @Override
    public void onUpdateAI(WorldItem worldItem) {
        if (this.follow == null) {
            return;
        }
        super.onUpdateAI(worldItem);
        float distance = this.getAIOwner().getPosition().distance(this.follow.getPosition());
        float chanceToFollowPlayer = this.chanceToFollowPlayer(distance);

        if (this.superAngryCd > 0 || chanceToFollowPlayer == 1.0f) {
            int cd = (int) (300 + (300 * this.getRageValue()));
            if (!this.hasPath() || worldItem.getWorld().getTicks() % 50 == 0) {
                this.setDestination(this.getFollow());
            }
            if (--this.superAngryCd == 0) {
                if (chanceToFollowPlayer == 1.0f) {
                    if (this.flag++ >= 2 + (3 * this.getRageValue())) {
                        this.teleport(HorrorGamePlayerState.findRandomVertexFormDist(worldItem.getWorld().getMapNavGraph(), this.getFollow().getPosition(), 32.0f));
                        this.flag = 0;
                    } else {
                        this.runCd = cd / 2;
                    }
                } else {
                    this.teleport(HorrorGamePlayerState.findRandomVertexFormDist(worldItem.getWorld().getMapNavGraph(), this.getFollow().getPosition(), 16.0f));
                }
            }
            if (this.superAngryCd <= 0) {
                this.superAngryCd = cd;
            }
        } else if (!this.hasPath()) {
            if (JGems3D.random.nextFloat() <= this.chanceToFollowPlayer(distance)) {
                this.setDestination(this.getFollow());
            } else {
                if (JGems3D.random.nextFloat() <= 0.1f + (0.3f * this.getRageValue())) {
                    this.teleport(HorrorGamePlayerState.findRandomVertexFormDist(worldItem.getWorld().getMapNavGraph(), this.getFollow().getPosition(), 16.0f));
                } else {
                    this.setDestination(worldItem.getWorld().getMapNavGraph().getClosestVertex(this.getAIOwner().getPosition().add(this.ranFloat(), this.ranFloat(), this.ranFloat())));
                }
            }
        }
    }

    public void teleport(GraphVertex vertex) {
        this.setCurrentVertex(vertex);
        this.clearPath();
        this.setOwnerPos(this.getVertexPosWithOffset(this.getCurrentVertex()));
    }

    private float ranFloat() {
        return (JGems3D.random.nextFloat() - JGems3D.random.nextFloat()) * 16.0f;
    }

    public void setFollow(WorldItem follow) {
        this.follow = follow;
    }

    public WorldItem getFollow() {
        return this.follow;
    }

    @Override
    public float getSpeed() {
        return (this.runCd-- > 0 ? 0.2f : 0.1f) + (0.1f * this.getRageValue());
    }

    private float closerAngryDistance() {
        return 8.0f + (10.0f * this.getRageValue());
    }

    private float chanceToFollowPlayer(float distance) {
        if (distance > 32.0f) {
            return 0.0f;
        }
        if (distance <= this.closerAngryDistance()) {
            return 1.0f;
        }
        return 0.05f + (0.45f * this.getRageValue());
    }

    private float getRageValue() {
        return (float) HorrorGamePlayerState.brainsCollected / HorrorGamePlayerState.MAX_BRAINS;
    }
}