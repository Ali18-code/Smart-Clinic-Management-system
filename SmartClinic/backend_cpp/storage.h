#pragma once
#include "patient_ds.h"
#include "emergency_ds.h"
#include "appointment_ds.h"
#include <string>

namespace storage {
    bool loadPatients(const std::string& dbPath, PatientAVL& avl);
    bool savePatients(const std::string& dbPath, PatientAVL& avl);

    bool loadEmergencyHeap(const std::string& dbPath, MaxHeap& heap);
    bool saveEmergencyHeap(const std::string& dbPath, MaxHeap& heap);

    bool loadAppointments(const std::string& dbPath, AppointmentQueue& queue);
    bool saveAppointments(const std::string& dbPath, AppointmentQueue& queue);
}
