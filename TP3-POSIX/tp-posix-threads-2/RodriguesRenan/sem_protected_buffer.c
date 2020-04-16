#include <errno.h>
#include <fcntl.h>
#include <pthread.h>
#include <semaphore.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/time.h>
#include "circular_buffer.h"
#include "protected_buffer.h"
#include "utils.h"

#define EMPTY_SLOTS_NAME "/empty_slots"
#define FULL_SLOTS_NAME "/full_slots"

// Initialise the protected buffer structure above. 
protected_buffer_t * sem_protected_buffer_init(int length) {
  protected_buffer_t * b;
  b = (protected_buffer_t *)malloc(sizeof(protected_buffer_t));
  b->buffer = circular_buffer_init(length);
  // Initialize the synchronization attributes
  // Use these filenames as named semaphores

  // Mutex
  b->semMutex = (pthread_mutex_t *)malloc(sizeof(pthread_mutex_t));
  if(pthread_mutex_init(b->semMutex,NULL) != 0){
    pthread_mutex_destroy(b->semMutex);
    perror("pthread_cond_init:");
    exit(1);
  }

  // Semaphores
  b->fullSlots = (sem_t *)malloc(sizeof(sem_t));
  b->fullSlots = sem_open(FULL_SLOTS_NAME, O_CREAT, 0644,0);
  if (b->fullSlots == SEM_FAILED) {
    sem_close(b->fullSlots);
    perror("Failed to open semphore for empty");
    exit(-1);
  }

  b->emptySlots = (sem_t *)malloc(sizeof(sem_t));
  b->emptySlots = sem_open(EMPTY_SLOTS_NAME, O_CREAT, 0644, b->buffer->max_size);
  if (b->emptySlots == SEM_FAILED) {
    sem_close(b->emptySlots);
    perror("Failed to open semphore for empty");
    exit(-1);
  }

  sem_unlink (EMPTY_SLOTS_NAME);
  sem_unlink (FULL_SLOTS_NAME);
  return b;
}

// Extract an element from buffer. If the attempted operation is
// not possible immedidately, the method call blocks until it is.
void * sem_protected_buffer_get(protected_buffer_t * b){
  void * d;
  
  // Enforce synchronisation semantics using semaphores.
  sem_wait(b->fullSlots);

  // Enter mutual exclusion.
  pthread_mutex_lock(b->semMutex); 

  d = circular_buffer_get(b->buffer);
  
  print_task_activity ("get", d);

  // Leave mutual exclusion.

  pthread_mutex_unlock(b->semMutex); 

  // Enforce synchronisation semantics using semaphores.
  if(b->buffer->size == 0){
    sem_post(b->emptySlots);
  }
  
  return d;
}

// Insert an element into buffer. If the attempted operation is
// not possible immedidately, the method call blocks until it is.
void sem_protected_buffer_put(protected_buffer_t * b, void * d){

  // Enforce synchronisation semantics using semaphores.
  sem_wait(b->emptySlots);
  // Enter mutual exclusion.
  pthread_mutex_lock(b->semMutex); 
  
  circular_buffer_put(b->buffer, d);
  print_task_activity ("put", d);

  // Leave mutual exclusion.
  pthread_mutex_unlock(b->semMutex); 

  // Enforce synchronisation semantics using semaphores.
  if(b->buffer->size == b->buffer->max_size){
    sem_post(b->fullSlots);
  }
}

// Extract an element from buffer. If the attempted operation is not
// possible immedidately, return NULL. Otherwise, return the element.
void * sem_protected_buffer_remove(protected_buffer_t * b){
  void * d = NULL;
  int    rc = -1;
  
  // Enforce synchronisation semantics using semaphores.
  rc = sem_trywait(b->fullSlots);

  if (rc != 0) {
    print_task_activity ("remove", d);
    return d;
  }

  // Enter mutual exclusion.
  pthread_mutex_lock(b->semMutex); 
  
  d = circular_buffer_get(b->buffer);
  print_task_activity ("remove", d);

  // Leave mutual exclusion.
  pthread_mutex_unlock(b->semMutex); 

  // Enforce synchronisation semantics using semaphores.
  if (b->buffer->size == 0) {
    sem_post(b->emptySlots);
  }
  
  return d;
}

// Insert an element into buffer. If the attempted operation is
// not possible immedidately, return 0. Otherwise, return 1.
int sem_protected_buffer_add(protected_buffer_t * b, void * d){
  int rc = -1;
  
  // Enforce synchronisation semantics using semaphores.
  rc = sem_trywait(b->emptySlots);
  if (rc != 0) {
    print_task_activity ("add", NULL);
    return 0;
  }

  // Enter mutual exclusion.
  pthread_mutex_lock(b->semMutex);
  
  circular_buffer_put(b->buffer, d);
  print_task_activity ("add", d);
  
  // Leave mutual exclusion.
  pthread_mutex_unlock(b->semMutex); 

  // Enforce synchronisation semantics using semaphores.
  if(b->buffer->size == b->buffer->max_size){
    sem_post(b->fullSlots);
  }

  return 1;
}

// Extract an element from buffer. If the attempted operation is not
// possible immedidately, the method call blocks until it is, but
// waits no longer than the given timeout. Return the element if
// successful. Otherwise, return NULL.
void * sem_protected_buffer_poll(protected_buffer_t * b, struct timespec *abstime){
  void * d = NULL;
  int    rc = -1;
  
  // Enforce synchronisation semantics using semaphores.

  rc = sem_timedwait(b->fullSlots, abstime);
  if (rc != 0) {
    print_task_activity ("poll", d);
    return d;
  }

  // Enter mutual exclusion. 
  pthread_mutex_lock(b->semMutex);
  
  d = circular_buffer_get(b->buffer);
  print_task_activity ("poll", d);

  // Leave mutual exclusion.
  pthread_mutex_unlock(b->semMutex);

  // Enforce synchronisation semantics using semaphores.
  if (b->buffer->size == 0) {
    sem_post(b->emptySlots);
  }
  return d;
}

// Insert an element into buffer. If the attempted operation is not
// possible immedidately, the method call blocks until it is, but
// waits no longer than the given timeout. Return 0 if not
// successful. Otherwise, return 1.
int sem_protected_buffer_offer(protected_buffer_t * b, void * d, struct timespec * abstime){
  int rc = -1;
  
  // Enforce synchronisation semantics using semaphores.
  rc = sem_timedwait(b->emptySlots, abstime);
  if (rc != 0) {
    d = NULL;
    print_task_activity ("offer", d);
    return 0;
  }

  // Enter mutual exclusion.
  pthread_mutex_lock(b->semMutex);
  
  circular_buffer_put(b->buffer, d);
  print_task_activity ("offer", d);

  // Leave mutual exclusion.
  pthread_mutex_unlock(b->semMutex);

  // Enforce synchronisation semantics using semaphores.
  if(b->buffer->size == b->buffer->max_size){
    sem_post(b->fullSlots);
  }
  return 1;
}

