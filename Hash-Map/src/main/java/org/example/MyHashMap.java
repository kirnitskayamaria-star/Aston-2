package org.example;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class MyHashMap<K, V> extends AbstractMap<K, V> {

    private Node<K, V>[] table;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        table = new Node[DEFAULT_CAPACITY];
    }

    private int hash(Object key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    private int getBucketIndex(Object key) {
        return hash(key) & (table.length - 1);
    }

    @Override
    public V put(K key, V value) {
        if (table.length == 0 || size >= table.length * LOAD_FACTOR) {
            resize();
        }
        int index = getBucketIndex(key);
        Node<K, V> head = table[index];

        while (head != null) {
            if (isKeyEqual(head.key, key)) {
                V oldValue = head.value;
                head.value = value;
                return oldValue;
            }
            head = head.next;
        }
        Node<K, V> newNode = new Node<>(key, value);
        newNode.next = table[index];
        table[index] = newNode;
        size++;
        return null;
    }


    @Override
    public V get(Object key) {
        int index = getBucketIndex(key);
        Node<K, V> head = table[index];

        while (head != null) {
            if (isKeyEqual(head.key, key)) {
                return head.value;
            }
            head = head.next;
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        int index = getBucketIndex(key);
        Node<K, V> head = table[index];
        Node<K, V> prev = null;

        while (head != null) {
            if (isKeyEqual(head.key, key)) {
                if (prev != null) {
                    prev.next = head.next;
                } else {
                    table[index] = head.next;
                }
                size--;
                return head.value;
            }
            prev = head;
            head = head.next;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        if (size > 0) {
            size = 0;
            Arrays.fill(table, null);
        }
    }

    private boolean isKeyEqual(Object k1, Object k2) {
        return k1 == k2 || (k1 != null && k1.equals(k2));
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        int newCapacity = oldTable.length == 0 ? 16 : oldTable.length * 2;
        table = new Node[newCapacity];
        for (Node<K, V> head : oldTable) {
            while (head != null) {
                Node<K, V> next = head.next;
                int newIndex = getBucketIndex(head.key);
                head.next = table[newIndex];
                table[newIndex] = head;
                head = next;
            }
        }
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new MyEntrySet<>(this, table);
    }
}

