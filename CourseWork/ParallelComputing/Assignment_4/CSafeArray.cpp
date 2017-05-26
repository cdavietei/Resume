#include "CSafeArray.h"

pthread_mutex_t CSafeArray::ME;

void CSafeArray::setup() {
    pthread_mutex_init(&ME,NULL);
};

void CSafeArray::lock() {
    pthread_mutex_lock(&ME);
};

void CSafeArray::unlock(){
    pthread_mutex_unlock(&ME);
};

CSafeArray::CSafeArray(int size){
    array.reserve(size);
};

bool CSafeArray::set(int index, int item) {
    // lock();
    assert(index < array.size());
    array[index] = item;
    // unlock();

    return true;
};

int CSafeArray::get(int index) {
    int retval = 0;

    // lock();
    assert(index < array.size());
    retval = array[index];
    // unlock();

    return retval;
}

void CSafeArray::push_back(int item) {

    // lock();
    array.push_back(item);
    // unlock();
};

int CSafeArray::pop_back() {
    int retval = 0;

    // lock();
    retval = array.back();
    array.pop_back();
    // unlock();

    return retval;
}

int CSafeArray::size() {

    int retval = -1;

    // lock();
    retval = array.size();
    // unlock();

    return retval;
}

void CSafeArray::del(int index) {

    // lock();
    assert(index < array.size());
    array.erase(array.begin()+index);
    // unlock();
}
