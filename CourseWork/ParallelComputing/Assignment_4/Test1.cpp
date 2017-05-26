#include <pthread.h>
#include <iostream>
#include <stdlib.h>
#include <vector>
#include <assert.h>
#include "CSafeArray.h"

struct job_data {
    int thread_id;
    CSafeArray * array;
    int retval;
};

void setup(CSafeArray *array, int n) {

    CSafeArray::setup();

    for(int i=0; i<n; i++) {
        array->push_back(i+1);
    }
}

void printArray(CSafeArray * array) {
    int size = array->size();
    for(int i=0; i<size; i++) {
        printf("%d ",array->get(i));
    }
    printf("\n");
}

void * remove_and_sum(void * args) {

    job_data &data = *((job_data*)args);

    CSafeArray &array = *data.array;

    array.lock();
    int min = array.get(0);
    int minIndex = 0;
    for(int i=1; i<array.size(); i++) {
        int val = array.get(i);
        if(val < min) {
            min = val;
            minIndex = i;
        }
    }

    array.del(minIndex);
    array.unlock();

    data.retval = min;

    return NULL;
}//remove_and_sum(void*)

int main(int argc, char *argv[]) {
    assert(argc >= 2);
    int num_threads = atoi(argv[1]);

    CSafeArray value(num_threads);
    CSafeArray * array = &value;

    setup(array,num_threads);
    printArray(array);

    pthread_t threads[num_threads];
    job_data thread_args[num_threads];

    int retval;

    //Creates the threads
    for(unsigned i=0; i<num_threads; i++) {
        thread_args[i].thread_id = i;
        thread_args[i].array = array;
        retval = pthread_create(&threads[i], NULL, remove_and_sum, &thread_args[i]);
    }//for i

    void *status;
    int sum =0;
    for(int i=0; i<num_threads; i++) {
        retval = pthread_join(threads[i], &status);
        if(!retval) {
            printf("Completed thread: %d\n",i);
            printf("Thread %d's min: %d\n",i,thread_args[i].retval);
            sum +=thread_args[i].retval;
        }
    }//for i

    int c_sum = 0;

    for(int i=0; i<n; i++) {
        c_sum+=i+1;
    }

    printf("Total Threads sum: %d\n", sum);
    printf("Total CSafeArray sum: %d\n", c_sum);

    return 0;
}//main()
