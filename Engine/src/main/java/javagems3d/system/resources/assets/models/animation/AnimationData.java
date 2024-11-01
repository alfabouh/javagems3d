package javagems3d.system.resources.assets.models.animation;

public class AnimationData {
    private Animation currentAnimation;
    private int currentFrameId;

    public AnimationData() {
        this(null);
    }

    public AnimationData(Animation animation) {
        this.currentAnimation = animation;
    }

    public void nextFrame() {
        int nextFrame = this.getCurrentFrameId() + 1;
        if (nextFrame > this.getCurrentAnimation().getFrameList().size() - 1) {
            this.setFrame(0);
        } else {
            this.setFrame(nextFrame);
        }
    }

    public void setFrame(int frame) {
        this.currentFrameId = frame;
    }

    public AnimationFrame getCurrentAnimationFrame() {
        return this.getCurrentAnimation().getFrameList().get(this.getCurrentFrameId());
    }

    public int getCurrentFrameId() {
        return this.currentFrameId;
    }

    public void setAnimation(Animation animation) {
        this.currentFrameId = 0;
        this.currentAnimation = animation;
    }

    public Animation getCurrentAnimation() {
        return this.currentAnimation;
    }

    public boolean isValid() {
        return this.getCurrentAnimation() != null;
    }
}
