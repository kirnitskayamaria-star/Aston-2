package org.example;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

class MyHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
    private final MyHashMap<K, V> map;
    private final Node<K, V>[] table;
    private int currentBucket = 0;
    private Node<K, V> currentNode = null;
    private Node<K, V> lastReturned = null;

    @SuppressWarnings("unchecked")
    MyHashMapIterator(MyHashMap<K, V> map, Node<K, V>[] table) {
        this.map = map;
        this.table = table;
        advanceToNextNode();
    }

    private void advanceToNextNode() {
        if (currentNode != null && currentNode.next != null) {
            currentNode = currentNode.next;
            return;
        }
        currentNode = null;
        while (currentBucket < table.length) {
            if (table[currentBucket] != null) {
                currentNode = table[currentBucket];
                currentBucket++;
                return;
            }
            currentBucket++;
        }
    }

    @Override
    public boolean hasNext() {
        return currentNode != null;
    }

    @Override
    public Map.Entry<K, V> next() {
        if (!hasNext()) throw new NoSuchElementException();
        lastReturned = currentNode;
        advanceToNextNode();
        return lastReturned;
    }

    @Override
    public void remove() {
        if (lastReturned == null) throw new IllegalStateException();
        map.remove(lastReturned.key);
        lastReturned = null;
    }
}
