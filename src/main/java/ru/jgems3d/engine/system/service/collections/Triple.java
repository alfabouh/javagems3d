/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.system.service.collections;

public class Triple<K, V, R> {

    private final K first;
    private final V second;
    private final R third;

    public Triple(K first, V second, R third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public R getThird() {
        return this.third;
    }

    public K getFirst() {
        return this.first;
    }

    public V getSecond() {
        return this.second;
    }

    @Override
    public String toString() {
        return first + " + " + second;
    }
}