package deque;

import java.util.Iterator;
import java.util.LinkedList;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] deque;
    private int size;
    private int front;
    private int rear;
    private int mid;

    public ArrayDeque() {
        deque = (T[]) new Object[8];
        size = 0;
        mid = deque.length / 2;
        front = mid - 1;
        rear = mid;
    }

    private void resize(int newSize) {
        T[] newDeque = (T[]) new Object[newSize];
        int newMid = newDeque.length / 2;
        int leftLength = mid - 1 - front;
        int startIndex = newMid; //- leftLength;
        System.arraycopy(deque, front + 1, newDeque, startIndex, size);
        deque = newDeque;
        front = startIndex - 1;
        rear = startIndex + size;
        mid = newMid;
    }

    public void addFirst(T item) {
        if (front != -1) {
            deque[front--] = item;
            size++;
        } else {
            resize(2 * deque.length);
            addFirst(item);
        }
    }

    public void addLast(T item) {
        if (rear != deque.length) {
            deque[rear++] = item;
            size++;
        } else {
            resize(2 * deque.length);
            addLast(item);
        }
    }

    public int size() {
        return size;
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T ans = deque[++front];
        size--;
        if (size < deque.length / 4 && deque.length > 8) {
            resize(deque.length / 2);
        }
        return ans;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T ans = deque[--rear];
        size--;
        if (size < deque.length / 4 && deque.length > 8) {
            resize(deque.length / 2);
        }
        return ans;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return deque[front + 1 + index];
    }


    @Override
    public Iterator<T> iterator() {
        return new dequeIterator();
    }

    private class dequeIterator implements Iterator<T> {
        public int index = front + 1;

        @Override
        public boolean hasNext() {
            return index != rear;
        }

        @Override
        public T next() {
            T ans = deque[index++];
            return ans;
        }
    }

    @Override
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
        Deque<T> another = (Deque<T>) o;
        if (this.size != another.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(another.get(i))) {
                return false;
            }
        }
        return true;
    }

    public void printDeque() {
        int index = front + 1;
        while (index != rear) {
            System.out.print(deque[index] + " ");
            index++;
        }
        System.out.println();
    }
}

