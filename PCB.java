import java.util.*;
import java.util.concurrent.*;
import java.io.*;

class PCB {
    int id, burstTime, priority, memory, originalBurstTime;
    int waitTime, turnaroundTime, startTime;

    public PCB(int id, int burstTime, int priority, int memory) {
        this.id = id;
        this.burstTime = burstTime;
        this.priority = priority;
        this.memory = memory;
        this.originalBurstTime = burstTime;
        this.waitTime = 0;
        this.turnaroundTime = 0;
    }
}

