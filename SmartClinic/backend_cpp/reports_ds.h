#ifndef REPORTS_DS_H
#define REPORTS_DS_H

#include "emergency_ds.h"
#include <vector>
#include <string>

class ReportManager {
private:
    int totalPatients;
    int normalPatients;
    int emergencyPatients;
    std::vector<EmergencyPatient> topEmergencies;

    void loadPatientData(const std::string& dbPath);
    void loadEmergencyData(const std::string& dbPath);
    void sortEmergencies();

public:
    ReportManager(const std::string& patientDbPath, const std::string& emergencyDbPath);
    void generateReport(const std::string& outputPath);
};

#endif
