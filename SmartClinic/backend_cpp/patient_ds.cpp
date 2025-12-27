#include "patient_ds.h"
#include <string>
#include <vector>
using namespace std;

// ---------------- Linked List ----------------
PatientList::~PatientList() {
    while (!patients.empty()) {
        patients.pop_back();
    }
}

bool PatientList::insert(const Patient& p) {
    for (auto& patient : patients) {
        if (patient.id == p.id) return false;
    }
    patients.push_back(p);
    return true;
}

bool PatientList::removeById(int id) {
    for (size_t i = 0; i < patients.size(); i++) {
        if (patients[i].id == id) {
            patients.erase(patients.begin() + i);
            return true;
        }
    }
    return false;
}

Patient* PatientList::findById(int id) {
    for (auto& patient : patients) {
        if (patient.id == id) return &patient;
    }
    return nullptr;
}

int PatientList::count() const {
    return patients.size();
}

// ---------------- AVL Tree ----------------
int PatientAVL::h(AVLNode* n) { return n ? n->height : 0; }

int PatientAVL::bal(AVLNode* n) { return n ? (h(n->left) - h(n->right)) : 0; }

AVLNode* PatientAVL::rightRotate(AVLNode* y) {
    AVLNode* x = y->left;
    AVLNode* t2 = x->right;

    x->right = y;
    y->left = t2;

    y->height = 1 + max(h(y->left), h(y->right));
    x->height = 1 + max(h(x->left), h(x->right));
    return x;
}

AVLNode* PatientAVL::leftRotate(AVLNode* x) {
    AVLNode* y = x->right;
    AVLNode* t2 = y->left;

    y->left = x;
    x->right = t2;

    x->height = 1 + max(h(x->left), h(x->right));
    y->height = 1 + max(h(y->left), h(y->right));
    return y;
}

AVLNode* PatientAVL::insertRec(AVLNode* node, const Patient& p, bool& ok) {
    if (!node) { ok = true; return new AVLNode(p); }

    if (p.id < node->data.id) node->left = insertRec(node->left, p, ok);
    else if (p.id > node->data.id) node->right = insertRec(node->right, p, ok);
    else { ok = false; return node; }

    node->height = 1 + max(h(node->left), h(node->right));
    int b = bal(node);

    if (b > 1 && p.id < node->left->data.id) return rightRotate(node);
    if (b < -1 && p.id > node->right->data.id) return leftRotate(node);
    if (b > 1 && p.id > node->left->data.id) {
        node->left = leftRotate(node->left);
        return rightRotate(node);
    }
    if (b < -1 && p.id < node->right->data.id) {
        node->right = rightRotate(node->right);
        return leftRotate(node);
    }
    return node;
}

AVLNode* PatientAVL::minNode(AVLNode* node) {
    AVLNode* cur = node;
    while (cur && cur->left) cur = cur->left;
    return cur;
}

AVLNode* PatientAVL::deleteRec(AVLNode* node, int id, bool& ok) {
    if (!node) { ok = false; return node; }

    if (id < node->data.id) node->left = deleteRec(node->left, id, ok);
    else if (id > node->data.id) node->right = deleteRec(node->right, id, ok);
    else {
        ok = true;
        if (!node->left || !node->right) {
            AVLNode* temp = node->left ? node->left : node->right;
            if (!temp) { delete node; node = nullptr; }
            else { *node = *temp; delete temp; }
        } else {
            AVLNode* temp = minNode(node->right);
            node->data = temp->data;
            node->right = deleteRec(node->right, temp->data.id, ok);
        }
    }

    if (!node) return node;

    node->height = 1 + max(h(node->left), h(node->right));
    int b = bal(node);

    if (b > 1 && bal(node->left) >= 0) return rightRotate(node);
    if (b > 1 && bal(node->left) < 0) {
        node->left = leftRotate(node->left);
        return rightRotate(node);
    }
    if (b < -1 && bal(node->right) <= 0) return leftRotate(node);
    if (b < -1 && bal(node->right) > 0) {
        node->right = rightRotate(node->right);
        return leftRotate(node);
    }
    return node;
}

Patient* PatientAVL::searchRec(AVLNode* node, int id) {
    if (!node) return nullptr;
    if (id == node->data.id) return &node->data;
    if (id < node->data.id) return searchRec(node->left, id);
    return searchRec(node->right, id);
}

void PatientAVL::inorderToJson(AVLNode* node, string& out, bool& first) {
    if (!node) return;
    inorderToJson(node->left, out, first);

    if (!first) out += ",";
    first = false;
    out += "{\"id\":" + to_string(node->data.id);
    out += ",\"name\":\"" + node->data.name + "\"";
    out += ",\"age\":" + to_string(node->data.age);
    out += ",\"disease\":\"" + node->data.disease + "\"";
    out += ",\"date\":\"" + node->data.date + "\"}";
    inorderToJson(node->right, out, first);
}

void PatientAVL::destroy(AVLNode* node) {
    if (!node) return;
    destroy(node->left);
    destroy(node->right);
    delete node;
}

bool PatientAVL::insert(const Patient& p) {
    bool ok = false;
    root = insertRec(root, p, ok);
    return ok;
}

bool PatientAVL::remove(int id) {
    bool ok = false;
    root = deleteRec(root, id, ok);
    return ok;
}

Patient* PatientAVL::search(int id) {
    return searchRec(root, id);
}

string PatientAVL::toJsonArray() {
    string out = "[";
    bool first = true;
    inorderToJson(root, out, first);
    out += "]";
    return out;
}
