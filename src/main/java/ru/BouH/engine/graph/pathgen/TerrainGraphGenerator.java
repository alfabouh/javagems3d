package ru.BouH.engine.graph.pathgen;

import org.bytedeco.bullet.BulletCollision.btBroadphaseProxy;
import org.bytedeco.bullet.BulletCollision.btCollisionWorld;
import org.bytedeco.bullet.BulletDynamics.btDynamicsWorld;
import org.bytedeco.bullet.LinearMath.btVector3;
import ru.BouH.engine.graph.Graph;
import ru.BouH.engine.physics.entities.BodyGroup;

import java.util.ArrayList;
import java.util.Stack;

public class TerrainGraphGenerator {
    private final Graph graph;
    private final ArrayList<Graph.GVertex> all;
    private final btDynamicsWorld world;

    public TerrainGraphGenerator(btDynamicsWorld world, Graph graph) {
        this.graph = graph;
        this.all = new ArrayList<>();
        this.world = world;
    }

    public void generate(Graph.GVertex start) {
        Stack<Graph.GVertex> stack = new Stack<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            Graph.GVertex current = stack.pop();
            this.getGraph().addVertex(current);
            this.all.add(current);
            double off = 1.0d;

            Graph.GVertex[] vertices = new Graph.GVertex[4];
            vertices[0] = this.tryPlaceVertex(current.getX(), current.getY(), current.getZ(), current.getX() - off, current.getY(), current.getZ());
            vertices[1] = this.tryPlaceVertex(current.getX(), current.getY(), current.getZ(), current.getX() + off, current.getY(), current.getZ());
            vertices[2] = this.tryPlaceVertex(current.getX(), current.getY(), current.getZ(), current.getX(), current.getY(), current.getZ() - off);
            vertices[3] = this.tryPlaceVertex(current.getX(), current.getY(), current.getZ(), current.getX(), current.getY(), current.getZ() + off);

            for (Graph.GVertex vertex : vertices) {
                if (vertex != null) {
                    Graph.GVertex v = this.checkNeighbors(vertex);
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

    public Graph.GVertex startPos(double x, double y, double z) {
        btVector3 va1 = new btVector3(x, y, z);
        btVector3 va2 = new btVector3(x, y - 5.0d, z);
        btVector3 hit = null;

        btCollisionWorld.ClosestRayResultCallback rayResultCallback = new btCollisionWorld.ClosestRayResultCallback(va1, va2);
        rayResultCallback.m_collisionFilterMask(btBroadphaseProxy.DefaultFilter & ~BodyGroup.PlayerFilter & ~BodyGroup.LiquidFilter & ~BodyGroup.GhostFilter);
        this.getWorld().rayTest(va1, va2, rayResultCallback);

        if (rayResultCallback.hasHit()) {
            hit = rayResultCallback.m_hitPointWorld();
        }

        va1.deallocate();
        va2.deallocate();
        rayResultCallback.deallocate();

        if (hit == null) {
            return new Graph.GVertex(x, y, z);
        } else {
            Graph.GVertex gVertex = new Graph.GVertex(hit.x(), hit.y(), hit.z());
            hit.deallocate();
            return gVertex;
        }
    }

    private Graph.GVertex tryPlaceVertex(double oldX, double oldY, double oldZ, double x, double y, double z) {
        Graph.GVertex vertex = null;
        btVector3 va1 = new btVector3(x, y + 1.5d, z);
        btVector3 va2 = new btVector3(x, y - 3.0d, z);
        btVector3 va3 = new btVector3(oldX, oldY + 0.1d, oldZ);
        btVector3 va4 = new btVector3(x, y + 1.5d, z);

        btCollisionWorld.ClosestRayResultCallback rayResultCallback = new btCollisionWorld.ClosestRayResultCallback(va1, va2);
        rayResultCallback.m_collisionFilterMask(btBroadphaseProxy.DefaultFilter & ~BodyGroup.PlayerFilter & ~BodyGroup.LiquidFilter & ~BodyGroup.GhostFilter);
        btCollisionWorld.ClosestRayResultCallback rayResultCallback2 = new btCollisionWorld.ClosestRayResultCallback(va3, va4);
        rayResultCallback2.m_collisionFilterMask(btBroadphaseProxy.DefaultFilter & ~BodyGroup.PlayerFilter & ~BodyGroup.LiquidFilter & ~BodyGroup.GhostFilter);

        this.getWorld().rayTest(va1, va2, rayResultCallback);
        this.getWorld().rayTest(va3, va4, rayResultCallback2);
        if (rayResultCallback.hasHit() && !rayResultCallback2.hasHit()) {
            btVector3 v = rayResultCallback.m_hitPointWorld();
            vertex = new Graph.GVertex(v.x(), v.y(), v.z());
        }
        va1.deallocate();
        va2.deallocate();
        rayResultCallback.deallocate();
        va3.deallocate();
        va4.deallocate();
        rayResultCallback2.deallocate();

        return vertex;
    }

    private Graph.GVertex checkNeighbors(Graph.GVertex vertex) {
        for (Graph.GVertex v : this.getGraph().getGraphContainer().keySet()) {
            if (v != vertex && v.distanceTo(vertex) <= 0.01d) {
                return v;
            }
        }
        return null;
    }

    public btDynamicsWorld getWorld() {
        return this.world;
    }

    public ArrayList<Graph.GVertex> getAll() {
        return this.all;
    }

    public Graph getGraph() {
        return this.graph;
    }
}
