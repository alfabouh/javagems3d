package workbench.graphics.environment.components;

import javagems3d.graphics.environment.skybox.background.SkyBoxBackground;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.physics.world.IWorld;
import workbench.graphics.objects.WBenchObject;

import java.util.*;

public class WBenchSkyBackground extends SkyBoxBackground {
    private final Queue<Integer> freeIds;

    public WBenchSkyBackground(IWorld world, float viewScaling) {
        super(world, viewScaling);
        this.freeIds = new ArrayDeque<>();
    }

    @Override
    protected void deathIterator() {
        Iterator<SceneProp> scenePropIterator = this.getSkySceneObjects().iterator();
        while (scenePropIterator.hasNext()) {
            WBenchObject wBenchObject = (WBenchObject) scenePropIterator.next();
            if (wBenchObject.isDead()) {
                this.getFreeIds().add(wBenchObject.getId());
                wBenchObject.onDestroy(this.getWorld());
                scenePropIterator.remove();
            }
        }
    }

    @Override
    public void addObject(SceneProp object) {
        WBenchObject wBenchObject = (WBenchObject) object;
        int id = wBenchObject.getId();
        if (id < 0) {
            if (!this.getFreeIds().isEmpty()) {
                Integer freeId = this.getFreeIds().poll();
                if (freeId != null) {
                    id = freeId;
                }
            } else {
                id = this.getSkySceneObjects().size();
            }
            wBenchObject.setId(id);
        }

        super.addObject(object);
    }

    @Override
    public void removeObject(SceneProp object) {
        int id = ((WBenchObject) object).getId();
        this.getFreeIds().add(id);
        super.removeObject(object);
    }

    public Queue<Integer> getFreeIds() {
        return this.freeIds;
    }
}