#pragma once
#include <string>

struct EmergencyPatient {
    std::string name;
    int severity; // 1-10
};

class NormalQueue {
private:
    EmergencyPatient* arr;
    int cap, front, rear, sz;
public:
    NormalQueue(int capacity = 200);
    ~NormalQueue();
    bool enqueue(const EmergencyPatient& p);
    bool dequeue(EmergencyPatient& out);
    int size() const { return sz; }
};

class UndoStack {
private:
    std::string* arr;
    int top;
    int cap;
public:
    UndoStack(int capacity = 200);
    ~UndoStack();
    bool push(const std::string& s);
    bool pop(std::string& out);
};

class MaxHeap {
private:
    EmergencyPatient* heap;
    int cap, sz;
    void swap(int i, int j);
    void heapifyUp(int i);
    void heapifyDown(int i);
public:
    MaxHeap(int capacity = 200);
    ~MaxHeap();
    bool insert(const EmergencyPatient& p);
    bool extractMax(EmergencyPatient& out);
    bool peek(EmergencyPatient& out) const;
    int size() const { return sz; }
};

class ClinicGraph {
private:
    static const int MAXV = 30;
    std::string names[MAXV];
    int adj[MAXV][MAXV];
    int vCount;

    int indexOf(const std::string& name) const;
public:
    ClinicGraph();
    void addRoom(const std::string& name);
    void addEdge(const std::string& a, const std::string& b); // undirected
    bool shortestPathBFS(const std::string& from, const std::string& to, std::string& pathOut);
};
