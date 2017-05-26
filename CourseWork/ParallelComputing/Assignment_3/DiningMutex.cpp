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

/* Global mutexes used by all the threads */
pthread_mutex_t ME;
pthread_mutex_t MUTEXES[NUM_DINERS];

/* The information passed to each thread */
struct job_data {
    int diner_index;
    bool * running;
};

/*
 * Makes the calling thread wait a given amount of seconds
 */
void sleepThread(int seconds) {
    struct timespec tm = {seconds,0};
    nanosleep(&tm, NULL);
}//sleepThread

/*
 * Tries to pick up the chopsticks the philosopher needs and will wait until
 * the chopsticks are available
 */
void take_chopsticks(int i) {

    int left = i;
    int right = (i+1) % NUM_DINERS;
    printf("Diner %d is HUNGRY\n",i);
    printf("Diner %d is waiting for CHOPSTICKS %d, %d\n",left,left, right);

    // Using the Mutual Exclusion Mutex results in a deadlock
    // pthread_mutex_lock(&ME);
    pthread_mutex_lock(&MUTEXES[left]);
    pthread_mutex_lock(&MUTEXES[right]);

    printf("Diner %d is using CHOPSTICKS %d, %d\n",left,left, right);

    // Using the Mutual Exclusion Mutex results in a deadlock
    // pthread_mutex_unlock(&ME);
}//take_chopsticks(int)

/*
 * Releases the chopsticks used by the philosopher after eating
 */
void drop_chopsticks(int i) {
    int left = i;
    int right = (i+1) % NUM_DINERS;

    // Using the Mutual Exclusion Mutex results in a deadlock
    // pthread_mutex_lock(&ME);

    pthread_mutex_unlock(&MUTEXES[left]);
    pthread_mutex_unlock(&MUTEXES[right]);

    // Using the Mutual Exclusion Mutex results in a deadlock
    // pthread_mutex_unlock(&ME);
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
        // sleepThread(0); //THINKING Stage

        /* Picking up CHOPSTICKS routine */
        take_chopsticks(data.diner_index);

        /* EATING routine*/
        printf("Diner %d is EATING\n",data.diner_index);
        sleepThread(EATING_TIME);

        /* Dropping CHOPSTICKS routine */
        drop_chopsticks(data.diner_index);

        printf("Diner %d finished EATING\n",data.diner_index);
        printf("Diner %d put down CHOPSTICKS %d, %d\n",left,left, right);
    }
    return NULL;
}//philosopher(void*)

/*
 * Sets the default states of all the mutexes
 */
void setup_mutexes() {
    for(int i=0; i<NUM_DINERS; i++) {
        pthread_mutex_init(&MUTEXES[i],NULL);
    }
    pthread_mutex_init(&ME,NULL);
}//setup()

/*
 * Destroys any memory allocated to the mutexes
 */
void cleanup_mutexes() {
    for(int i=0; i<NUM_DINERS; i++) {
        pthread_mutex_destroy(&MUTEXES[i]);
    }
    pthread_mutex_destroy(&ME);
}//cleanup()

int main(int argc, char* argv[]) {
    srand(0);

    pthread_t threads[NUM_DINERS];
    job_data thread_args[NUM_DINERS];

    setup_mutexes();

    int retval;
    bool run = true;

    //Creates the threads
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

    cleanup_mutexes();
    return 0;
}//(int, char*)
