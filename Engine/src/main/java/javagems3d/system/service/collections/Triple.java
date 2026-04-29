package javagems3d.system.service.collections;

public record Triple<K, V, R>(K first, V second, R third) {

    @Override
    public String toString() {
        return first + " + " + second;
    }
}