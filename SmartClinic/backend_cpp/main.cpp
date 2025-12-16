#include "json_util.h"
#include "patient_ds.h"
#include "emergency_ds.h"
#include "storage.h"
#include <iostream>

static std::string okOut(const std::string& msg, const std::string& extraJsonFields) {
    std::string j = "{\"ok\":true,\"message\":\"" + sj::esc(msg) + "\"";
    if(!extraJsonFields.empty()) j += "," + extraJsonFields;
    j += "}";
    return j;
}
static std::string errOut(const std::string& msg) {
    return "{\"ok\":false,\"message\":\"" + sj::esc(msg) + "\"}";
}

int main(int argc, char** argv) {
    // usage:
    // backend.exe patient
    // backend.exe emergency
    if(argc < 2){
        sj::writeAllText("../data/patient_output.json", errOut("Missing mode"));
        sj::writeAllText("../data/emergency_output.json", errOut("Missing mode"));
        return 0;
    }

    std::string mode = argv[1];

    if(mode == "patient"){
        std::string in = sj::readAllText("../data/patient_input.json");
        if(in.empty()){
            sj::writeAllText("../data/patient_output.json", errOut("patient_input.json not found/empty"));
            return 0;
        }

        PatientAVL avl;
        storage::loadPatients("../data/patients_db.json", avl);

        std::string action = sj::getString(in, "action");

        if(action == "ADD"){
            Patient p;
            p.id = sj::getInt(in, "id", 0);
            p.name = sj::getString(in, "name");
            p.age = sj::getInt(in, "age", 0);
            p.disease = sj::getString(in, "disease");
            p.date = sj::getString(in, "date");

            if(p.id <= 0 || p.name.empty() || p.age <= 0 || p.disease.empty() || p.date.empty()){
                sj::writeAllText("../data/patient_output.json", errOut("Invalid/empty patient fields"));
                return 0;
            }

            bool ok = avl.insert(p);
            if(!ok){
                sj::writeAllText("../data/patient_output.json", errOut("Duplicate Patient ID"));
                return 0;
            }

            storage::savePatients("../data/patients_db.json", avl);
            std::string patients = avl.toJsonArray();
            sj::writeAllText("../data/patient_output.json",
                okOut("Patient added", "\"count\":" + std::to_string(0) + ",\"patients\":" + patients)
            );
            return 0;
        }
        else if(action == "DELETE"){
            int id = sj::getInt(in, "id", 0);
            if(id <= 0){
                sj::writeAllText("../data/patient_output.json", errOut("Invalid ID"));
                return 0;
            }
            bool ok = avl.remove(id);
            if(!ok){
                sj::writeAllText("../data/patient_output.json", errOut("Patient not found"));
                return 0;
            }
            storage::savePatients("../data/patients_db.json", avl);
            sj::writeAllText("../data/patient_output.json", okOut("Patient deleted", "\"patients\":" + avl.toJsonArray()));
            return 0;
        }
        else if(action == "SEARCH"){
            int id = sj::getInt(in, "id", 0);
            Patient* p = avl.search(id);
            if(!p){
                sj::writeAllText("../data/patient_output.json", errOut("Patient not found"));
                return 0;
            }
            std::string one = "[{\"id\":" + std::to_string(p->id) +
                              ",\"name\":\"" + sj::esc(p->name) +
                              "\",\"age\":" + std::to_string(p->age) +
                              ",\"disease\":\"" + sj::esc(p->disease) +
                              "\",\"date\":\"" + sj::esc(p->date) + "\"}]";
            sj::writeAllText("../data/patient_output.json", okOut("Patient found", "\"patients\":" + one));
            return 0;
        }
        else if(action == "VIEW_ALL"){
            sj::writeAllText("../data/patient_output.json", okOut("All patients", "\"patients\":" + avl.toJsonArray()));
            return 0;
        }

        sj::writeAllText("../data/patient_output.json", errOut("Unknown action"));
        return 0;
    }

    if(mode == "emergency"){
        std::string in = sj::readAllText("../data/emergency_input.json");
        if(in.empty()){
            sj::writeAllText("../data/emergency_output.json", errOut("emergency_input.json not found/empty"));
            return 0;
        }

        MaxHeap heap(500);
        storage::loadEmergencyHeap("../data/emergency_db.json", heap);

        NormalQueue normalQ(500);
        UndoStack undo(500);
        ClinicGraph graph;

        std::string action = sj::getString(in, "action");

        if(action == "ADD_EMERGENCY"){
            EmergencyPatient p;
            p.name = sj::getString(in, "name");
            p.severity = sj::getInt(in, "severity", 0);

            if(p.name.empty() || p.severity < 1 || p.severity > 10){
                sj::writeAllText("../data/emergency_output.json", errOut("Invalid name/severity (1-10)"));
                return 0;
            }

            heap.insert(p);
            undo.push("ADD_EMERGENCY:" + p.name);

            storage::saveEmergencyHeap("../data/emergency_db.json", heap);

            EmergencyPatient top;
            std::string topStr = "";
            if(heap.peek(top)) topStr = top.name + "(" + std::to_string(top.severity) + ")";

            sj::writeAllText("../data/emergency_output.json",
                okOut("Emergency added", "\"heapTop\":\"" + sj::esc(topStr) + "\"")
            );
            return 0;
        }
        else if(action == "PROCESS_NEXT"){
            EmergencyPatient out;
            if(!heap.extractMax(out)){
                sj::writeAllText("../data/emergency_output.json", errOut("No emergency patients"));
                return 0;
            }
            undo.push("PROCESS:" + out.name);
            storage::saveEmergencyHeap("../data/emergency_db.json", heap);
            sj::writeAllText("../data/emergency_output.json",
                okOut("Processed: " + out.name, "\"processed\":\"" + sj::esc(out.name) + "\"")
            );
            return 0;
        }
        else if(action == "ROUTE"){
            std::string from = sj::getString(in, "from");
            std::string to = sj::getString(in, "to");
            if(from.empty() || to.empty()){
                sj::writeAllText("../data/emergency_output.json", errOut("Invalid room names"));
                return 0;
            }
            std::string path;
            if(!graph.shortestPathBFS(from, to, path)){
                sj::writeAllText("../data/emergency_output.json", errOut("Route not found (check room spellings)"));
                return 0;
            }
            sj::writeAllText("../data/emergency_output.json",
                okOut("Route found", "\"route\":\"" + sj::esc(path) + "\"")
            );
            return 0;
        }

        sj::writeAllText("../data/emergency_output.json", errOut("Unknown action"));
        return 0;
    }

    sj::writeAllText("../data/patient_output.json", errOut("Unknown mode"));
    sj::writeAllText("../data/emergency_output.json", errOut("Unknown mode"));
    return 0;
}
