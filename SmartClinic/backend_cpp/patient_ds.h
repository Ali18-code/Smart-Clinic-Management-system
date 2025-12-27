#pragma once
#include <string>
#include <vector>
using namespace std;

struct Patient {
    int id;
    string name;
    int age;
    string disease;
    string date;
};

// ----------------- Linked List using vector -----------------
class PatientList {
private:
    vector<Patient> patients;

public:
    PatientList() {}
    ~PatientList() { patients.clear(); }

    bool insert(const Patient& p) {
        for (auto& patient : patients) {
            if (patient.id == p.id) return false;
        }
        patients.push_back(p);
        return true;
    }

    bool removeById(int id) {
        for (size_t i = 0; i < patients.size(); i++) {
            if (patients[i].id == id) {
                patients.erase(patients.begin() + i);
                return true;
            }
        }
        return false;
    }

    Patient* findById(int id) {
        for (auto& patient : patients) {
            if (patient.id == id) return &patient;
        }
        return nullptr;
    }

    int count() const { return patients.size(); }

    vector<Patient>& getAll() { return patients; }
};

// ----------------- AVL Tree -----------------
struct AVLNode {
    Patient data;
    AVLNode* left;
    AVLNode* right;
    int height;
    AVLNode(const Patient& p): data(p), left(nullptr), right(nullptr), height(1) {}
};

class PatientAVL {
private:
    AVLNode* root;

    int h(AVLNode* n) { return n ? n->height : 0; }
    int bal(AVLNode* n) { return n ? (h(n->left) - h(n->right)) : 0; }
    AVLNode* rightRotate(AVLNode* y);
    AVLNode* leftRotate(AVLNode* x);
    AVLNode* insertRec(AVLNode* node, const Patient& p, bool& ok);
    AVLNode* minNode(AVLNode* node);
    AVLNode* deleteRec(AVLNode* node, int id, bool& ok);
    Patient* searchRec(AVLNode* node, int id);
    void inorderToJson(AVLNode* node, string& out, bool& first);
    void destroy(AVLNode* node);

public:
    PatientAVL(): root(nullptr) {}
    ~PatientAVL() { destroy(root); }

    bool insert(const Patient& p);
    bool remove(int id);
    Patient* search(int id);
    string toJsonArray();
};
