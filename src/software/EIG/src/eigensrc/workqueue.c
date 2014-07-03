#include <stdio.h>
#include <stdlib.h>
#include "workqueue.h"

void create_work_queue(work_queue **the_queue) {
  work_queue *queue = (work_queue*)malloc( sizeof(work_queue) );
  
  queue->tasks = NULL;
  queue->num_tasks = 0;

  *the_queue = queue;
} 

void destroy_work_queue(work_queue **the_queue) {
  work_queue *queue = *the_queue;

  free(queue->tasks);
  free(queue);

  the_queue = NULL;
}

void queue_task(work_queue *queue, const work_task* task) {
  // Copy the task into the queue
  queue->tasks = realloc(queue->tasks, sizeof(work_task) * ++queue->num_tasks);
  queue->tasks[queue->num_tasks-1] = *task;

  // Get a pointer to the official task in the queue
  work_task* the_task = &queue->tasks[queue->num_tasks-1];

#ifdef HAVE_PTHREAD
  // Create a new thread for the task.
  if(pthread_create(&the_task->thread,NULL,the_task->start_routine,the_task->argument)) {
    printf("System error creating new thread.\n");
    abort();
  }
#else
  the_task->start_routine(the_task->argument);
#endif
}

void wait_for_queue_to_complete(const work_queue *queue) {
#ifdef HAVE_PTHREAD
  int i;
  for(i = 0; i < queue->num_tasks; i++) {
    if(pthread_join(queue->tasks[i].thread,NULL)) {
      printf("System error waiting for thread to finish.\n");
      abort();
    }
  }
#endif
}
