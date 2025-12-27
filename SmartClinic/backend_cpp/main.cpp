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
