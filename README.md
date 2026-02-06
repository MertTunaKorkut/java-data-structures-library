# Java Data Structures Library

This repository contains a comprehensive implementation of fundamental data structures, developed to understand the core algorithms behind Java's built-in collections.

## Project Overview
The goal of this project was to manually implement various Abstract Data Types (ADTs) using different underlying strategies (Array-based, Linked-based, Hashing, Tree-based) to analyze their efficiency.

**Key Features:**
* **Language:** Java
* **Architecture:** OOP Principles (Polymorphism, Inheritance)
* **Scope:** 11 Data Types, 20+ Concrete Implementations.

## Implemented Structures
I have implemented the logic for the following structures within the provided academic interfaces:

### 1. Lists & Queues
* **List:** `ArrayList`, `LinkedList`
* **Queue:** `ArrayQueue`, `LinkedQueue`
* **Stack:** `ArrayStack`, `LinkedStack`

### 2. Dictionaries & Sets
* **Dictionary:** `AVLDictionary` (Self-balancing), `HashDictionary`, `SortedArrayDictionary`, `SortedLinkedDictionary`
* **Set:** `AVLSet`, `HashSet`, `SortedArraySet`, `SortedLinkedSet`
* **Bag:** `HashBag`, `SortedArrayBag`, `SortedLinkedBag`

### 3. Hashing & Heaps
* **HashTable:** `LinearProbingHashTable`, `SeparateChainingHashTable`
* **Heap:** `BinaryHeap`, `MaxiphobicHeap`, `WBLeftistHeap`
* **Priority Queue:** `MaxiphobicHeapPriorityQueue`, `SortedArrayPriorityQueue`

### 4. Trees & Graphs
* **Search Tree:** `AVLTree`, `BinarySearchTree (BST)`
* **Graph:** `DictionaryGraph`, `DictionaryDiGraph` (Directed Graph)
* **Graph Algorithms:** `DijkstraAlgorithm` (Shortest Path Finding)

## Academic Integrity Note
This project was developed as part of a university coursework.
* **Framework:** Abstract classes, interfaces, tests, and base traversal algorithms (BFS/DFS) were provided by the course instructor.
* **Implementation:** All core data structure logic, collision handling, tree rotations, and the implementation of **Dijkstra's Algorithm** were written by **Mert Tuna Korkut**.
