#include <pthread.h>
#include <iostream>
#include <assert.h>
#include <stdlib.h>
#include <time.h>
#include <iostream>

using namespace std;

#define THINKING 0
#define HUNGRY 1
#define EATING 2

#define THINK_MAX 10
#define NUM_DINERS 5
#define EATING_TIME 5

/* The information passed to each thread */
struct job_data {
    int diner_index;
    bool * running;
};

/* A struct representing a binary semaphore or mutex. */
struct mutex_struct {
    bool value;
};

/* Global variables used by all the threads */
mutex_struct ME;
mutex_struct MUTEXES[NUM_DINERS];
int FLAGS[NUM_DINERS];

/*
 * Makes the calling thread wait a given amount of seconds
 */
void sleepThread(int seconds) {
    struct timespec tm = {seconds,0};
    nanosleep(&tm, NULL);
}//sleepThread

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
 * Tests to see if any of the adjacent philosophers are eating and allows the
 * given philosopher to eat once it has finished waiting for a chopstick
 */
void test(int i) {
    if(FLAGS[i] == HUNGRY
        && FLAGS[(i-1) % NUM_DINERS] != EATING
        && FLAGS[(i+1) % NUM_DINERS] != EATING) {

        FLAGS[i] = EATING;
        up(&MUTEXES[i]);
    }
}//test(int)

/*
 * Tries to pick up the chopsticks the philosopher needs and will wait until
 * the chopsticks are available
 */
void take_chopsticks(int i) {
    down(&ME);
    FLAGS[i] = HUNGRY;
    test(i);
    printf("Diner %d is using CHOPSTICKS %d, %d\n",i,i,(i+1) % NUM_DINERS);
    up(&ME);
    down(&MUTEXES[i]);
}//take_chopsticks(int)

/*
 * Releases the chopsticks used by the philosopher after eating
 */
void drop_chopsticks(int i) {
    down(&ME);
    test((i-1) % NUM_DINERS);
    test((i+1) % NUM_DINERS);
    up(&ME);
}//drop_chopsticks(int)

/*
 * The job that each philosopher thread runs.
 * Will run infinitely until user enters false on main thread.
 */
void * philosopher(void *arg) {

    job_data &data = *((job_data*)arg);

    while (*data.running) {

        int left = data.diner_index;
        int right = (data.diner_index + 1) % NUM_DINERS;

        /* THINKING routine */
        printf("Diner %d Started THINKING\n",data.diner_index);
        sleepThread(rand() % THINK_MAX); //THINKING Stage

        /* Picking up CHOPSTICKS routine */
        printf("Diner %d is HUNGRY\n",data.diner_index);
        take_chopsticks(data.diner_index);

        /* EATING routine*/
        printf("Diner %d is EATING\n",data.diner_index);
        sleepThread(EATING_TIME);

        /* Dropping CHOPSTICKS routine */
        drop_chopsticks(data.diner_index);
        printf("Diner %d finished EATING\n",data.diner_index);
        printf("Diner %d put down CHOPSTICKS %d, %d\n",left,left, right);

        FLAGS[data.diner_index] = THINKING;
    }//while
    return NULL;
}//philosopher(void*)

/*
 * Sets the default states of all the mutexes
 */
void setup_mutexes() {
    for(int i=0; i<NUM_DINERS; i++) {
        MUTEXES[i].value = false;
    }
    ME.value = true;
}//setup()

int main(int argc, char* argv[]) {
    srand(0);

    pthread_t threads[NUM_DINERS];
    job_data thread_args[NUM_DINERS];

    setup_mutexes();

    int retval;
    bool run = true;

    /* Creates the threads */
    for(unsigned i=0; i<NUM_DINERS; i++) {
        thread_args[i].diner_index = i;
        thread_args[i].running = &run;
        retval = pthread_create(&threads[i], NULL, philosopher, &thread_args[i]);
    }//for i

    /* Blocks the main thread until user enters false */
    while(run) {
        cin >> run;
    }//while(run)

    void *status;
    for(int i=0; i<NUM_DINERS; i++) {
        retval = pthread_join(threads[i], &status);
        if(!retval) {
            printf("Completed thread: %d\n",i);
        }
    }//for i

    return 0;
}//(int, char*)
