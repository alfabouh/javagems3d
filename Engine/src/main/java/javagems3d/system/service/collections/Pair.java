package javagems3d.system.service.collections;

public record Pair<K, V>(K first, V second) {

    @SuppressWarnings("all")
    @SafeVarargs
    public static <K, V> Pair<K, V>[] get(Pair<K, V>... pairs) {
        Pair[] kvPair = new Pair[pairs.length];
        System.arraycopy(pairs, 0, kvPair, 0, kvPair.length);
        return kvPair;
    }

    @Override
    public String toString() {
        return first + " + " + second;
    }
}