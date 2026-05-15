package org.example;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

class MyEntrySet<K, V> extends AbstractSet<Map.Entry<K, V>> {
    private final MyHashMap<K, V> map;
    private final Node<K, V>[] table;

    MyEntrySet(MyHashMap<K, V> map, Node<K, V>[] table) {
        this.map = map;
        this.table = table;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new MyHashMapIterator<>(map, table);
    }
}

