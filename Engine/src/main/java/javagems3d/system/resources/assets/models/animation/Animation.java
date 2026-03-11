package javagems3d.system.resources.assets.models.animation;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public record Animation(String name, double duration, double fps, List<AnimationFrame> frameList) {

    public void clear() {
        this.frameList().forEach(AnimationFrame::clear);
        this.frameList().clear();
    }

    public int getFrameCount() {
        return this.frameList().size();
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
