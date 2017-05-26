#include <pthread.h>
#include <iostream>
#include <assert.h>
#include <stdlib.h>

using namespace std;

int COUNTER = 0;

void *job(void *tArg) {

    int val = *( ( int* )tArg );

    COUNTER = COUNTER + 1 ;
    cout << "Thread: " << val << endl;
    COUNTER = COUNTER * 2;
    return NULL;
}

int main(int argc, char *argv[]) {

    long num_threads = atoi(argv[1]);
    cout << num_threads << endl;

    pthread_t threads[num_threads];
    int thread_args[num_threads];

    int retval;
    void *status;

    //cout << "Before for loop"<<endl;
    printf("Before for loop\n");

    for(long i=0; i<num_threads; i++) {
        //cout << "Creating thread " << i << endl;
        printf("Creating thread %lu\n",i);
        thread_args[i] = i;
        retval = pthread_create(&threads[i], NULL, job, &thread_args[i]);
    }

    // cout << "After for loop"<<endl;
    printf("After for loop\n");

    for(int i=0; i<num_threads; i++) {
        retval = pthread_join(threads[i], &status);
        if(!retval) {
            //cout << "Completed Thread : " << i << endl;
            printf("Completed thread: %d\n",i);
        }
    }

    // cout << "Counter: " << COUNTER << endl;
    printf("Counter: %d\n", COUNTER);
}
