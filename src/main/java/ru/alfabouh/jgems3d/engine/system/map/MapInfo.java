package ru.alfabouh.jgems3d.engine.system.map;

import org.checkerframework.checker.units.qual.A;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.math.Pair;
import ru.alfabouh.jgems3d.mapsys.file.save.objects.MapProperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
