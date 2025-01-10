package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    @Override
    public Iterator<T> iterator() {
        return new dequeIterator();
    }

    public class Node {
        T item;
        Node prev;
        Node next;

        public Node(T item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private class dequeIterator implements Iterator<T> {
        Node current = head.next;

        @Override
        public boolean hasNext() {
            return current != tail;
        }

        @Override
        public T next() {
            T ans = current.item;
            current = current.next;
            return ans;
        }
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Iterator<T> it1 = this.iterator();
        if(o instanceof ArrayDeque) {
            ArrayDeque<T> another = (ArrayDeque<T>) o;
            Iterator<T> it2 = another.iterator();
            while (it1.hasNext() && it2.hasNext()) {
                if (!it1.next().equals(it2.next())) {
                    return false;
                }
            }
        } else {
            LinkedListDeque<T> another = (LinkedListDeque<T>) o;
            Iterator<T> it2 = another.iterator();
            while (it1.hasNext() && it2.hasNext()) {
                if (!it1.next().equals(it2.next())) {
                    return false;
                }
            }
        }
        return true;
    }

    public Node head;
    public Node tail;
    public int size;

    public LinkedListDeque() {
        head = new Node(null, null, null);
        tail = new Node(null, null, null);
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    public int size() {
        return size;
    }

    public void addFirst(T item) {
        Node newNode = new Node(item, null, null);
        newNode.next = head.next;
        head.next.prev = newNode;
        head.next = newNode;
        newNode.prev = head;
        size++;
    }

    public void addLast(T item) {
        Node newNode = new Node(item, null, null);
        newNode.prev = tail.prev;
        tail.prev.next = newNode;
        tail.prev = newNode;
        newNode.next = tail;
        size++;
    }

    public void printDeque() {
        Node current = head.next;
        while (current != tail) {
            System.out.print(current.item + " ");
            current = current.next;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T ans = head.next.item;
        head.next = head.next.next;
        head.next.prev = head;
        size--;
        return ans;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T ans = tail.prev.item;
        tail.prev = tail.prev.prev;
        tail.prev.next = tail;
        size--;
        return ans;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node current = head.next;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.item;
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRec(head.next, 0, index);
    }

    public T getRec(Node curr, int i, int index) {
        if (i == index) {
            return curr.item;
        } else {
            return getRec(curr.next, i + 1, index);
        }
    }
}
