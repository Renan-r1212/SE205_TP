class BoundedBuffer {
    int first, last, size, maxSize;
    Object content[];

    BoundedBuffer (int maxSize) {
        this.first = 0;
        this.last  = 0;
        this.size  = 0;
        this.maxSize = maxSize;
        this.content = new Object[maxSize];
    }

    Object get() {
        if (size == 0) return null;
        Object value = content[first];
        first = (first + 1) % maxSize;
        size  = size - 1;
        return value;
    }

    boolean put(Object value) {
        if (size == maxSize) return false;
        content[last] = value;
        last = (last + 1) % maxSize;
        size = size + 1;
        return true;
    }

    Object remove() {
        return get();
    }

    boolean add(Object value) {
        return put(value);
    }

    Object poll(long timeout) {
        return get();
    }

    boolean offer(Object value, long timeout) {
        return put(value);
    }
}
