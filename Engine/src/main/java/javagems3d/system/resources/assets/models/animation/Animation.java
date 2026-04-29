package javagems3d.system.resources.assets.models.animation;

import java.util.List;

public record Animation(String name, double duration, double fps, List<AnimationFrame> frameList) {

    public void clear() {
        this.frameList().forEach(AnimationFrame::clear);
        this.frameList().clear();
    }

    public int getFrameCount() {
        return this.frameList().size();
    }
}
