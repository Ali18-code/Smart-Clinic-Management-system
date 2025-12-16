#include "storage.h"
#include "json_util.h"

// helper to extract JSON objects one by one
static bool parseNextObject(const std::string& j, int& i, std::string& objOut) {
    while (i < (int)j.size() && j[i] != '{') i++;
    if (i >= (int)j.size()) return false;

    int start = i;
    int depth = 0;

    while (i < (int)j.size()) {
        if (j[i] == '{') depth++;
        else if (j[i] == '}') {
            depth--;
            if (depth == 0) {
                objOut = j.substr(start, i - start + 1);
                i++;
                return true;
            }
        }
        i++;
    }
    return false;
}

namespace storage {

bool loadPatients(const std::string& dbPath, PatientAVL& avl) {
    std::string json = sj::readAllText(dbPath);
    if (json.empty()) return true;

    int i = 0;
    std::string obj;
    while (parseNextObject(json, i, obj)) {
        Patient p;
        p.id = sj::getInt(obj, "id", 0);
        p.name = sj::getString(obj, "name");
        p.age = sj::getInt(obj, "age", 0);
        p.disease = sj::getString(obj, "disease");
        p.date = sj::getString(obj, "date");

        if (p.id != 0)
            avl.insert(p);
    }
    return true;
}

bool savePatients(const std::string& dbPath, PatientAVL& avl) {
    return sj::writeAllText(dbPath, avl.toJsonArray());
}

bool loadEmergencyHeap(const std::string& dbPath, MaxHeap& heap) {
    std::string json = sj::readAllText(dbPath);
    if (json.empty()) return true;

    int i = 0;
    std::string obj;
    while (parseNextObject(json, i, obj)) {
        EmergencyPatient p;
        p.name = sj::getString(obj, "name");
        p.severity = sj::getInt(obj, "severity", 1);

        if (!p.name.empty())
            heap.insert(p);
    }
    return true;
}

bool saveEmergencyHeap(const std::string& dbPath, MaxHeap& heap) {
    EmergencyPatient temp[500];
    int n = 0;
    EmergencyPatient e;

    while (heap.extractMax(e)) {
        temp[n++] = e;
    }

    std::string out = "[";
    for (int i = 0; i < n; i++) {
        out += "{\"name\":\"" + sj::esc(temp[i].name) +
               "\",\"severity\":" + std::to_string(temp[i].severity) + "}";
        if (i != n - 1) out += ",";
    }
    out += "]";

    for (int i = 0; i < n; i++)
        heap.insert(temp[i]);

    return sj::writeAllText(dbPath, out);
}

}