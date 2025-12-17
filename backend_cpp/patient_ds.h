#pragma once
#include <string>

struct Patient {
    int id;
    std::string name;
    int age;
    std::string disease;
    std::string date;
};

struct ListNode {
    Patient data;
    ListNode* next;
    ListNode(const Patient& p): data(p), next(nullptr) {}
};

class PatientList {
private:
    ListNode* head;
public:
    PatientList(): head(nullptr) {}
    ~PatientList();
    bool insert(const Patient& p);
    bool removeById(int id);
    Patient* findById(int id);
    int count() const;
    ListNode* getHead() const { return head; }
};

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

    int h(AVLNode* n);
    int bal(AVLNode* n);
    AVLNode* rightRotate(AVLNode* y);
    AVLNode* leftRotate(AVLNode* x);
    AVLNode* insertRec(AVLNode* node, const Patient& p, bool& ok);
    AVLNode* minNode(AVLNode* node);
    AVLNode* deleteRec(AVLNode* node, int id, bool& ok);
    Patient* searchRec(AVLNode* node, int id);
    void inorderToJson(AVLNode* node, std::string& out, bool& first);
    void destroy(AVLNode* node);

public:
    PatientAVL(): root(nullptr) {}
    ~PatientAVL() { destroy(root); }
    bool insert(const Patient& p);
    bool remove(int id);
    Patient* search(int id);
    std::string toJsonArray(); // returns patients as JSON array sorted by id
};

