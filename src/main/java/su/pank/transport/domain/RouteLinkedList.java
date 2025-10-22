package su.pank.transport.domain;

import su.pank.transport.data.models.Route;

import java.util.ArrayList;
import java.util.List;

public class RouteLinkedList {
    private RouteNode head;
    private int size;

    public RouteLinkedList() {
        this.head = null;
        this.size = 0;
    }

    public void add(Route route) {
        RouteNode newNode = new RouteNode(route);
        if (head == null) {
            head = newNode;
        } else {
            RouteNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    public boolean remove(Route route) {
        if (head == null) return false;

        if (head.data.getId() == route.getId()) {
            head = head.next;
            size--;
            return true;
        }

        RouteNode current = head;
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

    public Route get(int index) {
        if (index < 0 || index >= size) return null;
        RouteNode current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    public int size() {
        return size;
    }

    public void clear() {
        head = null;
        size = 0;
    }

    // Линейный поиск по номеру маршрута
    public Route linearSearch(int routeNumber) {
        RouteNode current = head;
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

        RouteNode sorted = null;
        RouteNode current = head;

        while (current != null) {
            RouteNode next = current.next;
            sorted = sortedInsert(sorted, current);
            current = next;
        }
        head = sorted;
    }

    private RouteNode sortedInsert(RouteNode sorted, RouteNode newNode) {
        if (sorted == null || sorted.data.getRouteNumber() >= newNode.data.getRouteNumber()) {
            newNode.next = sorted;
            return newNode;
        }

        RouteNode current = sorted;
        while (current.next != null && current.next.data.getRouteNumber() < newNode.data.getRouteNumber()) {
            current = current.next;
        }
        newNode.next = current.next;
        current.next = newNode;
        return sorted;
    }

    public List<Route> toList() {
        List<Route> list = new ArrayList<>();
        RouteNode current = head;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }

    static class RouteNode {
        Route data;
        RouteNode next;

        RouteNode(Route data) {
            this.data = data;
            this.next = null;
        }
    }
}