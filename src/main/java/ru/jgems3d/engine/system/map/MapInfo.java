package ru.jgems3d.engine.system.map;

import org.joml.Vector3f;
import ru.jgems3d.engine.system.service.misc.Pair;
import ru.jgems3d.toolbox.map_sys.save.objects.MapProperties;

import java.util.ArrayList;
import java.util.List;

public class MapInfo {
    private final List<Pair<Vector3f, Double>> spawnPoints;
    private final MapProperties mapProperties;

    public MapInfo(MapProperties mapProperties) {
        this.mapProperties = mapProperties;
        this.spawnPoints = new ArrayList<>();
    }

    public void addSpawnPoint(Vector3f position, double rotation) {
        this.spawnPoints.add(new Pair<>(position, rotation));
    }

    public MapProperties getMapProperties() {
        return this.mapProperties;
    }

    public Pair<Vector3f, Double> chooseRandomSpawnPoint() {
        if (this.getPlayerSpawnPoints().isEmpty()) {
            return new Pair<>(new Vector3f(0.0f), 0.0d);
        }
        ArrayList<Pair<Vector3f, Double>> list = this.getPlayerSpawnPoints();
        return list.get(list.size() - 1);
    }

    public ArrayList<Pair<Vector3f, Double>> getPlayerSpawnPoints() {
        return new ArrayList<>(this.spawnPoints);
    }
}
