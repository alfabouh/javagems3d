package ru.jgems3d.engine.system.navgraph.pathgen;

public class TerrainGraphGenerator {
 // private final Graph graph;
 // private final btDynamicsWorld world;

 // public TerrainGraphGenerator(btDynamicsWorld world, Graph graph) {
 //     this.graph = graph;
 //     this.world = world;
 // }

 // public void generate(Graph.GVertex start) {
 //     Stack<Graph.GVertex> stack = new Stack<>();
 //     stack.push(start);

 //     while (!stack.isEmpty()) {
 //         Graph.GVertex current = stack.pop();
 //         this.getGraph().addVertex(current);
 //         float off = 1.0f;

 //         Graph.GVertex[] vertices = new Graph.GVertex[4];
 //         vertices[0] = this.tryPlaceVertex(current, current.getX() - off, current.getY(), current.getZ());
 //         vertices[1] = this.tryPlaceVertex(current, current.getX() + off, current.getY(), current.getZ());
 //         vertices[2] = this.tryPlaceVertex(current, current.getX(), current.getY(), current.getZ() - off);
 //         vertices[3] = this.tryPlaceVertex(current, current.getX(), current.getY(), current.getZ() + off);

 //         for (Graph.GVertex vertex : vertices) {
 //             if (vertex != null) {
 //                 Graph.GVertex v = this.checkNeighbors(vertex);
 //                 if (v != null) {
 //                     this.getGraph().addEdge(v, current);
 //                     this.getGraph().addEdge(current, v);
 //                 } else {
 //                     stack.push(vertex);
 //                 }
 //             }
 //         }
 //     }
 // }

 // public Graph.GVertex startPos(float x, float y, float z) {
 //     btVector3 va1 = new btVector3(x, y, z);
 //     btVector3 va2 = new btVector3(x, y - 100.0d, z);
 //     btVector3 hit = null;

 //     btCollisionWorld.ClosestRayResultCallback rayResultCallback = new btCollisionWorld.ClosestRayResultCallback(va1, va2);
 //     rayResultCallback.m_collisionFilterMask(btBroadphaseProxy.DefaultFilter & ~BodyGroup.PlayerFilter & ~BodyGroup.LiquidFilter & ~BodyGroup.GhostFilter & ~BodyGroup.DefaultByPassNavChecks);
 //     this.getWorld().rayTest(va1, va2, rayResultCallback);

 //     if (rayResultCallback.hasHit()) {
 //         hit = rayResultCallback.m_hitPointWorld();
 //     }

 //     va1.deallocate();
 //     va2.deallocate();
 //     rayResultCallback.deallocate();

 //     if (hit == null) {
 //         return new Graph.GVertex(x, y, z);
 //     } else {
 //         Graph.GVertex gVertex = new Graph.GVertex((float) hit.x(), (float) hit.y(), (float) hit.z());
 //         hit.deallocate();
 //         return gVertex;
 //     }
 // }

 // private Graph.GVertex tryPlaceVertex(Graph.GVertex current, float x, float y, float z) {
 //     Graph.GVertex vertex = null;
 //     btVector3 va1 = new btVector3(x, y + 1.5d, z);
 //     btVector3 va2 = new btVector3(x, y - 3.0d, z);
 //     btVector3 va3 = new btVector3(current.getX(), current.getY() + 0.1d, current.getZ());
 //     btVector3 va4 = new btVector3(x, y + 1.5d, z);

 //     btCollisionWorld.ClosestRayResultCallback rayResultCallback = new btCollisionWorld.ClosestRayResultCallback(va1, va2);
 //     final int setter = btBroadphaseProxy.DefaultFilter & ~BodyGroup.PlayerFilter & ~BodyGroup.LiquidFilter & ~BodyGroup.GhostFilter & ~BodyGroup.DefaultByPassNavChecks;

 //     rayResultCallback.m_collisionFilterMask(setter);
 //     btCollisionWorld.ClosestRayResultCallback rayResultCallback2 = new btCollisionWorld.ClosestRayResultCallback(va3, va4);
 //     rayResultCallback2.m_collisionFilterMask(setter);

 //     this.getWorld().rayTest(va1, va2, rayResultCallback);
 //     this.getWorld().rayTest(va3, va4, rayResultCallback2);
 //     if (rayResultCallback.hasHit() && !rayResultCallback2.hasHit()) {
 //         btVector3 v = rayResultCallback.m_hitPointWorld();
 //         vertex = new Graph.GVertex((float) v.x(), (float) v.y(), (float) v.z());
 //     } else {
 //         if (rayResultCallback2.hasHit()) {
 //             btVector3 v1 = rayResultCallback2.m_hitPointWorld();
 //             btVector3 v1_s = new btVector3(v1.x(), v1.y(), v1.z());
 //             v1_s.subtractPut(va3).multiplyPut(0.5d);
 //             v1.subtractPut(v1_s);
 //             v1_s.deallocate();
 //             btVector3 va5 = new btVector3(v1.x(), y + 1.0d, v1.z());
 //             btVector3 va6 = new btVector3(v1.x(), y - 2.0d, v1.z());
 //             btCollisionWorld.ClosestRayResultCallback rayResultCallback3 = new btCollisionWorld.ClosestRayResultCallback(va5, va6);
 //             rayResultCallback3.m_collisionFilterMask(setter);
 //             this.getWorld().rayTest(va5, va6, rayResultCallback3);

 //             if (rayResultCallback3.hasHit()) {
 //                 btVector3 v2 = rayResultCallback3.m_hitPointWorld();
 //                 if (v2.y() <= v1.y() + 0.5d && v2.distance(va3) > 0.25d) {
 //                     Graph.GVertex v = new Graph.GVertex((float) v2.x(), (float) v2.y(), (float) v2.z());
 //                     this.getGraph().addVertex(v);
 //                     this.getGraph().addEdge(v, current);
 //                     this.getGraph().addEdge(current, v);
 //                 }
 //             }
 //         }
 //     }
 //     va1.deallocate();
 //     va2.deallocate();
 //     rayResultCallback.deallocate();
 //     va3.deallocate();
 //     va4.deallocate();
 //     rayResultCallback2.deallocate();

 //     return vertex;
 // }

 // private Graph.GVertex checkNeighbors(Graph.GVertex vertex) {
 //     for (Graph.GVertex v : this.getGraph().getGraphContainer().keySet()) {
 //         if (v != vertex && v.distanceTo(vertex) <= 0.01d) {
 //             return v;
 //         }
 //     }
 //     return null;
 // }

 // public btDynamicsWorld getWorld() {
 //     return this.world;
 // }

 // public Graph getGraph() {
 //     return this.graph;
 // }
}
