package su.pank.transport.domain;

import su.pank.transport.data.models.Route;

public class RouteLinkedList extends SimpleLinkedList<Route> {

    public RouteLinkedList() {
        super(Route.class);
    }

    public boolean remove(Route route) {
        if (head == null) return false;

        if (head.data.getId() == route.getId()) {
            head = head.next;
            size--;
            return true;
        }

        Node<Route> current = head;
        while (current.next != null) {
            if (current.next.data.getId() == route.getId()) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void clear() {
        head = null;
        size = 0;
    }

    // Линейный поиск по номеру маршрута
    public Route linearSearch(int routeNumber) {
        Node<Route> current = head;
        while (current != null) {
            if (current.data.getRouteNumber() == routeNumber) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    // Сортировка вставками по номеру маршрута
    public void insertionSort() {
        if (head == null || head.next == null) return;

        Node<Route> sorted = null;
        Node<Route> current = head;

        while (current != null) {
            Node<Route> next = current.next;
            sorted = sortedInsert(sorted, current);
            current = next;
        }
        head = sorted;
    }

    private Node<Route> sortedInsert(Node<Route> sorted, Node<Route> newNode) {
        if (sorted == null || sorted.data.getRouteNumber() >= newNode.data.getRouteNumber()) {
            newNode.next = sorted;
            return newNode;
        }

        Node<Route> current = sorted;
        while (current.next != null && current.next.data.getRouteNumber() < newNode.data.getRouteNumber()) {
            current = current.next;
        }
        newNode.next = current.next;
        current.next = newNode;
        return sorted;
    }
}