package javagems3d.graphics.environment.decals.scene;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.decals.fx.DecalFX;
import javagems3d.graphics.environment.lights.scene.LightScene;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.system.global.JGemsConfig;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class DecalsScene implements IDecalsScene {
    private final List<DecalFX> decalFXList;
    private final IEnvironment environment;

    public DecalsScene(IEnvironment environment) {
        this.decalFXList = new ArrayList<>();
        this.environment = environment;
    }

    @Override
    public void update(IRenderWorld renderWorld) {
        {
            Iterator<DecalFX> iterator = this.getDecalFXCollection().iterator();
            while (iterator.hasNext()) {
                DecalFX decalFX = iterator.next();
                if (decalFX.isDead()) {
                    decalFX.onDestroy(renderWorld);
                    iterator.remove();
                } else {
                    decalFX.onUpdate(renderWorld);
                }
            }
        }
    }

    @Override
    public DecalFX spawnDecalFX(@NotNull DecalFX decalFX) {
        this.decalFXList.add(decalFX);
        if (this.decalFXList.size() > JGemsConfig.SYSTEM.MAX_DECALS) {
            Optional<DecalFX> optionalD = this.decalFXList.stream().filter(e -> !e.unDestructible()).findFirst();
            optionalD.ifPresent(IWorldObject::setDead);
        }
        return decalFX;
    }

    @Override
    public void clear() {
        this.decalFXList.forEach(e -> e.onDestroy(this.getEnvironment().getWorld()));
        this.decalFXList.clear();
    }

    public IEnvironment getEnvironment() {
        return this.environment;
    }

    @Override
    public List<DecalFX> getDecalFXCollection() {
        return this.decalFXList;
    }
}
