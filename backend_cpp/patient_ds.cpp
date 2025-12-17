#include "patient_ds.h"

// ---------------- Linked List ----------------
PatientList::~PatientList() {
    ListNode* cur = head;
    while (cur) {
        ListNode* n = cur->next;
        delete cur;
        cur = n;
    }
}

bool PatientList::insert(const Patient& p) {
    // prevent duplicate
    if (findById(p.id) != nullptr) return false;
    ListNode* node = new ListNode(p);
    node->next = head;
    head = node;
    return true;
}

bool PatientList::removeById(int id) {
    ListNode* cur = head;
    ListNode* prev = nullptr;
    while (cur) {
        if (cur->data.id == id) {
            if (prev) prev->next = cur->next;
            else head = cur->next;
            delete cur;
            return true;
        }
        prev = cur;
        cur = cur->next;
    }
    return false;
}

Patient* PatientList::findById(int id) {
    ListNode* cur = head;
    while (cur) {
        if (cur->data.id == id) return &cur->data;
        cur = cur->next;
    }
    return nullptr;
}

int PatientList::count() const {
    int c = 0;
    ListNode* cur = head;
    while (cur) { c++; cur = cur->next; }
    return c;
}

// ---------------- AVL Tree ----------------
int PatientAVL::h(AVLNode* n) { return n ? n->height : 0; }

int PatientAVL::bal(AVLNode* n) { return n ? (h(n->left) - h(n->right)) : 0; }

AVLNode* PatientAVL::rightRotate(AVLNode* y) {
    AVLNode* x = y->left;
    AVLNode* t2 = x->right;

    x->right = y;
    y->left = t2;

    y->height = 1 + (h(y->left) > h(y->right) ? h(y->left) : h(y->right));
    x->height = 1 + (h(x->left) > h(x->right) ? h(x->left) : h(x->right));
    return x;
}

AVLNode* PatientAVL::leftRotate(AVLNode* x) {
    AVLNode* y = x->right;
    AVLNode* t2 = y->left;

    y->left = x;
    x->right = t2;

    x->height = 1 + (h(x->left) > h(x->right) ? h(x->left) : h(x->right));
    y->height = 1 + (h(y->left) > h(y->right) ? h(y->left) : h(y->right));
    return y;
}

AVLNode* PatientAVL::insertRec(AVLNode* node, const Patient& p, bool& ok) {
    if (!node) { ok = true; return new AVLNode(p); }

    if (p.id < node->data.id) node->left = insertRec(node->left, p, ok);
    else if (p.id > node->data.id) node->right = insertRec(node->right, p, ok);
    else { ok = false; return node; }

    node->height = 1 + (h(node->left) > h(node->right) ? h(node->left) : h(node->right));
    int b = bal(node);

    // LL
    if (b > 1 && p.id < node->left->data.id) return rightRotate(node);
    // RR
    if (b < -1 && p.id > node->right->data.id) return leftRotate(node);
    // LR
    if (b > 1 && p.id > node->left->data.id) {
        node->left = leftRotate(node->left);
        return rightRotate(node);
    }
    // RL
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
            if (!temp) {
                temp = node;
                node = nullptr;
            } else {
                *node = *temp;
            }
            delete temp;
        } else {
            AVLNode* temp = minNode(node->right);
            node->data = temp->data;
            node->right = deleteRec(node->right, temp->data.id, ok);
        }
    }

    if (!node) return node;

    node->height = 1 + (h(node->left) > h(node->right) ? h(node->left) : h(node->right));
    int b = bal(node);

    // LL
    if (b > 1 && bal(node->left) >= 0) return rightRotate(node);
    // LR
    if (b > 1 && bal(node->left) < 0) {
        node->left = leftRotate(node->left);
        return rightRotate(node);
    }
    // RR
    if (b < -1 && bal(node->right) <= 0) return leftRotate(node);
    // RL
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

void PatientAVL::inorderToJson(AVLNode* node, std::string& out, bool& first) {
    if (!node) return;
    inorderToJson(node->left, out, first);

    if (!first) out += ",";
    first = false;
    out += "{\"id\":";
    out += std::to_string(node->data.id);
    out += ",\"name\":\"" + node->data.name + "\"";
    out += ",\"age\":";
    out += std::to_string(node->data.age);
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

std::string PatientAVL::toJsonArray() {
    std::string out = "[";
    bool first = true;
    inorderToJson(root, out, first);
    out += "]";
    return out;
}
