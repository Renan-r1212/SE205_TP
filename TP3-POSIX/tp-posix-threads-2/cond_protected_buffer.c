#include <errno.h>
#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include "circular_buffer.h"
#include "protected_buffer.h"
#include "utils.h"

// Initialise the protected buffer structure above. 
protected_buffer_t * cond_protected_buffer_init(int length) {
  protected_buffer_t * b;
  b = (protected_buffer_t *)malloc(sizeof(protected_buffer_t));
  b->buffer = circular_buffer_init(length);
  // Initialize the synchronization components

  // pthread_mutex_init returns 0 if the orperation went well
  b->mutex = (pthread_mutex_t *)malloc(sizeof(pthread_mutex_t));
  if(pthread_mutex_init(b->mutex,NULL) != 0){
    pthread_mutex_destroy(b->mutex);
    perror("pthread_cond_init:");
    exit(1);
  }
 
  b->conditionEmpty = (pthread_cond_t *)malloc(sizeof(pthread_cond_t));
  if(pthread_cond_init(b->conditionEmpty, NULL)){
    pthread_cond_destroy(b->conditionEmpty); 
    perror("pthread_cond_init:");
    exit(1);    
  }

    b->conditionFull = (pthread_cond_t *)malloc(sizeof(pthread_cond_t));
    if(pthread_cond_init(b->conditionFull, NULL)){
      pthread_cond_destroy(b->conditionFull);   
      perror("pthread_cond_init:");
      exit(1);    
  }
  return b;
}

// Extract an element from buffer. If the attempted operation is
// not possible immedidately, the method call blocks until it is.
void * cond_protected_buffer_get(protected_buffer_t * b){
  void * d;
  
  // Enter mutual exclusion
  
  pthread_mutex_lock(b->mutex); 
  
  // Wait until there is a full slot to get data from the unprotected
  // circular buffer (circular_buffer_get).


//  d = circular_buffer_get(b->buffer);
  while(NULL == (d = circular_buffer_get(b->buffer))){
    pthread_cond_wait(b->conditionEmpty, b->mutex);

  }
  // Signal or broadcast that an empty slot is available in the
  // unprotected circular buffer (if needed)
  if(b->buffer->size == 0){
      pthread_cond_broadcast(b->conditionFull);
  }

  print_task_activity ("get", d);

  // Leave mutual exclusion
  pthread_mutex_unlock(b->mutex); 
  
  return d;
}

// Insert an element into buffer. If the attempted operation is
// not possible immedidately, the method call blocks until it is.
void cond_protected_buffer_put(protected_buffer_t * b, void * d){

  // Enter mutual exclusion
  pthread_mutex_lock(b->mutex); 
  
  // Wait until there is an empty slot to put data in the unprotected
  // circular buffer (circular_buffer_put).
  
  while(!circular_buffer_put(b->buffer,d)){
    pthread_cond_wait(b->conditionFull, b->mutex);
  }
  
  // Signal or broadcast that a full slot is available in the
  // unprotected circular buffer (if needed)
  if (b->buffer->size == b->buffer->max_size) {
    pthread_cond_broadcast(b->conditionEmpty);
  }
  print_task_activity ("put", d);

  // Leave mutual exclusion
  pthread_mutex_unlock(b->mutex); 
}

// Extract an element from buffer. If the attempted operation is not
// possible immedidately, return NULL. Otherwise, return the element.
void * cond_protected_buffer_remove(protected_buffer_t * b){
  void * d;

  if(!pthread_mutex_trylock(b->mutex)){
    d = circular_buffer_get(b->buffer);
  }
  else{
    d = NULL;
  }
  print_task_activity ("remove", d);

  // Signal or broadcast that an empty slot is available in the
  // unprotected circular buffer (if needed)

  if (b->buffer->size == 0) {
    pthread_cond_broadcast(b->conditionFull);
  }

  pthread_mutex_unlock(b->mutex); 
  
  return d;
}

// Insert an element into buffer. If the attempted operation is
// not possible immedidately, return 0. Otherwise, return 1.
int cond_protected_buffer_add(protected_buffer_t * b, void * d){
  int done;
  
  // Enter mutual exclusion
 
  if(!pthread_mutex_trylock(b->mutex)){
    done = circular_buffer_put(b->buffer, d);
    if (!done) d = NULL;
  }else{
    done = 0;
    d = NULL;
  }
  
  // Signal or broadcast that a full slot is available in the
  // unprotected circular buffer (if needed)
  if (b->buffer->size == b->buffer->max_size) {
    pthread_cond_broadcast(b->conditionEmpty);
  }

  print_task_activity ("add", d);

  // Leave mutual exclusion
  pthread_mutex_unlock(b->mutex); 

  return done;
}

// Extract an element from buffer. If the attempted operation is not
// possible immedidately, the method call blocks until it is, but
// waits no longer than the given timeout. Return the element if
// successful. Otherwise, return NULL.
void * cond_protected_buffer_poll(protected_buffer_t * b, struct timespec *abstime){
  void * d = NULL;
  int    rc = 0;
  
  // Enter mutual exclusio
  if(pthread_mutex_timedlock(b->mutex, abstime)){
    print_task_activity ("poll", NULL);
    return NULL;
  } 
  // Wait until there is an empty slot to put data in the unprotected
  // circular buffer (circular_buffer_put) but waits no longer than
  // the given timeout.
  while(b->buffer->size == 0){
    rc = pthread_cond_timedwait(b->conditionFull,b->mutex,abstime);
    if(rc == ETIMEDOUT){
      print_task_activity ("poll", NULL);
      pthread_mutex_unlock(b->mutex); 
      return NULL;
    }
  }
  // Signal or broadcast that a full slot is available in the
  // unprotected circular buffer (if needed)
  if(rc != ETIMEDOUT){
    if(b->buffer->size == b->buffer->max_size){
      pthread_cond_broadcast(b->conditionEmpty);
    }
    d = circular_buffer_get(b->buffer);
  }

  print_task_activity ("poll", d);

  // Leave mutual exclusion
  pthread_mutex_unlock(b->mutex); 

  return d;
}

// Insert an element into buffer. If the attempted operation is not
// possible immedidately, the method call blocks until it is, but
// waits no longer than the given timeout. Return 0 if not
// successful. Otherwise, return 1.
int cond_protected_buffer_offer(protected_buffer_t * b, void * d, struct timespec * abstime){
  int rc = 0;
  int done = 0;
  
  // Enter mutual exclusion
  if(pthread_mutex_timedlock(b->mutex, abstime)){
    print_task_activity ("offer", NULL);
    return 0;
  } 
  
  // Signal or broadcast that a full slot is available in the
  // unprotected circular buffer (if needed) but waits no longer than
  // the given timeout.
  while(b->buffer->size == b->buffer->max_size){
    rc = pthread_cond_timedwait(b->conditionEmpty,b->mutex,abstime);
    if(rc == ETIMEDOUT){
      print_task_activity ("offer", NULL);
      pthread_mutex_unlock(b->mutex); 
      return 0;
    }
  }
 
  // Signal or broadcast that a full slot is available in the
  // unprotected circular buffer (if needed)

  if (rc != ETIMEDOUT){
    if(b->buffer->size == 0){
      pthread_cond_broadcast(b->conditionFull);
    }
    done = circular_buffer_put(b->buffer, d);
    if (!done) d = NULL; 
  }
    
  print_task_activity ("offer", d);
    
  // Leave mutual exclusion
  pthread_mutex_unlock(b->mutex); 
  return done;
}
