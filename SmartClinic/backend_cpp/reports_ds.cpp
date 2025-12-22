#include "reports_ds.h"
#include "json_util.h"
#include <algorithm> // For std::sort
#include <fstream>   // For std::ofstream

// Comparison function for sorting EmergencyPatient objects by severity
bool compareEmergencies(const EmergencyPatient& a, const EmergencyPatient& b) {
    return a.severity > b.severity;
}

ReportManager::ReportManager(const std::string& patientDbPath, const std::string& emergencyDbPath) {
    totalPatients = 0;
    normalPatients = 0;
    emergencyPatients = 0;
    loadPatientData(patientDbPath);
    loadEmergencyData(emergencyDbPath);
    sortEmergencies();
}

void ReportManager::loadPatientData(const std::string& dbPath) {
    std::string json = sj::readAllText(dbPath);
    if (json.empty()) return;

    // A simple way to count patients is to count occurrences of a unique key like "id"
    int count = 0;
    size_t pos = json.find("\"id\"");
    while (pos != std::string::npos) {
        count++;
        pos = json.find("\"id\"", pos + 1);
    }
    totalPatients = count;
}

void ReportManager::loadEmergencyData(const std::string& dbPath) {
    std::string json = sj::readAllText(dbPath);
    if (json.empty()) return;

    size_t start = json.find('[');
    size_t end = json.find(']');
    if (start == std::string::npos || end == std::string::npos) return;

    std::string content = json.substr(start + 1, end - start - 1);
    
    size_t current = 0;
    while ((current = content.find('{', current)) != std::string::npos) {
        size_t objEnd = content.find('}', current);
        std::string patJson = content.substr(current, objEnd - current + 1);
        
        EmergencyPatient p;
        p.name = sj::getString(patJson, "name");
        p.severity = sj::getInt(patJson, "severity", 0);
        
        topEmergencies.push_back(p);
        
        current = objEnd;
    }
    emergencyPatients = topEmergencies.size();
    normalPatients = totalPatients > emergencyPatients ? totalPatients - emergencyPatients : 0;
}

void ReportManager::sortEmergencies() {
    std::sort(topEmergencies.begin(), topEmergencies.end(), compareEmergencies);
}

void ReportManager::generateReport(const std::string& outputPath) {
    std::ofstream out(outputPath.c_str());

    out << "{\n";
    out << "  \"totalPatients\": " << totalPatients << ",\n";
    out << "  \"normalPatients\": " << normalPatients << ",\n";
    out << "  \"emergencyPatients\": " << emergencyPatients << ",\n";
    out << "  \"topEmergencies\": [\n";

    for (size_t i = 0; i < topEmergencies.size() && i < 5; ++i) {
        out << "    { \"name\": \"" << sj::esc(topEmergencies[i].name)
            << "\", \"severity\": " << topEmergencies[i].severity << " }";
        if (i < topEmergencies.size() - 1 && i < 4) {
            out << ",";
        }
        out << "\n";
    }

    out << "  ]\n";
    out << "}";
    out.close();
}
