#include "appointment_ds.h"
#include <cstring>
#include <cstdio>

using namespace std;

AppointmentQueue::AppointmentQueue() {
    front = 0;
    rear = -1;
    size = 0;
}

bool AppointmentQueue::enqueue(Appointment a) {
    if (size >= 100) return false;
    rear = (rear + 1) % 100;
    arr[rear] = a;
    size++;
    return true;
}

bool AppointmentQueue::dequeue(Appointment &out) {
    if (size == 0) return false;
    out = arr[front];
    front = (front + 1) % 100;
    size--;
    return true;
}

bool AppointmentQueue::isEmpty() {
    return size == 0;
}

int AppointmentQueue::count() {
    return size;
}

void AppointmentQueue::toJson(char* buffer) {
    strcpy(buffer, "[");
    for (int i = 0; i < size; ++i) {
        char temp[256];
        int currentIndex = (front + i) % 100;
        sprintf(
            temp,
            "{\"patientId\":%d,\"patientName\":\"%s\",\"doctor\":\"%s\",\"time\":\"%s\"}",
            arr[currentIndex].patientId,
            arr[currentIndex].patientName,
            arr[currentIndex].doctor,
            arr[currentIndex].time
        );
        strcat(buffer, temp);
        if (i < size - 1) {
            strcat(buffer, ",");
        }
    }
    strcat(buffer, "]");
}
