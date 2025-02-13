package javagems3d.system.resources.assets.models.animation;

public class AnimationData {
    private Animation currentAnimation;
    private int currentFrameId;
    private int previousFrameId;
    private double animationFrameDelta;

    public AnimationData() {
        this(null);
    }

    public AnimationData(Animation animation) {
        this.currentAnimation = animation;
        this.previousFrameId = 0;
        this.animationFrameDelta = 0.0f;
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
        this.previousFrameId = this.currentFrameId;
        this.currentFrameId = frame;
    }

    public float getFrameRate() {
        return this.getCurrentAnimation().getFrameRate();
    }

    public double getAnimationFrameDelta() {
        return this.animationFrameDelta;
    }

    public void setAnimationFrameDelta(double animationFrameDelta) {
        this.animationFrameDelta = animationFrameDelta;
    }

    public AnimationFrame getCurrentAnimationFrame() {
        return this.getCurrentAnimation().getFrameList().get(this.getCurrentFrameId());
    }

    public AnimationFrame getPreviousAnimationFrame() {
        return this.getCurrentAnimation().getFrameList().get(this.getPreviousFrameId());
    }

    public int getPreviousFrameId() {
        return this.previousFrameId;
    }

    public int getCurrentFrameId() {
        return this.currentFrameId;
    }

    public void setAnimation(Animation animation) {
        this.currentFrameId = 0;
        this.previousFrameId = 0;
        this.currentAnimation = animation;
    }

    public Animation getCurrentAnimation() {
        return this.currentAnimation;
    }

    public boolean isValid() {
        return this.getCurrentAnimation() != null;
    }
}
