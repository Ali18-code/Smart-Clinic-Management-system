#ifndef APPOINTMENT_DS_H
#define APPOINTMENT_DS_H

using namespace std;

struct Appointment {
    int patientId;
    char patientName[50];
    char doctor[50];
    char time[20];
};

class AppointmentQueue {
    Appointment arr[100];
    int front, rear, size;

public:
    AppointmentQueue();
    bool enqueue(Appointment a);
    bool dequeue(Appointment &out);
    bool isEmpty();
    int count();
    void toJson(char* buffer);
};

#endif
