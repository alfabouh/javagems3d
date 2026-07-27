/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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
        return new Vector3f(this.randomFloat(Math.abs(bound))).mul(bound < 0.0f ? -1.0f : 1.0f);
    }

    public Vector2f randomVector2f(float bound) {
        return new Vector2f(this.randomFloat(Math.abs(bound))).mul(bound < 0.0f ? -1.0f : 1.0f);
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