# __Assignment 3 Description__

## Part 1

Write a C/C++ program that tries to solve the Dining Philosophers Problem using mutexes alone:

1. Display a message every time an event happens (trace the program’s progress)
1. Discuss the problems of this approach (deadlock/starvation etc.) and support your
arguments with trace examples of your code (save it as a pdf and submit with your
code)

## Part 2
Write a C/C++ program that tries to solve the problem using any synchronization mechanism
you choose.

---

# __Assignment 3 Discussion__

## Problems with this approach

### Deadlock issue

There is a possibility for a deadlock condition with only
using mutexes instead of another synchronization method.
This can happen if all the philosophers try and pick up the chopstick to their left at the same time.
This results in all chopsticks being used, and the philosophers are stuck waiting for the chopstick to their right to be available, but they never will be released.

However, this is very unlikely to happen as it requires the CPU to be able to run 5 hardware threads and for the threads to be running their procedures exactly in sync.

Tannenbaum's solution handles this by using an additional mutex for mutual exclusion. This results only one philosopher being able to try pick up a chopstick at a time.

I had two implementations using mutexes, one using the <code>phtread_mutex_t</code> mutex and the other using global booleans to represent mutexes.

For some reason in the version using phtread mutexes, if I tried to have the mutual exclusion mutex, my program would result in a deadlock. This is shown in the code below.

```cpp
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
```
However the code using booleans wrapped in structs to directly implement Tannenbaum's solution has no issue with the mutual exclusion.

### Starvation issue

The starvation issue refers to the possibility of a philosopher not ever being allowed to eat. This can occur when a philosopher thinks for 0 time units and is therefore constantly alternating between being hungry and eating. It is possible that this philosopher is constantly picking up and putting down the same chopsticks, preventing either philosopher next to them from using them.

In practice this doesn't appear to be an issue because hungry philosophers are waiting on the mutexes representing chopsticks to be unlocked, whereas a philosopher that just finished eating has a couple procedures to execute before trying to pick up chopsticks, even if they think for 0 time units. This is highlighted by the code shown below:

```cpp
while (*data.running) {

    int left = data.diner_index;
    int right = (data.diner_index + 1) % NUM_DINERS;
    &nbsp;
    /*THINKING routine*/
    printf("Diner %d Started THINKING\n",data.diner_index);
    sleepThread(rand() % THINK_MAX); //THINKING Stage

    /*Picking up CHOPSTICKS routine*/
    take_chopsticks(data.diner_index);

    /*EATING routine*/
    printf("Diner %d is EATING\n",data.diner_index);
    sleepThread(EATING_TIME);

    /*Dropping CHOPSTICKS routine*/
    drop_chopsticks(data.diner_index);

    printf("Diner %d finished EATING\n",data.diner_index);
    printf("Diner %d is put down CHOPSTICKS %d, %d\n",left,left, right);
}
```
## Semaphore Approach

As mentioned at the end of the __The Dining Philosophers__ problem description, Dijkstra stated that the problem could be solved by using an integer semaphore instead of a binary semaphore, and set the value of the semaphore to be _N-1_ where _N_ is the number of philosophers. Therefore there will always be enough chopsticks for at least 1 of the philosophers.

Building on this proposed solution, I made the assumption that since only _N-1_ philosophers were to be allowed to the table at a time, their placement wouldn't matter and the only thing that would need to be controlled would be number of philosophers at the table, not which chopsticks they were using. This approach made the code simpler and also prevented the _starvation_ issue.
