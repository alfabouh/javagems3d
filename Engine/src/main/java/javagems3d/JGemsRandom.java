package javagems3d;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Random;

public final class JGemsRandom {
    public static JGemsRandom instance = null;
    private final Random random;

    static {
        JGemsRandom.instance = new JGemsRandom(0L);
    }

    public void init(long seed) {
        JGemsRandom.instance = new JGemsRandom(seed);
    }

    private JGemsRandom(long seed) {
        this.random = new Random(seed);
    }

    public Vector3f randomVector3f(Vector3f defaultValue) {
        return new Vector3f(defaultValue).sub(new Vector3f(JGemsRandom.getRandom().nextFloat(), JGemsRandom.getRandom().nextFloat(), JGemsRandom.getRandom().nextFloat()).mul(defaultValue).mul(2.0f));
    }

    public Vector3f randomVector3f(Vector3f defaultValue, Vector3f mask) {
        return new Vector3f(defaultValue).sub(new Vector3f(JGemsRandom.getRandom().nextFloat(), JGemsRandom.getRandom().nextFloat(), JGemsRandom.getRandom().nextFloat()).mul(mask));
    }

    public Vector3f randomVector3f(float defaultValue, Vector3f mask) {
        return new Vector3f(defaultValue).sub(new Vector3f(JGemsRandom.getRandom().nextFloat(), JGemsRandom.getRandom().nextFloat(), JGemsRandom.getRandom().nextFloat()).mul(mask));
    }

    public Vector2f randomVector2f(float defaultValue, Vector2f mask) {
        return new Vector2f(defaultValue).sub(new Vector2f(JGemsRandom.getRandom().nextFloat(), JGemsRandom.getRandom().nextFloat()).mul(mask));
    }

    public Vector3f randomVector3f(float bound) {
        return new Vector3f(this.randomFloat(bound), this.randomFloat(bound), this.randomFloat(bound));
    }

    public Vector2f randomVector2f(float bound) {
        return new Vector2f(this.randomFloat(bound), this.randomFloat(bound));
    }

    public float randomFloat(float range) {
        return range == 0.0f ? 0.0f : JGemsRandom.getRandom().nextFloat(range);
    }

    public float randomFloatDuo(float range) {
        return range == 0.0f ? 0.0f : JGemsRandom.getRandom().nextFloat(range * 2.0f) - range;
    }

    public float randomInt(int from, int to) {
        return JGemsRandom.getRandom().nextInt(from, to);
    }

    public boolean randomBoolean() {
        return JGemsRandom.getRandom().nextBoolean();
    }

    public static Random getRandom() {
        return JGemsRandom.instance.random;
    }
}