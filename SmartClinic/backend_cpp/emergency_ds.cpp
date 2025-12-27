#include "emergency_ds.h"
#include <vector>
#include <queue>
#include <algorithm>

using namespace std;

// --- NormalQueue Implementation ---
NormalQueue::NormalQueue(int capacity) {
    this->capacity = capacity;
    queue.resize(capacity);
    front = 0;
    rear = -1;
    currentSize = 0;
}

NormalQueue::~NormalQueue() {
}

bool NormalQueue::enqueue(const EmergencyPatient& patient) {
    if (currentSize == capacity) return false;
    rear = (rear + 1) % capacity;
    queue[rear] = patient;
    currentSize++;
    return true;
}

bool NormalQueue::dequeue(EmergencyPatient& out) {
    if (currentSize == 0) return false;
    out = queue[front];
    front = (front + 1) % capacity;
    currentSize--;
    return true;
}

// --- UndoStack Implementation ---
UndoStack::UndoStack(int capacity) {
    this->capacity = capacity;
    stack.resize(capacity);
    top = -1;
}

UndoStack::~UndoStack() {
}

bool UndoStack::push(const string& item) {
    if (top == capacity - 1) return false;
    stack[++top] = item;
    return true;
}

bool UndoStack::pop(string& out) {
    if (top == -1) return false;
    out = stack[top--];
    return true;
}

// --- MaxHeap Implementation ---
MaxHeap::MaxHeap(int capacity) {
    this->capacity = capacity;
    heap.resize(capacity);
    currentSize = 0;
}

MaxHeap::~MaxHeap() {
}

void MaxHeap::swap(int i, int j) {
    EmergencyPatient temp = heap[i];
    heap[i] = heap[j];
    heap[j] = temp;
}

void MaxHeap::heapifyUp(int index) {
    while (index > 0) {
        int parentIndex = (index - 1) / 2;
        if (heap[parentIndex].severity >= heap[index].severity) break;
        swap(parentIndex, index);
        index = parentIndex;
    }
}

void MaxHeap::heapifyDown(int index) {
    while (true) {
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;
        int largest = index;

        if (leftChild < currentSize && heap[leftChild].severity > heap[largest].severity)
            largest = leftChild;
        if (rightChild < currentSize && heap[rightChild].severity > heap[largest].severity)
            largest = rightChild;

        if (largest == index) break;
        swap(index, largest);
        index = largest;
    }
}

bool MaxHeap::insert(const EmergencyPatient& patient) {
    if (currentSize == capacity) return false;
    heap[currentSize] = patient;
    heapifyUp(currentSize);
    currentSize++;
    return true;
}

bool MaxHeap::extractMax(EmergencyPatient& out) {
    if (currentSize == 0) return false;
    out = heap[0];
    heap[0] = heap[currentSize - 1];
    currentSize--;
    heapifyDown(0);
    return true;
}

bool MaxHeap::peek(EmergencyPatient& out) const {
    if (currentSize == 0) return false;
    out = heap[0];
    return true;
}

// --- ClinicGraph Implementation ---
ClinicGraph::ClinicGraph() {
    vertexCount = 0;
    adjacency.assign(MAXV, vector<int>(MAXV, 0));

    addRoom("Reception");
    addRoom("Triage");
    addRoom("Emergency_Room");
    addRoom("ICU");
    addRoom("Pharmacy");
    addRoom("Radiology");
    addRoom("General_Ward");
    addRoom("Operation_Theatre");
    addRoom("Cafeteria");

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

void ClinicGraph::addRoom(const string& name) {
    if (vertexCount < MAXV) {
        roomNames.push_back(name);
        vertexCount++;
    }
}

int ClinicGraph::indexOf(const string& name) const {
    for (int i = 0; i < vertexCount; i++) {
        if (roomNames[i] == name) return i;
    }
    return -1;
}

void ClinicGraph::addEdge(const string& a, const string& b) {
    int i = indexOf(a);
    int j = indexOf(b);
    if (i >= 0 && j >= 0) {
        adjacency[i][j] = 1;
        adjacency[j][i] = 1;
    }
}

bool ClinicGraph::shortestPathBFS(const string& from, const string& to, string& pathOut) {
    int startIndex = indexOf(from);
    int targetIndex = indexOf(to);

    if (startIndex < 0 || targetIndex < 0) return false;

    queue<int> q;
    vector<bool> visited(vertexCount, false);
    vector<int> parent(vertexCount, -1);

    q.push(startIndex);
    visited[startIndex] = true;

    bool found = false;
    while (!q.empty()) {
        int current = q.front();
        q.pop();

        if (current == targetIndex) {
            found = true;
            break;
        }

        for (int neighbor = 0; neighbor < vertexCount; neighbor++) {
            if (adjacency[current][neighbor] == 1 && !visited[neighbor]) {
                visited[neighbor] = true;
                parent[neighbor] = current;
                q.push(neighbor);
            }
        }
    }

    if (!found) return false;

    vector<int> path;
    int current = targetIndex;
    while (current != -1) {
        path.push_back(current);
        current = parent[current];
    }
    reverse(path.begin(), path.end());

    pathOut = "";
    for (size_t i = 0; i < path.size(); i++) {
        pathOut += roomNames[path[i]];
        if (i < path.size() - 1) pathOut += " -> ";
    }

    return true;
}
