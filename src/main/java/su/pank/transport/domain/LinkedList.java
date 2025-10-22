package su.pank.transport.domain;

public interface LinkedList<T> {
    void add(T item);
    T get(int index);
    int size();
    boolean isEmpty();
    T[] toArray();
}