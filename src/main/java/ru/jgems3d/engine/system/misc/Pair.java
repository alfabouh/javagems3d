package ru.jgems3d.engine.system.misc;

public class Pair<K, V> {

    private final K first;
    private final V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
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