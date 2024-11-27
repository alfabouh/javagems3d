package javagems3d.graphics.opengl.rendering.scene.inderect;

import javagems3d.system.resources.assets.models.mesh.DataMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.mesh.vertex.buffers.VertexBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.RenderAttributePointer;
import javagems3d.system.resources.manager.mesh.MeshBuffersDrawCache;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.stream.Collectors;

public final class IndirectRenderBuffer {
    private int staticVao;
    private final List<Integer> vboList;

    public IndirectRenderBuffer() {
        this.vboList = new ArrayList<>();
    }

    public void init(MeshBuffersDrawCache meshBuffersDrawCache) {
        Map<Boolean, List<MeshBuffer>> partitionedModels = meshBuffersDrawCache.getMeshBuffers().stream().collect(Collectors.partitioningBy(MeshStructure::isAnimationsNotEmpty));

        List<MeshBuffer> staticModels = partitionedModels.get(false);
        List<MeshBuffer> animatedModels = partitionedModels.get(true);

        this.forStatic(staticModels);
        this.forStatic(animatedModels);
    }

    private void forStatic(List<MeshBuffer> obj) {
        if (obj.isEmpty()) {
            return;
        }

        this.staticVao = GL46.glGenVertexArrays();
        GL46.glBindVertexArray(this.getStaticVao());

        int structSize = 0;
        int indexesSize = 0;
        int positionsSize = 0;
        int offset = 0;

        for (MeshBuffer meshBuffer : obj) {
            List<MeshBuffer.PassData> passDataList = meshBuffer.getPassData();
            List<MeshBuffer.MeshBufferNode> nodes = meshBuffer.getMeshNodes();
            for (MeshBuffer.MeshBufferNode node : nodes) {
                DataMesh dataMesh = node.getMesh();
                int meshSizeInBytes = 0;
                int posLength = dataMesh.getPositionsLength();
                indexesSize += dataMesh.getIndexesBuffer().getLength();
                positionsSize += posLength;

                for (VertexBuffer<Float> attributePointer : dataMesh.getBufferMap().values()) {
                    RenderAttributePointer renderAttributePointer = attributePointer.getRenderAttributePointer();
                    structSize += attributePointer.getLength();
                    meshSizeInBytes += renderAttributePointer.getLengthInMemory() * renderAttributePointer.getBytes();
                }

                meshSizeInBytes *= posLength;
                passDataList.add(new MeshBuffer.PassData(meshSizeInBytes, node.getMaterialId(), offset, dataMesh.getIndexesBuffer().getLength()));
                offset = positionsSize / 3;
            }
        }

        int vboId = GL46.glGenBuffers();
        this.getVboList().add(vboId);
        FloatBuffer meshesBuffer = MemoryUtil.memAllocFloat(structSize);
        for (MeshBuffer meshBuffer : obj) {
            List<MeshBuffer.MeshBufferNode> nodes = meshBuffer.getMeshNodes();
            for (MeshBuffer.MeshBufferNode node : nodes) {
                DataMesh dataMesh = node.getMesh();
                this.populateMeshBuffer(meshesBuffer, dataMesh);
            }
        }
        meshesBuffer.flip();

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, vboId);
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, meshesBuffer, GL46.GL_STATIC_DRAW);
        MemoryUtil.memFree(meshesBuffer);

        this.defineVertexAttributes(obj.get(0).getFirstNode().getMesh());

        vboId = GL46.glGenBuffers();
        this.getVboList().add(vboId);
        IntBuffer indexesBuffer = MemoryUtil.memAllocInt(indexesSize);
        for (MeshBuffer meshBuffer : obj) {
            List<MeshBuffer.MeshBufferNode> nodes = meshBuffer.getMeshNodes();
            for (MeshBuffer.MeshBufferNode node : nodes) {
                DataMesh dataMesh = node.getMesh();
                for (int i : dataMesh.getIndexesBuffer().getValues()) {
                    indexesBuffer.put(i);
                }
            }
        }
        indexesBuffer.flip();

        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, vboId);
        GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, indexesBuffer, GL46.GL_STATIC_DRAW);
        MemoryUtil.memFree(indexesBuffer);

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
        GL46.glBindVertexArray(0);
    }

    private void forAnimated(List<MeshBuffer> obj) {
        //TODO
    }

    private void populateMeshBuffer(FloatBuffer floatBuffer, DataMesh dataMesh) {
        List<VertexBuffer<Float>> values = new ArrayList<>(dataMesh.getBufferMap().values());
        values.sort(Comparator.comparingInt(e -> e.getRenderAttributePointer().getIndex()));

        Map<Integer, Integer> loopPointer = new HashMap<>();
        for (int i = 0; i < dataMesh.getPositionsLength(); i += 3) {
            for (VertexBuffer<Float> p : values) {
                int idx = p.getRenderAttributePointer().getIndex();
                loopPointer.put(idx, 0);
                int curr = loopPointer.get(idx);
                int len = p.getRenderAttributePointer().getLengthInMemory();
                int next = curr + len;
                for (int j = curr; j < next; j += 1) {
                    floatBuffer.put(p.getValues().get(j));
                }
                loopPointer.replace(idx, next);
            }
        }
    }

    private void defineVertexAttributes(DataMesh renderMesh) {
        int stride = 0;
        int pointer = 0;

        for (VertexBuffer<Float> vertexBuffer : renderMesh.getBufferMap().values()) {
            RenderAttributePointer renderAttributePointer = vertexBuffer.getRenderAttributePointer();
            stride += renderAttributePointer.getLengthInMemory() * renderAttributePointer.getBytes();
        }

        for (VertexBuffer<Float> vertexBuffer : renderMesh.getBufferMap().values()) {
            RenderAttributePointer renderAttributePointer = vertexBuffer.getRenderAttributePointer();
            int idx = renderAttributePointer.getIndex();
            int size = renderAttributePointer.getLengthInMemory();

            GL46.glEnableVertexAttribArray(idx);
            GL46.glVertexAttribPointer(idx, size, GL46.GL_FLOAT, false, stride, pointer);
            pointer += renderAttributePointer.getLengthInMemory() * renderAttributePointer.getBytes();
        }
    }

    public void clear() {
        GL46.glDeleteVertexArrays(this.getStaticVao());
        this.getVboList().forEach(GL46::glDeleteBuffers);
        this.getVboList().clear();
    }

    public int getStaticVao() {
        return this.staticVao;
    }

    public void setStaticVao(int staticVao) {
        this.staticVao = staticVao;
    }

    public List<Integer> getVboList() {
        return this.vboList;
    }
}
