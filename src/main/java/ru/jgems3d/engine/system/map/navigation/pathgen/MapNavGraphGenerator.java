package ru.jgems3d.engine.system.map.navigation.pathgen;

import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.math.Vector3f;
import ru.jgems3d.engine.physics.entities.properties.collision.CollisionFilter;
import ru.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import ru.jgems3d.engine.physics.world.thread.dynamics.DynamicsUtils;
import ru.jgems3d.engine.system.graph.Graph;
import ru.jgems3d.engine.system.graph.GraphVertex;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class MapNavGraphGenerator {
    private final Graph graph;
    private final DynamicsSystem dynamicsSystem;

    public static Graph createGraphWithStartPoint(DynamicsSystem dynamicsSystem, Vector3f pos) {
        MapNavGraphGenerator mapNavGraphGenerator = new MapNavGraphGenerator(dynamicsSystem);
        mapNavGraphGenerator.generate(mapNavGraphGenerator.startPos(pos));
        return mapNavGraphGenerator.getGraph();
    }

    private MapNavGraphGenerator(DynamicsSystem dynamicsSystem) {
        this.graph = new Graph();
        this.dynamicsSystem = dynamicsSystem;
    }

    private void generate(GraphVertex start) {
        Stack<GraphVertex> stack = new Stack<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            GraphVertex current = stack.pop();
            this.getGraph().addVertex(current);
            float off = 1.0f;

            GraphVertex[] vertices = new GraphVertex[4];
            vertices[0] = this.tryPlaceVertex(current, current.getPosition().x - off, current.getPosition().y, current.getPosition().z);
            vertices[1] = this.tryPlaceVertex(current, current.getPosition().x + off, current.getPosition().y, current.getPosition().z);
            vertices[2] = this.tryPlaceVertex(current, current.getPosition().x, current.getPosition().y, current.getPosition().z - off);
            vertices[3] = this.tryPlaceVertex(current, current.getPosition().x, current.getPosition().y, current.getPosition().z + off);

            for (GraphVertex vertex : vertices) {
                if (vertex != null) {
                    GraphVertex v = this.checkNeighbors(vertex);
                    if (v != null) {
                        this.getGraph().addEdge(v, current);
                        this.getGraph().addEdge(current, v);
                    } else {
                        stack.push(vertex);
                    }
                }
            }
        }
    }

    private GraphVertex startPos(Vector3f pos) {
        com.jme3.math.Vector3f vectorCheck1 = new com.jme3.math.Vector3f(pos);
        com.jme3.math.Vector3f vectorCheck2 = new com.jme3.math.Vector3f(pos.x, pos.y - 100.0f, pos.z);

        List<PhysicsRayTestResult> rayTestResults = this.dynamicsSystem.getPhysicsSpace().rayTest(vectorCheck1, vectorCheck2);
        Optional<PhysicsRayTestResult> optional = rayTestResults.stream().filter(e -> (e.getCollisionObject().getCollisionGroup() & CollisionFilter.ST_BODY.getMask()) != 0).findFirst();
        PhysicsRayTestResult rayTestResult = optional.orElse(null);
        if (rayTestResult == null) {
            return null;
        }

        com.jme3.math.Vector3f hitPoint = DynamicsUtils.lerp(vectorCheck1, vectorCheck2, rayTestResult.getHitFraction());
        return new GraphVertex(new org.joml.Vector3f(hitPoint.x, hitPoint.y, hitPoint.z));
    }

    private GraphVertex tryPlaceVertex(GraphVertex current, float x, float y, float z) {
        com.jme3.math.Vector3f vectorCheck1 = new com.jme3.math.Vector3f(x, y + 1.5f, z);
        com.jme3.math.Vector3f vectorCheck2 = new com.jme3.math.Vector3f(x, y - 3.0f, z);

        com.jme3.math.Vector3f vectorCheck3 = DynamicsUtils.convertV3F_JME(current.getPosition()).add(0.0f, 0.1f, 0.0f);
        com.jme3.math.Vector3f vectorCheck4 = new com.jme3.math.Vector3f(x, y + 1.5f, z);

        List<PhysicsRayTestResult> rayPathToPoint = this.dynamicsSystem.getPhysicsSpace().rayTest(vectorCheck3, vectorCheck4);
        Optional<PhysicsRayTestResult> optional = rayPathToPoint.stream().filter(e -> (e.getCollisionObject().getCollisionGroup() & CollisionFilter.ST_BODY.getMask()) != 0).findFirst();
        PhysicsRayTestResult rayTestResult1 = optional.orElse(null);

        if (rayTestResult1 == null) {
            List<PhysicsRayTestResult> rayToSurface = this.dynamicsSystem.getPhysicsSpace().rayTest(vectorCheck1, vectorCheck2);
            Optional<PhysicsRayTestResult> optional1 = rayToSurface.stream().filter(e -> (e.getCollisionObject().getCollisionGroup() & CollisionFilter.ST_BODY.getMask()) != 0).findFirst();
            PhysicsRayTestResult rayTestResult2 = optional1.orElse(null);
            if (rayTestResult2 != null) {
                com.jme3.math.Vector3f hitPointSurface = DynamicsUtils.lerp(vectorCheck1, vectorCheck2, rayTestResult2.getHitFraction());
                return new GraphVertex(new org.joml.Vector3f(hitPointSurface.x, hitPointSurface.y, hitPointSurface.z));
            }
        }

            com.jme3.math.Vector3f hitPoint1 = rayTestResult1 == null ? (vectorCheck4) : DynamicsUtils.lerp(vectorCheck3, vectorCheck4, rayTestResult1.getHitFraction());
            com.jme3.math.Vector3f hitPointPath = new com.jme3.math.Vector3f(hitPoint1).subtract(vectorCheck3);

            com.jme3.math.Vector3f hitPointHalfWay = new com.jme3.math.Vector3f(vectorCheck3).add(hitPointPath.mult(0.5f));

            com.jme3.math.Vector3f vectorCheck3_1 = new Vector3f(hitPointHalfWay.x, hitPointHalfWay.y + 1.0f, hitPointHalfWay.z);
            com.jme3.math.Vector3f vectorCheck3_2 = new Vector3f(hitPointHalfWay.x, hitPointHalfWay.y - 2.0f, hitPointHalfWay.z);

            List<PhysicsRayTestResult> rayToSurface = this.dynamicsSystem.getPhysicsSpace().rayTest(vectorCheck3_1, vectorCheck3_2);
            Optional<PhysicsRayTestResult> optional1 = rayToSurface.stream().filter(e -> (e.getCollisionObject().getCollisionGroup() & CollisionFilter.ST_BODY.getMask()) != 0).findFirst();
            PhysicsRayTestResult rayTestResult2 = optional1.orElse(null);
            if (rayTestResult2 == null) {
                return null;
            }

            com.jme3.math.Vector3f hitPointSurface = DynamicsUtils.lerp(vectorCheck3_1, vectorCheck3_2, rayTestResult2.getHitFraction());
            if (hitPointSurface.y <= hitPoint1.y + 0.5d && hitPointSurface.distance(vectorCheck3) > 0.25d) {
                GraphVertex v = new GraphVertex(new org.joml.Vector3f(hitPointSurface.x, hitPointSurface.y, hitPointSurface.z));
                this.getGraph().addVertex(v);
                this.getGraph().addEdge(v, current);
                this.getGraph().addEdge(current, v);
            }
        return null;
    }

    private GraphVertex checkNeighbors(GraphVertex vertex) {
        for (GraphVertex v : this.getGraph().getGraphContainer().keySet()) {
            if (v != vertex && v.distanceTo(vertex) <= 0.01d) {
                return v;
            }
        }
        return null;
    }

    private Graph getGraph() {
        return this.graph;
    }
}
