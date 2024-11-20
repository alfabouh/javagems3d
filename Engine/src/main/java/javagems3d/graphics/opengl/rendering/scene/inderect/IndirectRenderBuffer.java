package javagems3d.graphics.opengl.rendering.scene.inderect;

import javagems3d.graphics.opengl.rendering.items.IModeled;
import javagems3d.system.resources.assets.models.mesh.IndirectRenderMesh;
import javagems3d.system.resources.assets.models.mesh.vertex.buffers.VertexBuffer;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.MeshRenderTarget;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.RenderAttributePointer;
import javagems3d.system.service.collections.Pair;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.*;
import java.util.stream.Collectors;

public final class IndirectRenderBuffer {
    private int staticVao;
    private final List<Integer> vboList;

    public IndirectRenderBuffer(List<Integer> vboList) {
        this.vboList = vboList;
    }

    public void loadBuffer(List<IModeled> modeledObjects) {
        Map<Boolean, List<IModeled>> partitionedModels = modeledObjects.stream().filter(IModeled::hasModel).filter(e -> e.getModel().getMeshStructure().getMeshTargetType().equals(MeshRenderTarget.INDIRECT)).collect(Collectors.partitioningBy(IModeled::hasAnimations));

        List<IModeled> staticModels = partitionedModels.get(false);
        List<IModeled> animatedModels = partitionedModels.get(true);

        this.forStatic(staticModels);
        this.forStatic(animatedModels);
    }

    private void forStatic(List<IModeled> obj) {
        this.staticVao = GL46.glGenVertexArrays();
        GL46.glBindVertexArray(this.getStaticVao());

        int structSize = 0;
        int positionsSize = 0;
        int offset = 0;

        for (IModeled modeled : obj) {
            MeshBuffer meshStructure = (MeshBuffer) modeled.getModel().getMeshStructure();
            List<MeshBuffer.DrawData> drawDataList = meshStructure.getDrawData();
            List<MeshBuffer.MeshNode> meshNodes = meshStructure.getMeshes();

            for (MeshBuffer.MeshNode meshNode : meshNodes) {
                IndirectRenderMesh indirectRenderMesh = meshNode.getMesh();
                int meshSizeInBytes = 0;
                int posLength = indirectRenderMesh.getBufferById(indirectRenderMesh.positionsIndex()).getLength();
                positionsSize += posLength;

                for (RenderAttributePointer attributePointer : indirectRenderMesh.getBufferMap().keySet()) {
                    structSize += attributePointer.getSize();
                    meshSizeInBytes += attributePointer.getSize() * Float.BYTES;
                }

                meshSizeInBytes *= posLength;
                drawDataList.add(new MeshBuffer.DrawData(meshSizeInBytes, meshNode.getMaterialId(), offset, indirectRenderMesh.getIndexes().getLength()));
                offset = positionsSize / 3;
            }
        }
        int vboId = GL46.glGenBuffers();
        this.getVboList().add(vboId);
        FloatBuffer meshesBuffer = MemoryUtil.memAllocFloat(structSize);
        for (IModeled modeled : obj) {
            MeshBuffer meshStructure = (MeshBuffer) modeled.getModel().getMeshStructure();
            List<MeshBuffer.MeshNode> meshNodes = meshStructure.getMeshes();

            for (MeshBuffer.MeshNode meshNode : meshNodes) {
                IndirectRenderMesh meshBuffer = meshNode.getMesh();
                this.populateMeshBuffer(meshesBuffer, meshBuffer);
            }
        }
        meshesBuffer.flip();
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, vboId);
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, meshesBuffer, GL46.GL_STATIC_DRAW);
        MemoryUtil.memFree(meshesBuffer);

        this.defineVertexAttributes();

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
        GL46.glBindVertexArray(0);
    }

    private void forAnimated(List<IModeled> obj) {
        //TODO
    }

    private void populateMeshBuffer(FloatBuffer floatBuffer, IndirectRenderMesh renderMesh) {
        List<Pair<RenderAttributePointer, VertexBuffer<Float>>> pairs = new ArrayList<>();
        for (Map.Entry<RenderAttributePointer, VertexBuffer<Float>> entry : renderMesh.getBufferMap().entrySet()) {
            pairs.add(new Pair<>(entry.getKey(), entry.getValue()));
        }
        pairs.sort(Comparator.comparingInt(e -> e.getFirst().getIndex()));

        int[] attrIdx = new int[pairs.size()];
        boolean shouldEnd = false;

        while (!shouldEnd) {
            for (Pair<RenderAttributePointer, VertexBuffer<Float>> p : pairs) {
                int stoppedAt = attrIdx[p.getFirst().getIndex()];
                for (int i = stoppedAt; i < stoppedAt + p.getFirst().getSize(); i++) {
                    floatBuffer.put(p.getSecond().getValues().get(i));
                }
                if (stoppedAt >= p.getSecond().getLength()) {
                    shouldEnd = true;
                }
            }
        }
    }

    private void defineVertexAttributes(IndirectRenderMesh renderMesh) {
        int stride = 0;
        int pointer = 0;

        for (RenderAttributePointer attributePointerSet : renderMesh.getBufferMap().keySet()) {
            stride += attributePointerSet.getSize() * Float.BYTES;
        }

        for (RenderAttributePointer attributePointerSet : renderMesh.getBufferMap().keySet()) {
            int idx = attributePointerSet.getIndex();
            int size = attributePointerSet.getSize();

            GL46.glEnableVertexAttribArray(idx);
            GL46.glVertexAttribPointer(idx, size, GL46.GL_FLOAT, false, stride, pointer);
            pointer += idx * size;
        }
    }

    public void clean() {
        GL46.glDeleteVertexArrays(this.getStaticVao());
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
