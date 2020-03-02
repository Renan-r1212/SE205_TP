import java.util.concurrent.Semaphore;
import java.lang.InterruptedException;
import java.util.concurrent.TimeUnit;

class NatBoundedBuffer extends BoundedBuffer {

    // Initialise the protected buffer structure above. 
    NatBoundedBuffer (int maxSize) {
        super(maxSize);
    }

    // Extract an element from buffer. If the attempted operation is
    // not possible immediately, the method call blocks until it is.
    Object get() {
        Object value;

        // Enter mutual exclusion
            
            // Wait until there is a full slot available.

            // Signal or broadcast that an empty slot is available (if needed)

            value = super.get();

            // Leave mutual exclusion and enforce synchronisation semantics
            // using semaphores.
        return value;
    }

    // Insert an element into buffer. If the attempted operation is
    // not possible immedidately, the method call blocks until it is.
    boolean put(Object value) {
        // Enter mutual exclusion
            
            // Wait until there is a empty slot available.

            // Signal or broadcast that a full slot is available (if needed)

            super.put(value);

            // Leave mutual exclusion and enforce synchronisation semantics
            // using semaphores.
        return true;
    }

    // Extract an element from buffer. If the attempted operation is not
    // possible immedidately, return NULL. Otherwise, return the element.
    Object remove() {
        Object value = null;

        // Enter mutual exclusion
            
            // Signal or broadcast that an empty slot is available (if needed)

            value=super.get();

            // Leave mutual exclusion
        return value;
    }

    // Insert an element into buffer. If the attempted operation is
    // not possible immedidately, return 0. Otherwise, return 1.
    boolean add(Object value) {
        boolean done;

        // Enter mutual exclusion
            
            // Signal or broadcast that a full slot is available (if needed)

            done=super.put(value);

            // Leave mutual exclusion
        return done;
    }

    // Extract an element from buffer. If the attempted operation is not
    // possible immedidately, the method call blocks until it is, but
    // waits no longer than the given deadline. Return the element if
    // successful. Otherwise, return NULL.
    Object poll(long deadline) {
        Object  value;
        long    timeout;
        long    now;
        boolean done = false;
        boolean interrupted = true;

        // Enter mutual exclusion

            // Wait until a full slot is available but wait
            // no longer than the given deadline
    
            if (!done) return null;

            // Signal or broadcast that an full slot is available (if needed)

            value = super.get();

            // Leave mutual exclusion 
        return value;
    }

    // Insert an element into buffer. If the attempted operation is not
    // possible immedidately, the method call blocks until it is, but
    // waits no longer than the given deadline. Return 0 if not
    // successful. Otherwise, return 1.
    boolean offer(Object value, long deadline) {
        boolean done = false;
        long    timeout;
        boolean interrupted = true;
        long    now;

        // Enter mutual exclusion

            // Wait until a empty slot is available but wait
            // no longer than the given deadline
    
            if (!done) return false;

            // Signal or broadcast that an empty slot is available (if needed)

            super.put(value);

            // Leave mutual exclusion and enforce synchronisation semantics
            // using semaphores.
        return true;
    }
}
