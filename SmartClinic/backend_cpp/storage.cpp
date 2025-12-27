#include "storage.h"
#include "json_util.h"

#include <vector>
#include <cstdio>   // for snprintf

namespace storage {

    // ================= PATIENTS =================

    bool loadPatients(const std::string& dbPath, PatientAVL& avl) {

        // Read file into a string
        std::string json = sj::readAllText(dbPath);

        if (json.empty()) {
            return false;   // file missing or empty
        }

        size_t pos = 0;

        // Keep finding { ... } blocks
        while (true) {

            pos = json.find('{', pos);

            if (pos == std::string::npos) {
                break;  // no more objects
            }

            size_t end = json.find('}', pos);

            if (end == std::string::npos) {
                break;  // malformed JSON
            }

            std::string block = json.substr(pos, end - pos + 1);

            Patient p;
            p.id = sj::getInt(block, "id", 0);
            p.name = sj::getString(block, "name");
            p.age = sj::getInt(block, "age", 0);
            p.disease = sj::getString(block, "disease");
            p.date = sj::getString(block, "date");

            if (p.id > 0) {
                avl.insert(p);
            }

            pos = end + 1;
        }

        return true;
    }

    bool savePatients(const std::string& dbPath, PatientAVL& avl) {

        std::string json = avl.toJsonArray();

        return sj::writeAllText(dbPath, json);
    }


    // ================= EMERGENCY HEAP =================

    bool loadEmergencyHeap(const std::string& dbPath, MaxHeap& heap) {

        std::string json = sj::readAllText(dbPath);

        if (json.empty()) {
            return false;
        }

        size_t pos = 0;

        while (true) {

            pos = json.find('{', pos);

            if (pos == std::string::npos) {
                break;
            }

            size_t end = json.find('}', pos);

            if (end == std::string::npos) {
                break;
            }

            std::string block = json.substr(pos, end - pos + 1);

            EmergencyPatient ep;
            ep.name = sj::getString(block, "name");
            ep.severity = sj::getInt(block, "severity", 0);

            if (!ep.name.empty()) {
                heap.insert(ep);
            }

            pos = end + 1;
        }

        return true;
    }

    bool saveEmergencyHeap(const std::string& dbPath, MaxHeap& heap) {

        std::vector<EmergencyPatient> backup;

        EmergencyPatient ep;

        // Take everything out of heap
        while (heap.extractMax(ep)) {
            backup.push_back(ep);
        }

        // Put back into heap
        for (size_t i = 0; i < backup.size(); i++) {
            heap.insert(backup[i]);
        }

        // Build JSON text
        std::string json = "[";

        for (size_t i = 0; i < backup.size(); i++) {

            json += "{";
            json += "\"name\":\"" + sj::esc(backup[i].name) + "\",";
            json += "\"severity\":" + std::to_string(backup[i].severity);
            json += "}";

            if (i < backup.size() - 1) {
                json += ",";
            }
        }

        json += "]";

        return sj::writeAllText(dbPath, json);
    }


    // ================= APPOINTMENTS =================

    bool loadAppointments(const std::string& dbPath, AppointmentQueue& queue) {

        std::string json = sj::readAllText(dbPath);

        if (json.empty()) {
            return false;
        }

        size_t pos = 0;

        while (true) {

            pos = json.find('{', pos);

            if (pos == std::string::npos) {
                break;
            }

            size_t end = json.find('}', pos);

            if (end == std::string::npos) {
                break;
            }

            std::string block = json.substr(pos, end - pos + 1);

            Appointment a;

            a.patientId = sj::getInt(block, "patientId", 0);

            std::string pname = sj::getString(block, "patientName");
            std::string doc   = sj::getString(block, "doctor");
            std::string time  = sj::getString(block, "time");

            snprintf(a.patientName, sizeof(a.patientName), "%s", pname.c_str());
            snprintf(a.doctor,      sizeof(a.doctor),      "%s", doc.c_str());
            snprintf(a.time,        sizeof(a.time),        "%s", time.c_str());

            queue.enqueue(a);

            pos = end + 1;
        }

        return true;
    }

    bool saveAppointments(const std::string& dbPath, AppointmentQueue& queue) {

        char temp[16384];

        queue.toJson(temp);

        std::string json = temp;

        return sj::writeAllText(dbPath, json);
    }

}
