package javagems3d.system.resources.assets.models.animation;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public final class Animation {
    private final String name;
    private final double duration;
    private final List<AnimationFrame> frameList;
    private int offset;

    public Animation(String name, double duration, List<AnimationFrame> frameList) {
        this.name = name;
        this.duration = duration;
        this.frameList = frameList;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getName() {
        return this.name;
    }

    public double getDuration() {
        return this.duration;
    }

    public List<AnimationFrame> getFrameList() {
        return this.frameList;
    }

    public int getFrameCount() {
        return this.getFrameList().size();
    }

    public static final class Node {
        private final List<Node> leaves;
        private final String name;
        private final Node parent;
        private final Matrix4f transformation;

        public Node(String name, Node parent, Matrix4f transformation) {
            this.name = name;
            this.parent = parent;
            this.transformation = transformation;
            this.leaves = new ArrayList<>();
        }

        public void addLeaf(Node node) {
            this.getLeaves().add(node);
        }

        public List<Node> getLeaves() {
            return this.leaves;
        }

        public String getName() {
            return this.name;
        }

        public Node getParent() {
            return this.parent;
        }

        public Matrix4f getTransformation() {
            return this.transformation;
        }
    }
}
