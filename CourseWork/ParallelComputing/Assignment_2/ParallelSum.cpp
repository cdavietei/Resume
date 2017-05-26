#include <pthread.h>
#include <iostream>
#include <assert.h>
#include <stdlib.h>

using namespace std;

/*
 * A struct that contains the arguments passed to the thread as well
 * as the return value of the thread
 */
struct job_data {
    int startIndex;
    int endIndex;
    int *array;
    int retval;
};

/*
 * Performs the partial sum on an array passed in the job_data struct
 */
void * perform_partial_sum(void *arg) {
    job_data *data = ((job_data*)arg);

    int *array = data->array;
    int partial_sum = 0;

    for(int i=data->startIndex; i<data->endIndex; i++) {
        partial_sum += array[i];
    }

    data->retval = partial_sum;

     return NULL;
}//perform_partial_sum(void*)

/*
 * Populates the array with numbers 0 to len-1
 */
void fill_array(int len, int* array) {

    for(int i=0; i<len; i++) {
        array[i] = i;
    }
}//fill_array(int, int*)

/*
 * Prints out the contents of the array
 */
void print_array(int len, int* array) {
    for(int i=0; i<len; i++) {
        printf("%d ",array[i]);
    }
    printf("\n");
}//print_array(int, int*)

/*
 * Creates the most equal partitions possible for each thread to use as its
 * operating range
 */
void create_partitions(job_data args[], int len, int num) {

    //The size of the partitions
    int part_size = len / num;
    //The number of elements excluded from the equal partitions
    int left_overs = len % num;
    //Used to keep track of the current partition position
    int current = 0;

    for(int i=0; i<num; i++) {
        args[i].startIndex = current;
        current += part_size;
        if(left_overs) {
            current++;
            left_overs--;
        }
        args[i].endIndex = current;
    }//for i
}//create_partitions(job_data, int, int)

int main(int argc, char *argv[]) {
    //Expects two commandline arguments for array length and number of threads
    assert(argc >= 3);

    unsigned array_len = atoi(argv[1]);
    unsigned num_threads = atoi(argv[2]);

    assert(array_len >= num_threads);

    printf("Array Length: %d Num Threads: %d\n",array_len, num_threads);

    int* array = new int[array_len];

    fill_array(array_len, array);
    print_array(array_len, array);

    pthread_t threads[num_threads];
    job_data thread_args[num_threads];

    create_partitions(thread_args, array_len, num_threads);

    int retval;

    //Creates the threads
    for(unsigned i=0; i<num_threads; i++) {
        thread_args[i].array = array;
        retval = pthread_create(&threads[i], NULL, perform_partial_sum, &thread_args[i]);
    }//for i

    void *status;
    int sum = 0;
    for(int i=0; i<num_threads; i++) {
        retval = pthread_join(threads[i], &status);
        if(!retval) {
            printf("Completed thread: %d\n",i);
            sum += thread_args[i].retval;
        }
    }//for i
    printf("Sum: %d\n",sum);

    delete [] array;

    return 0;
}//main(int, char*)
