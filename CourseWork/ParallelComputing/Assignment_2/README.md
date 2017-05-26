# __Assignment 2 Description__
---
## Part 1

Write a C/C++ program that performs a parallel math operation on all elements in a given array:


1. Array size and number of threads should be supplied by the user as arguments to the
program
2. Try to balance the work by sending an equal portion of the array to each thread
3. Do not use global variables to communicate with the threads. Use the thread function
argument to send information. Hint: If you have more than one data item to send, use
custom Structs or Classes to send information to the thread.
4. Each thread will perform a math operation on each element in the array in the portion of the array that was provided to it.

Each thread should do something along the lines of:
```cpp
// loop on thread supplied data range
 for(int i=startIndex;i<endIndex;i++)
 array[i] =array[i]*2 + 10; // perform some operation on each element
```
## Part 2

Write a C/C++ program that performs a parallel sum on all elements in a given array:

1. Array size and number of threads should be supplied by the user as arguments to the
program
1. Try to balance the work by sending an equal portion of the array to each thread
1. Do not use global variables to communicate with the threads. Use the thread function
argument to send/receive information. Hint: If you have more than one data item to
send, use custom Structs or Classes to send/receive information to/from the thread.
1. Each thread will sum each element in the array in the portion of the array that was
provided to him and return the partial sum to the main thread (c)
1. The main thread should sum the partial sums and display the total sum of the array

Each thread should do something along the lines of:
```cpp
// loop on thread supplied data range
 for(int i=startIndex;i<endIndex;i++)
 partialSum += =array[i]; //sum all the elements in the sub array
```

---
# Assignment 2 Discussion
