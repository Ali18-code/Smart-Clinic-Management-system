#include "emergency_ds.h"

NormalQueue::NormalQueue(int capacity) {
    cap = capacity; arr = new EmergencyPatient[cap];
    front = 0; rear = -1; sz = 0;
}
NormalQueue::~NormalQueue(){ delete[] arr; }

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

UndoStack::UndoStack(int capacity) { cap = capacity; arr = new std::string[cap]; top = -1; }
UndoStack::~UndoStack(){ delete[] arr; }
bool UndoStack::push(const std::string& s){ if(top+1>=cap) return false; arr[++top]=s; return true; }
bool UndoStack::pop(std::string& out){ if(top<0) return false; out=arr[top--]; return true; }

MaxHeap::MaxHeap(int capacity) { cap = capacity; heap = new EmergencyPatient[cap]; sz = 0; }
MaxHeap::~MaxHeap(){ delete[] heap; }
void MaxHeap::swap(int i,int j){ EmergencyPatient t=heap[i]; heap[i]=heap[j]; heap[j]=t; }

void MaxHeap::heapifyUp(int i){
    while(i>0){
        int p=(i-1)/2;
        if(heap[p].severity >= heap[i].severity) break;
        swap(p,i); i=p;
    }
}
void MaxHeap::heapifyDown(int i){
    while(true){
        int l=2*i+1, r=2*i+2, best=i;
        if(l<sz && heap[l].severity > heap[best].severity) best=l;
        if(r<sz && heap[r].severity > heap[best].severity) best=r;
        if(best==i) break;
        swap(i,best); i=best;
    }
}
bool MaxHeap::insert(const EmergencyPatient& p){
    if(sz==cap) return false;
    heap[sz]=p; heapifyUp(sz); sz++;
    return true;
}
bool MaxHeap::extractMax(EmergencyPatient& out){
    if(sz==0) return false;
    out=heap[0];
    heap[0]=heap[sz-1];
    sz--;
    heapifyDown(0);
    return true;
}
bool MaxHeap::peek(EmergencyPatient& out) const {
    if(sz==0) return false;
    out=heap[0];
    return true;
}

// -------- Graph BFS ----------
ClinicGraph::ClinicGraph() {
    vCount = 0;
    for(int i=0;i<MAXV;i++) for(int j=0;j<MAXV;j++) adj[i][j]=0;

    // default rooms (edit as per your clinic map)
    addRoom("Reception");
    addRoom("OPD");
    addRoom("Emergency");
    addRoom("Lab");
    addRoom("Pharmacy");
    addRoom("XRay");
    addRoom("ICU");

    addEdge("Reception","OPD");
    addEdge("Reception","Emergency");
    addEdge("OPD","Lab");
    addEdge("Lab","Pharmacy");
    addEdge("Emergency","ICU");
    addEdge("Emergency","XRay");
    addEdge("XRay","Lab");
}

void ClinicGraph::addRoom(const std::string& name){
    if(vCount>=MAXV) return;
    names[vCount++] = name;
}
int ClinicGraph::indexOf(const std::string& name) const {
    for(int i=0;i<vCount;i++) if(names[i]==name) return i;
    return -1;
}
void ClinicGraph::addEdge(const std::string& a,const std::string& b){
    int i=indexOf(a), j=indexOf(b);
    if(i<0||j<0) return;
    adj[i][j]=1; adj[j][i]=1;
}

bool ClinicGraph::shortestPathBFS(const std::string& from, const std::string& to, std::string& pathOut){
    int s=indexOf(from), t=indexOf(to);
    if(s<0||t<0) return false;

    int q[MAXV]; int f=0, r=0;
    int vis[MAXV]; int parent[MAXV];
    for(int i=0;i<MAXV;i++){ vis[i]=0; parent[i]=-1; }

    q[r++]=s; vis[s]=1;

    while(f<r){
        int u=q[f++];
        if(u==t) break;
        for(int v=0; v<vCount; v++){
            if(adj[u][v] && !vis[v]){
                vis[v]=1; parent[v]=u;
                q[r++]=v;
            }
        }
    }

    if(!vis[t]) return false;

    int stack[MAXV]; int top=-1;
    int cur=t;
    while(cur!=-1){ stack[++top]=cur; cur=parent[cur]; }

    pathOut="";
    for(int i=top;i>=0;i--){
        pathOut += names[stack[i]];
        if(i!=0) pathOut += " -> ";
    }
    return true;
}
