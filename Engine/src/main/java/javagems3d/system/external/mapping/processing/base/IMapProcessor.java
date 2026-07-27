/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.system.external.mapping.processing.base;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.lights.scene.ILightScene;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.external.mapping.IGameMap;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.resources.managing.resources.SystemResources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collection;

public interface IMapProcessor {
    void init();

    void preProcessing(PhysicsWorld world, SceneWorld sceneWorld);
    void onProcessing(PhysicsWorld world, SceneWorld sceneWorld);
    void postProcessing(PhysicsWorld world, SceneWorld sceneWorld);

    void onSetupLighting(ILightScene lightScene, IEnvironment environment);
    void onSetupShadows(IShadowScene shadowScene, IEnvironment environment);
    void onSetupSkyBox(ISkyBox skyBox, ISkyBackground background, IEnvironment environment);
    void onSetupFog(IFogScene fogScene, IEnvironment environment);

    @Nullable IGameMap.IPlayerConstructor getPlayerConstructor(PhysicsWorld physicsWorld, SceneWorld sceneWorld);
    @Nullable Collection<IGameMap.SpawnPlayerData> getSpawnPlayersSet();

    @NotNull String getMapName();
    @NotNull String getMapInformation();

    default @NotNull Vector3f getDefaultStartPosition() {
        return new Vector3f(0.0f);
    }

    default @NotNull Vector3f getDefaultStartRotation() {
        return new Vector3f(0.0f);
    }

    void setGlobalResources(@NotNull SystemResources globalResources);
    void setLocalResources(@NotNull SystemResources localResources);

    @NotNull SystemResources getGlobalResources();
    @NotNull SystemResources getLocalResources();
}