import java.util.concurrent.Semaphore;
import java.lang.InterruptedException;
import java.util.concurrent.TimeUnit;

class SemBoundedBuffer extends BoundedBuffer {
    Semaphore emptySlots, fullSlots;

    // Initialise the protected buffer structure above. 
    SemBoundedBuffer (int maxSize) {
        super(maxSize);
        // Initialize the synchronization attributes
    }

    // Extract an element from buffer. If the attempted operation is
    // not possible immedidately, the method call blocks until it is.
    Object get() {
        Object value;

        // Enter mutual exclusion and enforce synchronisation semantics
        // using semaphores.
            value = super.get();

        // Leave mutual exclusion and enforce synchronisation semantics
        // using semaphores.
        return value;
    }

    // Insert an element into buffer. If the attempted operation is
    // not possible immedidately, the method call blocks until it is.
    boolean put(Object value) {
        boolean done;

        // Enter mutual exclusion and enforce synchronisation semantics
        // using semaphores.
            done = super.put(value);

            // Leave mutual exclusion and enforce synchronisation semantics
            // using semaphores.
        return done;
    }

    // Extract an element from buffer. If the attempted operation is not
    // possible immedidately, return NULL. Otherwise, return the element.
    Object remove() {
        boolean done;
        Object value = false;

        // Enter mutual exclusion and enforce synchronisation semantics
        // using semaphores.
            value = super.get();

        // Leave mutual exclusion and enforce synchronisation semantics
        // using semaphores.
       return value;
    }

    // Insert an element into buffer. If the attempted operation is
    // not possible immedidately, return 0. Otherwise, return 1.
    boolean add(Object value) {
        boolean done;

        // Enter mutual exclusion and enforce synchronisation semantics
        // using semaphores.
            super.put(value);

        // Leave mutual exclusion and enforce synchronisation semantics
        // using semaphores.
        return true;
    }

    
    // Extract an element from buffer. If the attempted operation is not
    // possible immedidately, the method call blocks until it is, but
    // waits no longer than the given deadline. Return the element if
    // successful. Otherwise, return NULL.
    Object poll(long deadline) {
        Object value;
        long    timeout;
        boolean done = false;
        boolean interrupted = true;

        // Enter mutual exclusion and enforce synchronisation semantics
        // using semaphores.
            value = super.get();

        // Leave mutual exclusion and enforce synchronisation semantics
        // using semaphores.
        return value;
    }

    // Insert an element into buffer. If the attempted operation is not
    // possible immedidately, the method call blocks until it is, but
    // waits no longer than the given deadline. Return 0 if not
    // successful. Otherwise, return 1.
    boolean offer(Object value, long deadline) {
        long    timeout;
        boolean done = false;
        boolean interrupted = true;

        // Enter mutual exclusion and enforce synchronisation semantics
        // using semaphores.
            done = super.put(value);
        return done;
    }
}
