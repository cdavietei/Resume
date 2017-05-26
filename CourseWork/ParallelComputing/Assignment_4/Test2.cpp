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

void * reader(void * args) {
    job_data &data = *((job_data*)args);

    CSafeArray &array = *data.array;

    array.lock();

    data.retval = array.get(data.thread_id);

    array.unlock();

    return NULL;
}//remove_and_sum(void*)

void * writer(void * args) {
    job_data &data = *((job_data*)args);

    CSafeArray &array = *data.array;

    array.lock();

    array.set(data.thread_id,data.thread_id * 2);

    array.unlock();


    return NULL;
}

int main(int argc, char *argv[]) {
    assert(argc >= 2);
    int num_threads = atoi(argv[1]);

    CSafeArray value(num_threads);
    CSafeArray * array = &value;

    setup(array,num_threads);
    printArray(array);

    pthread_t reader_threads[num_threads];
    pthread_t writer_threads[num_threads];
    job_data reader_args[num_threads];
    job_data writer_args[num_threads];

    int retval;

    //Creates the threads
    for(unsigned i=0; i<num_threads; i++) {
        reader_args[i].thread_id = i;
        reader_args[i].array = array;

        writer_args[i].thread_id = i;
        writer_args[i].array = array;

        retval = pthread_create(&reader_threads[i], NULL, reader, &reader_args[i]);
        retval = pthread_create(&writer_threads[i], NULL, writer, &writer_args[i]);
    }//for i

    void *status;
    for(int i=0; i<num_threads; i++) {
        retval = pthread_join(writer_threads[i], &status);

        if(!retval) {
            printf("Completed reader thread: %d\n",i);
            printf("Reader Thread %d's val: %d\n",i,reader_args[i].retval);
        }
        retval = pthread_join(reader_threads[i], &status);
        if(!retval) {
            printf("Completed writer thread: %d\n",i);
        }

    }//for i

    printf("Final Array\n");
    printArray(array);

    return 0;
}//main()
