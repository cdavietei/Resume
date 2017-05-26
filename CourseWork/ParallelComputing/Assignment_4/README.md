# __Assignment 4 Description__

## Summary
In this assignment we will practice creating a thread safe data structure i.e. a data structure that can be accessed by multiple threads in parallel without corrupting the data inside the data structure

## Implementation Specification
1. Design and Implement a C++ thread safe data structure:
 1. Create a Thread safe array/vector data structure. Use a C++ class (e.g. class CSafeArray)
 1. Protect all read/write operations in CSafeArray with a single Pthread Mutex
2. Test/Validate CSafeArray by writing and executing the following program:
 2. Initialize the CSafeArray with sequential data (1 to N => the array sum = N*(N+1)/2)
 2. Create multiple threads(M) and do the following in each thread:
    2. Search for the smallest number in CSafeArray
    2. Remove the smallest number from CSafeArray
    2. Sum the removed numbers in each thread and return the thread sum to main
 2. In main do the following:
    2. Display each thread’s sum
    2. Calculate the total sum of all threads
    2. Display CSafeArray sum
    2. Compare the thread total sum to CSafeArray sum (if you have no bugs then the total sum should be equal to the sum of all data retrieved by the threads)
3. Test/Validate CSafeArray by writing and executing the following program:
 3. Write a writer thread that writes data to the CSafeArray
 3. Write a reader thread that reads data from the CSafeArray
 3. Create multiple combinations of readers and writers
 3. Run and verify that CSafeArray is empty by the end of the program and no deadlocks or starvation occurred (think how to do that for your specific test code)



---

# __Assignment 4 Discussion__
## CSafeArray Implementation

Implementing the CSafeArray was difficult because I was having issues remembering how to make a class in C++. Additionally I was having an issue with the static <code>pthread_mutex_t</code> as referencing it in my lock and unlock function giving me the following error:

```undefined symbols for architecture x86_64```

But after declaring the variable in the C++ file, the error went away.

## Test 1 Implementation

Once the CSafeArray was implemented, creating the tests was pretty easy but I kept running into a race condition when getting the minimum of the array and subsequently removing it. The issue was that while the individual read/write operations were thread safe, the threads themselves would be reading and writing in between each other. This resulted in the array being changed as one thread goes through the operation shown below:

```cpp
void * remove_and_sum(void * args) {

    job_data &data = *((job_data*)args);

    CSafeArray &array = *data.array;

    int min = array.get(0);
    int minIndex = 0;
    for(int i=1; i<array.size(); i++) { //Size is constantly changing
        int val = array.get(i);
        if(val < min) {
            min = val;
            minIndex = i;
        }
    }

    array.del(minIndex); //This min may have been deleted by another thread

    data.retval = min;

    return NULL;
}//remove_and_sum(void*)
```

The solution I chose for this problem was to have the lock and unlock functions be public and have the threads handle the locking and unlocking themselves. In hindsight a better solution would have been to have a mutex for the CSafeArray internally, and then a separate mutex for the threads. My solution is shown below:

```cpp
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
```

After this fix, the program ran as expected.

## Test 2 Implementation

I was a bit confused about what exactly we had to for the second test, so I interpreted it as we needed to have multiple threads reading and writing to the same CSafeArray.

I had ___n___ reader threads and ___n___ writer threads using the same CSafeArray, and the reader threads would store the value at the index corresponding to their ID.

The writer threads would set the value at the index corresponding to their ID to be twice the value of their ID.

The result was that since I started each reader thread before its corresponding writer thread, the readers weren't affected by the writers.

In the future I would make the number of readers and writers random and start them randomly to see if they can deadlock the other.
