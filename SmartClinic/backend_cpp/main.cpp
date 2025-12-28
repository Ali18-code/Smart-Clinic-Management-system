#include "json_util.h"
#include "patient_ds.h"
#include "emergency_ds.h"
#include "storage.h"
#include "reports_ds.h"
#include "appointment_ds.h"

#include <iostream>
#include <cstring>

using namespace std;

/* =========================
   COMMON OUTPUT HELPERS
   ========================= */
static string okOut(const string& msg, const string& extraJsonFields = "") {
    string j = "{\"ok\":true,\"message\":\"" + sj::esc(msg) + "\"";
    if (!extraJsonFields.empty()) j += "," + extraJsonFields;
    j += "}";
    return j;
}

static string errOut(const string& msg) {
    return "{\"ok\":false,\"message\":\"" + sj::esc(msg) + "\"}";
}

/* =========================
   MAIN
   ========================= */
int main(int argc, char** argv) {

    if (argc < 2) {
        sj::writeAllText("../data/patient_output.json", errOut("Missing mode"));
        sj::writeAllText("../data/emergency_output.json", errOut("Missing mode"));
        sj::writeAllText("../data/reports_output.json", errOut("Missing mode"));
        sj::writeAllText("../data/appointment_output.json", errOut("Missing mode"));
        return 0;
    }

    string mode = argv[1];
    string patientDbPath   = "../data/patients_db.json";
    string emergencyDbPath = "../data/emergency_db.json";

    /* =========================
       MODULE 1 — PATIENT
       ========================= */
    if (mode == "patient") {

        string in = sj::readAllText("../data/patient_input.json");
        if (in.empty()) {
            sj::writeAllText("../data/patient_output.json",
                             errOut("patient_input.json not found or empty"));
            return 0;
        }

        PatientAVL avl;
        storage::loadPatients(patientDbPath, avl);

        string action = sj::getString(in, "action");

        if (action == "ADD") {
            Patient p;
            p.id      = sj::getInt(in, "id", 0);
            p.name    = sj::getString(in, "name");
            p.age     = sj::getInt(in, "age", 0);
            p.disease = sj::getString(in, "disease");
            p.date    = sj::getString(in, "date");

            if (p.id <= 0 || p.name.empty() || p.age <= 0 ||
                p.disease.empty() || p.date.empty()) {
                sj::writeAllText("../data/patient_output.json",
                                 errOut("Invalid or empty patient fields"));
                return 0;
            }

            if (!avl.insert(p)) {
                sj::writeAllText("../data/patient_output.json",
                                 errOut("Duplicate Patient ID"));
                return 0;
            }

            storage::savePatients(patientDbPath, avl);
            sj::writeAllText("../data/patient_output.json",
                okOut("Patient added", "\"patients\":" + avl.toJsonArray()));
            return 0;
        }

        if (action == "DELETE") {
            int id = sj::getInt(in, "id", 0);
            if (id <= 0 || !avl.remove(id)) {
                sj::writeAllText("../data/patient_output.json",
                                 errOut("Patient not found"));
                return 0;
            }

            storage::savePatients(patientDbPath, avl);
            sj::writeAllText("../data/patient_output.json",
                okOut("Patient deleted", "\"patients\":" + avl.toJsonArray()));
            return 0;
        }

        if (action == "SEARCH") {
            int id = sj::getInt(in, "id", 0);
            Patient* p = avl.search(id);

            if (!p) {
                sj::writeAllText("../data/patient_output.json",
                                 errOut("Patient not found"));
                return 0;
            }

            string one =
                "[{\"id\":" + to_string(p->id) +
                ",\"name\":\"" + sj::esc(p->name) +
                "\",\"age\":" + to_string(p->age) +
                ",\"disease\":\"" + sj::esc(p->disease) +
                "\",\"date\":\"" + sj::esc(p->date) + "\"}]";

            sj::writeAllText("../data/patient_output.json",
                okOut("Patient found", "\"patients\":" + one));
            return 0;
        }

        if (action == "VIEW_ALL") {
            sj::writeAllText("../data/patient_output.json",
                okOut("All patients", "\"patients\":" + avl.toJsonArray()));
            return 0;
        }

        sj::writeAllText("../data/patient_output.json",
                         errOut("Unknown patient action"));
        return 0;
    }

    /* =========================
       MODULE 2 — EMERGENCY
       ========================= */
    if (mode == "emergency") {

        string in = sj::readAllText("../data/emergency_input.json");
        if (in.empty()) {
            sj::writeAllText("../data/emergency_output.json",
                             errOut("emergency_input.json not found or empty"));
            return 0;
        }

        MaxHeap heap(500);
        storage::loadEmergencyHeap(emergencyDbPath, heap);

        UndoStack undo(500);
        ClinicGraph graph;

        string action = sj::getString(in, "action");

        if (action == "ADD_EMERGENCY") {
            EmergencyPatient p;
            p.name     = sj::getString(in, "name");
            p.severity = sj::getInt(in, "severity", 0);

            if (p.name.empty() || p.severity < 1 || p.severity > 10) {
                sj::writeAllText("../data/emergency_output.json",
                                 errOut("Invalid name or severity (1–10)"));
                return 0;
            }

            heap.insert(p);
            undo.push("ADD:" + p.name);
            storage::saveEmergencyHeap(emergencyDbPath, heap);

            sj::writeAllText("../data/emergency_output.json",
                             okOut("Emergency added"));
            return 0;
        }

        if (action == "PROCESS_NEXT") {
            EmergencyPatient out;
            if (!heap.extractMax(out)) {
                sj::writeAllText("../data/emergency_output.json",
                                 errOut("No emergency patients"));
                return 0;
            }

            undo.push("PROCESS:" + out.name);
            storage::saveEmergencyHeap(emergencyDbPath, heap);

            sj::writeAllText("../data/emergency_output.json",
                okOut("Processed emergency", "\"name\":\"" + sj::esc(out.name) + "\""));
            return 0;
        }

        if (action == "ROUTE") {
            string from = sj::getString(in, "from");
            string to   = sj::getString(in, "to");

            string path;
            if (!graph.shortestPathBFS(from, to, path)) {
                sj::writeAllText("../data/emergency_output.json",
                                 errOut("Route not found"));
                return 0;
            }

            sj::writeAllText("../data/emergency_output.json",
                okOut("Route found", "\"route\":\"" + sj::esc(path) + "\""));
            return 0;
        }

        sj::writeAllText("../data/emergency_output.json",
                         errOut("Unknown emergency action"));
        return 0;
    }

    /* =========================
       MODULE 4 — APPOINTMENT
       ========================= */
    if (mode == "appointment") {

        string appointmentDbPath = "../data/appointments_db.json";
        string in = sj::readAllText("../data/appointment_input.json");
        if (in.empty()) {
            sj::writeAllText("../data/appointment_output.json",
                             errOut("appointment_input.json not found or empty"));
            return 0;
        }

        AppointmentQueue aq;
        storage::loadAppointments(appointmentDbPath, aq);

        string action = sj::getString(in, "action");

        if (action == "ADD") {
            Appointment a;
            a.patientId = sj::getInt(in, "id", 0);
            string patientName = sj::getString(in, "name");
            string doctor = sj::getString(in, "doctor");
            string time = sj::getString(in, "time");

            strcpy_s(a.patientName, sizeof(a.patientName), patientName.c_str());
            strcpy_s(a.doctor, sizeof(a.doctor), doctor.c_str());
            strcpy_s(a.time, sizeof(a.time), time.c_str());

            if (!aq.enqueue(a)) {
                sj::writeAllText("../data/appointment_output.json",
                    errOut("Appointment queue is full"));
                return 0;
            }

            storage::saveAppointments(appointmentDbPath, aq);

            char buffer[4096] = {0};
            aq.toJson(buffer);

            sj::writeAllText("../data/appointment_output.json",
                okOut("Appointment added", "\"appointments\":" + string(buffer)));
            return 0;
        }

        if (action == "SERVE_NEXT") {

            MaxHeap eHeap(500);
            storage::loadEmergencyHeap(emergencyDbPath, eHeap);

            EmergencyPatient dummy;
            if (eHeap.peek(dummy)) {
                sj::writeAllText("../data/appointment_output.json",
                    errOut("Emergency cases pending"));
                return 0;
            }

            Appointment out;
            if (!aq.dequeue(out)) {
                sj::writeAllText("../data/appointment_output.json",
                    errOut("No appointments"));
                return 0;
            }

            storage::saveAppointments(appointmentDbPath, aq);

            char buffer[4096] = {0};
            aq.toJson(buffer);

            sj::writeAllText("../data/appointment_output.json",
                okOut("Appointment served",
                      "\"served\":\"" + string(out.patientName) +
                      "\",\"appointments\":" + string(buffer)));
            return 0;
        }

        if (action == "VIEW_ALL") {
            char buffer[4096] = {0};
            aq.toJson(buffer);
            sj::writeAllText("../data/appointment_output.json",
                okOut("All appointments", "\"appointments\":" + string(buffer)));
            return 0;
        }

        sj::writeAllText("../data/appointment_output.json",
                         errOut("Unknown appointment action"));
        return 0;
    }

    /* =========================
       MODULE 3 — REPORTS
       ========================= */
    if (mode == "reports") {
        string reportOutputPath = "../data/reports_output.json";
        ReportManager manager(patientDbPath, emergencyDbPath);
        manager.generateReport(reportOutputPath);
        return 0;
    }

    sj::writeAllText("../data/patient_output.json", errOut("Unknown mode"));
    sj::writeAllText("../data/emergency_output.json", errOut("Unknown mode"));
    sj::writeAllText("../data/reports_output.json", errOut("Unknown mode"));
    sj::writeAllText("../data/appointment_output.json", errOut("Unknown mode"));
    return 0;
}
