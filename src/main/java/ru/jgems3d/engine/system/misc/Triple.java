package ru.jgems3d.engine.system.misc;

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