package ru.jgems3d.engine.math;

public class Triple<K, V, E> {

    private final K key;
    private final V value;
    private final E value2;

    public Triple(K key, V value, E value2) {
        this.key = key;
        this.value = value;
        this.value2 = value2;
    }

    public K getFirst() {
        return this.key;
    }

    public V getSecond() {
        return this.value;
    }

    public E getThird() {
        return this.value2;
    }

    @Override
    public String toString() {
        return this.key + "=" + this.value + " + " + this.value2;
    }
}