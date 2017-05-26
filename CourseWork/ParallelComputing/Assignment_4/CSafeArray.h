#include <pthread.h>
#include <iostream>
#include <stdlib.h>
#include <vector>
#include <assert.h>

using namespace std;

class CSafeArray {
private:
    static pthread_mutex_t ME;
    vector<int> array;


public:
    CSafeArray(int size);
    void lock();
    void unlock();
    static void setup();
    bool set(int index, int item);
    int get(int index);
    void push_back(int item);
    int pop_back();
    int size();
    void del(int index);
};
