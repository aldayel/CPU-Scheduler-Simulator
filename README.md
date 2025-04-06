# CPU Scheduler Simulator

This project is a Java-based simulation of a CPU scheduler. It demonstrates various scheduling algorithms, including First-Come-First-Served (FCFS), Round-Robin (RR), and Priority Scheduling with basic starvation handling. The simulator integrates simple system call simulations to mimic process creation, waiting, and termination.

---

## Overview

The CPU Scheduler Simulator is designed to emulate how an operating system schedules processes. Processes are represented as instances of a Process Control Block (PCB) and are managed using different scheduling algorithms. The project also includes a basic simulation of system calls to further emulate operating system behavior.

---

## Features

- **Process Loading:** Processes are loaded from a file using a dedicated thread, ensuring asynchronous file I/O operations.
- **Scheduling Algorithms:**  
  - **FCFS (First-Come-First-Serve):** Processes are executed in the order they are loaded.
  - **Round-Robin (RR):** Processes receive a time quantum (user-defined) for execution, simulating time-sliced scheduling.
  - **Priority Scheduling:** Processes are sorted based on priority, with a mechanism to handle potential starvation.
- **System Call Simulation:** The project simulates basic system calls (fork, wait, exit, abort) to mimic process management routines.
- **Gantt Chart Output:** After execution, the simulator prints a Gantt chart for each scheduling algorithm, showing process execution intervals.
- **Multithreading:** Utilizes Java threads to manage process loading and scheduling concurrently.

---

## Project Structure

- **CPU_Scheduler.java:**  
  The main class that orchestrates the simulation. It handles process loading, queue management, scheduling algorithms, and Gantt chart visualization.  
  citeturn0file0

- **PCB.java:**  
  Represents the Process Control Block, storing process details like ID, burst time, priority, memory requirement, and timing statistics.  
  citeturn0file2

- **SystemCall.java:**  
  An interface defining methods to simulate system calls such as process creation (`fork`), waiting (`waitt`), exiting, and aborting.  
  citeturn0file1

- **SimpleSystemCall.java:**  
  A simple implementation of the `SystemCall` interface. It simulates system calls with basic print statements and thread delays.  



## Requirements

- **Java JDK:** Version 8 or higher is recommended.
- **Operating System:** Platform independent (tested on Windows, macOS, and Linux).
- **Text File:** A process/job file (e.g., `job.txt`) containing process details in a specified format.


## Setup and Compilation

1. **Clone or Download the Repository:**
   - Ensure all Java files (`CPU_Scheduler.java`, `PCB.java`, `SystemCall.java`, `SimpleSystemCall.java`) are in the same directory.

2. **Compile the Source Code:**
   ```bash
   javac *.java
   ```

3. **Run the Simulator:**
   ```bash
   java CPU_Scheduler
   ```



## Usage

1. **Process File:**
   - The simulator expects a file containing process/job data. Update the file path in `CPU_Scheduler.java` (see the `loadProcesses` method) to point to your job file.

2. **Scheduling Selection:**
   - Upon running the simulator, you will be prompted to select a scheduling algorithm:
     - Enter `1` for FCFS.
     - Enter `2` for Round-Robin. (You will then be prompted to provide a time quantum.)
     - Enter `3` for Priority Scheduling.
     
3. **Output:**
   - The program outputs execution details to the console, including start times, wait times, turnaround times, and a Gantt chart for visualizing the process timeline.

---

## Process File Format

The process/job file should have entries in the following format (one per line):

```
<ProcessID>:<BurstTime>;<Priority>;<Memory>
```

- **ProcessID:** Unique identifier for the process.
- **BurstTime:** CPU burst time required.
- **Priority:** Priority level (used for Priority Scheduling).
- **Memory:** Memory requirement (in MB).

Example:
```
1:20;2;100
2:15;1;150
```

Processes exceeding the available memory limit (2048 MB) will be skipped with an appropriate message.
