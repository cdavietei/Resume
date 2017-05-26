#include <pthread.h>
#include <iostream>
#include <assert.h>
#include <stdlib.h>
#include <time.h>
#include <iostream>

using namespace std;

#define THINK_MAX 10
#define NUM_DINERS 5
#define EATING_TIME 5

struct job_data {
    int diner_index;
};

struct semaphore_struct {
    int value;
};

/* A struct representing a binary semaphore or mutex. */
struct mutex_struct {
    bool value;
};

mutex_struct ME;
semaphore_struct SEATED;

/*
 * Makes the calling thread wait a given amount of seconds
 */
void sleepThread(int seconds) {
    struct timespec tm = {seconds,0};
    nanosleep(&tm, NULL);
}//sleepThread

/*
 * Increments the given semaphore
 */
void up(semaphore_struct * s) {
    s->value = s->value + 1;
}//up(semaphore_struct)

/*
 * Tries to decrement the given semaphore or waits if it is currently at 0
 */
void down(semaphore_struct * s) {
    while(!s->value);

    s->value = s->value - 1;
}//down(semaphore_struct)

/*
 * Unlocks the given mutex
 */
void up(mutex_struct * m) {
    m->value = true;
}//up(mutex_struct)

/*
 * Tries to lock a given mutex but waits if it is currently locked by another
 * thread
 */
void down(mutex_struct * m) {
    while(!m->value);

    m->value = false;
}//up(mutex_struct)

/*
 * A philosopher tries to access the table if a spot is available or waits until
 * a spot is available.
 */
void take_chopsticks(int i) {
    printf("Diner %d is HUNGRY\n",i);

    down(&ME);

    down(&SEATED);

    up(&ME);
    printf("Diner %d picked up chopsticks\n",i);
    printf("Take chopsticks :: ME val: %d\n",(&ME)->value);
}//take_chopsticks(int)

/*
 * A philosopher will release their spot at the table once they're finished
 * eating.
 */
void drop_chopsticks(int i) {

    printf("Diner %d dropping chopsticks\n",i);
    printf("Drop chopsticks :: ME val: %d\n",(&ME)->value);
    down(&ME);

    printf("Diner %d claimed ME\n",i);

    up(&SEATED);

    printf("Diner %d released seat\n",i);

    up(&ME);
    printf("Diner %d dropped chopsticks\n",i);
}//drop_chopsticks(int)

/*
 * The job that each philosopher thread runs.
 * Will run infinitely.
 */
void * philosopher(void *arg) {

    job_data &data = *((job_data*)arg);

    while (true) {

        int left = data.diner_index;
        int right = (data.diner_index + 1) % NUM_DINERS;

        /* THINKING routine */
        printf("Diner %d Started THINKING\n",data.diner_index);
        sleepThread(rand() % THINK_MAX); //THINKING Stage

        /* Picking up CHOPSTICKS routine */
        take_chopsticks(data.diner_index);

        /* EATING routine*/
        printf("Diner %d is EATING\n",data.diner_index);
        // printf("Diner %d is using CHOPSTICKS %d, %d\n",left,left, right);
        sleepThread(EATING_TIME);

        /* Dropping CHOPSTICKS routine */
        drop_chopsticks(data.diner_index);

        printf("Diner %d finished EATING\n",data.diner_index);
        // printf("Diner %d is put down CHOPSTICKS %d, %d\n",left,left, right);
    }
    return NULL;
}//philosopher(void*)

/*
 * Sets the default states of the mutex and semaphore
 */
void setup() {
    ME.value = 1;
    SEATED.value = NUM_DINERS - 1;
}//setup()

int main(int argc, char* argv[]) {
    srand(0);

    pthread_t threads[NUM_DINERS];
    job_data thread_args[NUM_DINERS];

    setup();

    int retval;

    //Creates the threads
    for(unsigned i=0; i<NUM_DINERS; i++) {
        thread_args[i].diner_index = i;
        retval = pthread_create(&threads[i], NULL, philosopher, &thread_args[i]);
    }//for i

    /* Used for debugging purposes*/
    bool run = true;

    while(run) {
        cin >>run;
        printf("Semaphore Value: %d\n",(&SEATED)->value);
    }

    void *status;
    for(int i=0; i<NUM_DINERS; i++) {
        retval = pthread_join(threads[i], &status);
        if(!retval) {
            printf("Completed thread: %d\n",i);
        }
    }//for i

    return 0;
}//(int, char*)
