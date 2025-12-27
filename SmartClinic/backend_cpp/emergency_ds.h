#pragma once
#include <string>
#include <vector>

using namespace std;

struct EmergencyPatient {
    string name;
    int severity; // 1-10
};

class NormalQueue {
private:
    vector<EmergencyPatient> queue;
    int capacity, front, rear, currentSize;
public:
    NormalQueue(int capacity = 200);
    ~NormalQueue();
    bool enqueue(const EmergencyPatient& patient);
    bool dequeue(EmergencyPatient& out);
    int size() const { return currentSize; }
};

class UndoStack {
private:
    vector<string> stack;
    int top;
    int capacity;
public:
    UndoStack(int capacity = 200);
    ~UndoStack();
    bool push(const string& item);
    bool pop(string& out);
};

class MaxHeap {
private:
    vector<EmergencyPatient> heap;
    int capacity, currentSize;
    void swap(int i, int j);
    void heapifyUp(int index);
    void heapifyDown(int index);
public:
    MaxHeap(int capacity = 200);
    ~MaxHeap();
    bool insert(const EmergencyPatient& patient);
    bool extractMax(EmergencyPatient& out);
    bool peek(EmergencyPatient& out) const;
    int size() const { return currentSize; }
};

class ClinicGraph {
private:
    static const int MAXV = 30;
    vector<string> roomNames;
    vector<vector<int>> adjacency;
    int vertexCount;

    int indexOf(const string& name) const;
public:
    ClinicGraph();
    void addRoom(const string& name);
    void addEdge(const string& a, const string& b); // undirected
    bool shortestPathBFS(const string& from, const string& to, string& pathOut);
};
