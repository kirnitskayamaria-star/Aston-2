package org.example;

import java.util.Map;
import java.util.Objects;

public class Node<K, V> implements Map.Entry<K, V> {
    final K key;
    V value;
    Node<K, V> next;

    Node(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Map.Entry)) return false;
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
        return Objects.equals(key, entry.getKey()) &&
                Objects.equals(value, entry.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key) ^ Objects.hashCode(value);
    }
}
