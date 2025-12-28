Smart Clinic Management System
1. Group Information
Group Members
Name	Registration ID
Ali Muhammad	242759
Hassan bin Nisar	242693
Contribution Statement

Both members collaboratively contributed to:

Java Swing GUI development

C++ Data Structures & Algorithm implementation

Java ↔ C++ File-Based Integration

Testing & Debugging

Documentation

GitHub Version Control

Although each member led specific modules, all design and integration decisions were shared and mutually implemented.

2. Project Title & System Concept
Project Title
⭐ Smart Clinic Management & Patient Flow Optimization System
System Description

The Smart Clinic Management System is a desktop-based hospital automation system designed to manage:

✔ Patient Records
✔ Emergency Handling
✔ Appointment Scheduling
✔ Reports & Analytics

The project’s core objective is to apply custom-implemented Data Structures & Algorithms (DSA) without using library-built DS structures — ensuring performance, learning depth, and full algorithm control.

All modules operate as one single integrated system, not as separate projects.

Technology Stack Statement
Layer	Technology
Frontend GUI	Java Swing
Backend Logic	C++
Integration Method	JSON-based File Exchange

No built-in Java or C++ DS libraries were used — all DSAs are manually implemented. 💪

3. System Module Design Overview

The system contains four integrated modules:

1️⃣ Patient Records Management
2️⃣ Emergency Queue & Navigation
3️⃣ Reports & Analytics
4️⃣ Appointment Scheduling

Each module internally uses Level-1 and Level-2 DSAs as required by the course.

MODULE 1 — Patient Records Management (Lead: Ali Muhammad)
(A) C++ Component — DSAs & Algorithms
Level-1 Data Structures Used
1. Arrays

Temporary storage before structured insertion

Provides indexed access

2. Linked List

Maintains full patient record list

Supports:

Insert

Delete

Traverse

Search

Benefits

Dynamic memory

No element shifting

Level-2 Data Structure Used
AVL Tree (Balanced BST)

Used to store patients sorted by Patient ID

Operations Implemented

Insert

Delete

Search

Height update

Rotations (LL, RR, LR, RL)

Time Complexity
✔ O(log n) for search/insertion/deletion

(B) Java GUI Component

GUI built with Java Swing

Input Fields

Patient ID

Name

Age

Disease

Appointment Date

Actions

✔ Add Patient
✔ Delete Patient
✔ Search Patient
✔ View All Patients

Output

Displayed neatly in Swing Tables & messages.

(C) Integration Layer — JSON File Exchange
Process Flow

Java writes → patient_input.json

C++ reads JSON

C++ updates DSAs

C++ writes result → patient_output.json

Java displays result

This keeps backend independent, modular, & testable.

(D) Work Allocation — Module-1
Task	Ali Muhammad	Hassan bin Nisar
AVL Tree Implementation	⭐ Lead	Assist
Linked List	Assist	⭐ Lead
Java UI	⭐ Lead	Assist
Integration	Shared	Shared
MODULE 2 — Emergency Queue & Clinic Navigation (Lead: Hassan bin Nisar)
(A) C++ DSAs Implemented
Level-1 DSAs
1. Queue (Normal Cases)

Models normal patient flow (FIFO)

2. Stack (Undo System)

Stores recent actions — simulating rollback.

Level-2 DSAs
1. Max-Heap / Priority Queue

Stores emergency cases ranked by severity (1–10)

✔ Highest severity treated first
✔ O(log n) priority operations

2. Graph + BFS

Represents clinic rooms as graph nodes.

Used for shortest path navigation 💊

✔ Doctor can quickly find route
✔ BFS ensures O(V+E) shortest path

(B) Java GUI
Input Fields

Patient Name

Severity Level

Starting Room

Destination Room

Actions

✔ Add emergency case
✔ Process next patient
✔ Display clinic route

(C) Integration Layer

JSON files used:

emergency_input.json

emergency_output.json

(D) Work Allocation — Module-2
Task	Ali Muhammad	Hassan bin Nisar
Heap / Priority Queue	Assist	⭐ Lead
Graph / BFS	⭐ Assist	Lead
GUI	Assist	⭐ Lead
Integration	Shared	Shared
MODULE 3 — Reports & Analytics System (Lead: Ali Muhammad)
(A) C++ Statistical Engine — DSAs Used
Level-1

✔ Arrays — for traversal
✔ Stack — for history tracking

Level-2

✔ Max-Heap — ranks top emergencies
✔ AVL Tree — ensures fast lookup

Generated Reports Include

📊 Total registered patients
📊 Total emergency cases
📊 Critical alerts (severity ≥ 7)
📊 Top emergency severity ranking

(B) Java Reports Panel

Displays analytics in:

✔ Count cards
✔ Data tables
✔ Visualized format

(C) Integration

Uses:

reports_input.json

reports_output.json

(D) Work Allocation — Module-3
Task	Ali Muhammad	Hassan
Report Algorithms	⭐ Lead	Assist
Heap Ranking	⭐ Lead	Assist
GUI	⭐ Lead	Assist
Integration	Shared	Shared
MODULE 4 — Appointment Scheduling System (Lead: Hassan bin Nisar)
(A) C++ DSAs Used
Level-1 — Circular Queue

Stores upcoming appointments.

✔ FIFO Order
✔ O(1) Time

Emergency Rule

If emergency patients exist →
⛔ Appointments pause

Else →
▶ Serve next appointment

(B) Java GUI Panel
Fields

Patient ID

Name

Doctor

Time

Actions

✔ Add Appointment
✔ Serve Next
✔ View List

(C) Integration

Files used:

appointment_input.json

appointment_output.json

(D) Work Allocation — Module-4
Task	Ali Muhammad	Hassan
Queue Logic	Assist	⭐ Lead
Emergency Check	Shared	Shared
GUI	Assist	⭐ Lead
Testing	Shared	Shared
4. Mandatory DSA Requirement Summary
Student	Level-1 DSAs	Level-2 DSAs
Ali Muhammad	Arrays, Stack, Queue	AVL Tree, Heap
Hassan bin Nisar	Linked List	Graph (BFS), Priority Queue

✔ Requirement Fully Met
✔ All DSAs Manual — No Library Use

5. UML Diagram Summary
Use Case Diagram Includes

Manage Patients

Schedule Appointments

Handle Emergencies

Generate Reports

Class Diagram Includes

Java Swing UI Panels

Integration Layer

C++ Data Structure Classes

6. GitHub Collaboration Requirement
Repository Name

DSA_Project_GroupX_AliMuhammad_HassanBinNisar

Repository Contains

✔ Java GUI Code
✔ C++ Source Files
✔ Integration Layer
✔ UML Diagrams
✔ Docs & ReadMe

Collaboration Followed

✔ Feature branches
✔ Pull requests
✔ Equal contribution
✔ Instructor access granted

🎯 Final Notes

This project demonstrates:

⭐ Real-world DSA application
⭐ Modular yet unified architecture
⭐ Safe hospital workflow modeling
⭐ Efficient search, storage & prioritization
<img width="1155" height="802" alt="UML" src="https://github.com/user-attachments/assets/62d15e38-a67e-42c8-9c54-4c69f78793f9" />
