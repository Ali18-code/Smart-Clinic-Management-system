#include "emergency_ds.h"
#include <iostream>
#include <string>

// --- NormalQueue Implementation ---
NormalQueue::NormalQueue(int capacity) {
    cap = capacity;
    arr = new EmergencyPatient[cap];
    front = 0;
    rear = -1;
    sz = 0;
}

NormalQueue::~NormalQueue() {
    delete[] arr;
}

bool NormalQueue::enqueue(const EmergencyPatient& p) {
    if (sz == cap) return false;
    rear = (rear + 1) % cap;
    arr[rear] = p;
    sz++;
    return true;
}

bool NormalQueue::dequeue(EmergencyPatient& out) {
    if (sz == 0) return false;
    out = arr[front];
    front = (front + 1) % cap;
    sz--;
    return true;
}

// --- UndoStack Implementation ---
UndoStack::UndoStack(int capacity) {
    cap = capacity;
    arr = new std::string[cap];
    top = -1;
}

UndoStack::~UndoStack() {
    delete[] arr;
}

bool UndoStack::push(const std::string& s) {
    if (top == cap - 1) return false;
    arr[++top] = s;
    return true;
}

bool UndoStack::pop(std::string& out) {
    if (top == -1) return false;
    out = arr[top--];
    return true;
}

// --- MaxHeap Implementation ---
// Used for Priority Queue: Extracts patient with highest severity
MaxHeap::MaxHeap(int capacity) {
    cap = capacity;
    heap = new EmergencyPatient[cap];
    sz = 0;
}

MaxHeap::~MaxHeap() {
    delete[] heap;
}

void MaxHeap::swap(int i, int j) {
    EmergencyPatient temp = heap[i];
    heap[i] = heap[j];
    heap[j] = temp;
}



void MaxHeap::heapifyUp(int i) {
    while (i > 0) {
        int parent = (i - 1) / 2;
        if (heap[parent].severity >= heap[i].severity) break;
        swap(parent, i);
        i = parent;
    }
}

void MaxHeap::heapifyDown(int i) {
    while (true) {
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        int largest = i;

        if (left < sz && heap[left].severity > heap[largest].severity)
            largest = left;
        if (right < sz && heap[right].severity > heap[largest].severity)
            largest = right;

        if (largest == i) break;
        swap(i, largest);
        i = largest;
    }
}

bool MaxHeap::insert(const EmergencyPatient& p) {
    if (sz == cap) return false;
    heap[sz] = p;
    heapifyUp(sz);
    sz++;
    return true;
}

bool MaxHeap::extractMax(EmergencyPatient& out) {
    if (sz == 0) return false;
    out = heap[0];
    heap[0] = heap[sz - 1];
    sz--;
    heapifyDown(0);
    return true;
}

bool MaxHeap::peek(EmergencyPatient& out) const {
    if (sz == 0) return false;
    out = heap[0];
    return true;
}

// --- ClinicGraph Implementation ---
// Used for Navigation: Finds the shortest path between locations using BFS
ClinicGraph::ClinicGraph() {
    vCount = 0;
    // Initialize adjacency matrix to 0 (no connections)
    for (int i = 0; i < MAXV; i++) {
        for (int j = 0; j < MAXV; j++) {
            adj[i][j] = 0;
        }
    }

    // Define rooms (Must match Java side exactly)
    addRoom("Reception");
    addRoom("Triage");
    addRoom("Emergency_Room");
    addRoom("ICU");
    addRoom("Pharmacy");
    addRoom("Radiology");
    addRoom("General_Ward");
    addRoom("Operation_Theatre");
    addRoom("Cafeteria");

    // Define Connections (Edges)
    addEdge("Reception", "Triage");
    addEdge("Reception", "Cafeteria");
    addEdge("Triage", "Emergency_Room");
    addEdge("Triage", "General_Ward");
    addEdge("Emergency_Room", "ICU");
    addEdge("Emergency_Room", "Radiology");
    addEdge("Emergency_Room", "Operation_Theatre");
    addEdge("Radiology", "General_Ward");
    addEdge("General_Ward", "Pharmacy");
}

void ClinicGraph::addRoom(const std::string& name) {
    if (vCount < MAXV) {
        names[vCount++] = name;
    }
}

int ClinicGraph::indexOf(const std::string& name) const {
    for (int i = 0; i < vCount; i++) {
        if (names[i] == name) return i;
    }
    return -1;
}

void ClinicGraph::addEdge(const std::string& a, const std::string& b) {
    int i = indexOf(a);
    int j = indexOf(b);
    if (i >= 0 && j >= 0) {
        adj[i][j] = 1;
        adj[j][i] = 1;
    }
}



bool ClinicGraph::shortestPathBFS(const std::string& from, const std::string& to, std::string& pathOut) {
    int startIdx = indexOf(from);
    int targetIdx = indexOf(to);

    if (startIdx < 0 || targetIdx < 0) return false;

    // BFS setup using fixed arrays instead of vectors
    int queue[MAXV];
    int head = 0, tail = 0;
    
    int visited[MAXV];
    int parent[MAXV];
    for (int i = 0; i < MAXV; i++) {
        visited[i] = 0;
        parent[i] = -1;
    }

    queue[tail++] = startIdx;
    visited[startIdx] = 1;

    bool found = false;
    while (head < tail) {
        int current = queue[head++];
        
        if (current == targetIdx) {
            found = true;
            break;
        }

        for (int neighbor = 0; neighbor < vCount; neighbor++) {
            if (adj[current][neighbor] == 1 && !visited[neighbor]) {
                visited[neighbor] = 1;
                parent[neighbor] = current;
                queue[tail++] = neighbor;
            }
        }
    }

    if (!found) return false;

    // Path Reconstruction using a temporary stack
    int pathStack[MAXV];
    int top = -1;
    int curr = targetIdx;
    while (curr != -1) {
        pathStack[++top] = curr;
        curr = parent[curr];
    }

    pathOut = "";
    while (top >= 0) {
        pathOut += names[pathStack[top--]];
        if (top >= 0) pathOut += " -> ";
    }

    return true;
}
