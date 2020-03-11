import java.util.concurrent.Semaphore;
import java.lang.InterruptedException;
import java.util.concurrent.TimeUnit;

class SemBoundedBuffer extends BoundedBuffer {
    Semaphore emptySlots, fullSlots, mutexSem;

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
        try{
        emptySlots.acquire();
        }catch(Exception e){}

        synchronized (this){
            value = super.get();
        }
        // Leave mutual exclusion and enforce synchronisation semantics
        // using semaphores.

        fullSlots.release();

        return value;
    }

    // Insert an element into buffer. If the attempted operation is
    // not possible immedidately, the method call blocks until it is.
    boolean put(Object value) {
        boolean done = false;

        // Enter mutual exclusion and enforce synchronisation semantics
        // using semaphores.
        try{
        fullSlots.acquire();
        }catch(Exception e){}

        synchronized (this){
            done = super.put(value);
        }
        // Leave mutual exclusion and enforce synchronisation semantics
        // using semaphores.
        emptySlots.release();
        return done;
    }

    // Extract an element from buffer. If the attempted operation is not
    // possible immedidately, return NULL. Otherwise, return the element.
    Object remove() {
        boolean done;
        Object value;
        
        // Enter mutual exclusion and enforce synchronisation semantics
        // using semaphores.
        try{
            if(!emptySlots.tryAcquire()){;    
                return null;
            }
        }catch(Exception e){}        

        synchronized (this){
            if(size == maxSize)
                value = super.get();
            else
                value = null;            
        }
        // Leave mutual exclusion and enforce synchronisation semantics
        // using semaphores.
        fullSlots.release();
        
        return value;
    }

    // Insert an element into buffer. If the attempted operation is
    // not possible immedidately, return 0. Otherwise, return 1.
    boolean add(Object value) {
        boolean done = false;

        // Enter mutual exclusion and enforce synchronisation semantics
        // using semaphores.
        try{
            if(!fullSlots.tryAcquire()){
                return done;
            }
        }catch(Exception e){}
        
        synchronized (this){
            if(size == 0)
                done = super.put(value);
        }
        // Leave mutual exclusion and enforce synchronisation semantics
        // using semaphores.
        emptySlots.release();

        return done;
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

        timeout = deadline - System.currentTimeMillis();
        // Enter mutual exclusion and enforce synchronisation semantics
        // using semaphores.

        try{
            if(!emptySlots.tryAcquire(timeout, TimeUnit.MILLISECONDS)){
                return null;
            }

        }catch(Exception e){}

        synchronized (this){
            if(size == maxSize)
                value = super.get();
            else
                value = null;
        }
        // Leave mutual exclusion and enforce synchronisation semantics
        // using semaphores.
        fullSlots.release();

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

        timeout = deadline - System.currentTimeMillis();

        // Enter mutual exclusion and enforce synchronisation semantics
        // using semaphores.


        try{
            if(!fullSlots.tryAcquire(timeout, TimeUnit.MILLISECONDS)){
                return done;
            }
        }catch(Exception e){}

        synchronized (this){
            if(size == 0)
                done = super.put(value);
        }

        emptySlots.release();
        
        return done;
    }
}
