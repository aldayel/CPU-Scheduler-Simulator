import java.util.*;
import java.util.concurrent.*;
import java.io.*;

public class CPU_Scheduler {

    // Max memory in MB
    final int MAX_MEMORY = 2048;

    // Queue for the job loading process
    Queue<PCB> jobQueue = new LinkedList<>();
    Queue<PCB> readyQueue = new LinkedList<>();
    List<PCB> completedJobs = new ArrayList<>();

    // Create an instance of SimpleSystemCall (SystemCall interface implementation)
    SystemCall systemCall = new SimpleSystemCall();

    // Method to load processes from a file
    public void loadProcesses(String fileName) {
        Runnable loadJobTask = new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(fileName));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        try {
                            String[] parts = line.split(":|;");
                            if (parts.length < 4) {
                                throw new IllegalArgumentException("Invalid job format.");
                            }

                            int id = Integer.parseInt(parts[0]);
                            int burstTime = Integer.parseInt(parts[1]);
                            int priority = Integer.parseInt(parts[2]);
                            int memory = Integer.parseInt(parts[3]);

                            PCB pcb = new PCB(id, burstTime, priority, memory);
                            jobQueue.add(pcb);
                            System.out.println("Process " + id + " loaded.");
                        } catch (Exception e) {
                            System.out.println("Error reading job data: " + e.getMessage());
                        }
                    }
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Error reading the file: " + e.getMessage());
                }
            }
        };

        Thread loadThread = new Thread(loadJobTask);
        loadThread.start();
        try {
            loadThread.join();  // Wait for the file reading thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method to load jobs into the ready queue
    public void loadJobsIntoReadyQueue() {
        Runnable loadReadyQueueTask = new Runnable() {
            @Override
            public void run() {
                int usedMemory = 0;
                while (!jobQueue.isEmpty()) {
                    PCB job = jobQueue.poll();
                    if (usedMemory + job.memory <= MAX_MEMORY) {
                        readyQueue.add(job);
                        usedMemory += job.memory;
                        job.startTime = usedMemory; // Track the time when the job enters the ready queue
                        System.out.println("Job " + job.id + " added to ready queue.");
                    } else {
                        System.out.println("Job " + job.id + " cannot be loaded due to memory constraints.");
                    }
                }
            }
        };

        Thread loadThread = new Thread(loadReadyQueueTask);
        loadThread.start();
        try {
            loadThread.join();  // Wait for the loading of jobs into the ready queue
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // First-Come-First-Serve (FCFS) Scheduling with System Call Integration
    public void FCFS() {
        if (readyQueue.isEmpty()) {
            System.out.println("No jobs available to schedule.");
            return;
        }

        int currentTime = 0;
        System.out.println("\nFCFS Execution Details:");
        while (!readyQueue.isEmpty()) {
            PCB job = readyQueue.poll();
            
            systemCall.waitt();  // Simulate waiting for the process to finish

            job.waitTime = currentTime;
            job.turnaroundTime = job.waitTime + job.burstTime;
            currentTime += job.burstTime;

            System.out.println("Process " + job.id + " | Stop Time: " + currentTime + " | Wait Time: " + job.waitTime + " | Turnaround Time: " + job.turnaroundTime);

            completedJobs.add(job);
        }

        // Print averages
        printResults("FCFS");
        printGanttChartFCFS();
    }


 // Round-Robin (RR) Scheduling with Quantum = 7ms
    public void RR(int timeQuantum) {
        if (readyQueue.isEmpty()) {
            System.out.println("No jobs available to schedule.");
            return;
        }

        int currentTime = 0;
        System.out.println("\nRound-Robin Execution Details:");
        Queue<PCB> tempQueue = new LinkedList<>(readyQueue);
        List<String> ganttChart = new ArrayList<>();  // To store Gantt chart details

        // For each process, calculate waiting and turnaround time
        while (!tempQueue.isEmpty()) {
            PCB job = tempQueue.poll();

            int startTime = currentTime;  // Track start time for this process
            int burstTime = Math.min(job.burstTime, timeQuantum); // Execute up to quantum or remaining burst time
            
            System.out.println("Selected Process " + job.id + " | Start Time: " + currentTime + " | Remaining Burst Time: " + job.burstTime);

            // Job execution for the quantum or until completion
            currentTime += burstTime;
            job.burstTime -= burstTime;

            // Add to Gantt chart (show job execution on the timeline)
            ganttChart.add("P" + job.id + " (" + startTime + " - " + currentTime + ")");

            if (job.burstTime > 0) {
                // If the job still has burst time left, add it back to the queue for next round
                tempQueue.add(job);
            } else {
                // Job is completed
                job.waitTime = currentTime - job.originalBurstTime - job.burstTime; // Correct wait time calculation
                job.turnaroundTime = currentTime; // Turnaround time is the finish time

                System.out.println("Process " + job.id + " | Stop Time: " + currentTime + " | Wait Time: " + job.waitTime + " | Turnaround Time: " + job.turnaroundTime);
                completedJobs.add(job);
            }
            // Simulate time passing, giving a "wait" between process executions
            try {
                Thread.sleep(300); // Adds a 1-second delay between each process execution
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        
        }

        // Print results
        printResults("Round-Robin");

        // Print the Gantt chart
        printGanttChartRR(ganttChart);
    }






    // Priority Scheduling with Starvation Handling
 // Priority Scheduling with Starvation Handling
    public void priorityScheduling() {
        if (readyQueue.isEmpty()) {
            System.out.println("No jobs available to schedule.");
            return;
        }

        int currentTime = 0;
        System.out.println("\nPriority Scheduling Execution Details:");
        List<PCB> sortedQueue = new ArrayList<>(readyQueue);
        sortedQueue.sort(Comparator.comparingInt(a -> a.priority));

        // Iterate through each job based on priority
        for (PCB job : sortedQueue) {
            systemCall.waitt();  // Simulate waiting for the process to finish

            // Print the job start time and burst time
            System.out.println("Selected Process " + job.id + " | Start Time: " + currentTime + " | Burst Time: " + job.burstTime);

            // Calculate waiting time and check for starvation
            job.waitTime = currentTime;  // Waiting time when the job starts executing
            int starvationThreshold = getStarvationThreshold(job.priority);  // Get threshold for starvation

            if (job.waitTime > starvationThreshold) {
                System.out.println("Process " + job.id + " is starved!");  // Indicate starvation
            }

            job.turnaroundTime = job.waitTime + job.burstTime;

            // Process execution
            currentTime += job.burstTime;

            // Display process completion time
            System.out.println("Process " + job.id + " | Stop Time: " + currentTime + " | Wait Time: " + job.waitTime + " | Turnaround Time: " + job.turnaroundTime);

            // Add completed job to the list
            completedJobs.add(job);
        }
        

        // Print the results and Gantt chart
        printResults("Priority Scheduling");
        printGanttChartPriorityScheduling();
    }


    // Define starvation threshold based on priority
    private int getStarvationThreshold(int priority) {
        switch (priority) {
            case 1: return 50;  // High priority, small threshold
            case 2: return 60;
            case 3: return 70;
            case 4: return 80;
            case 5: return 90;
            case 6: return 100;
            case 7: return 120;
            case 8: return 150; // Low priority, large threshold
            default: return 100;
        }
    }

    // Print the results
 // Print the results
    public void printResults(String algorithm) {
        int totalWaitTime = 0, totalTurnaroundTime = 0;
        
        for (PCB job : completedJobs) {
            totalWaitTime += job.waitTime;
            totalTurnaroundTime += job.turnaroundTime;
        }

        double avgWaitTime = (double) totalWaitTime / completedJobs.size();
        double avgTurnaroundTime = (double) totalTurnaroundTime / completedJobs.size();

        System.out.println("Average Wait Time for " + algorithm + ": " + String.format("%.2f", avgWaitTime));
        System.out.println("Average Turnaround Time for " + algorithm + ": " + String.format("%.2f", avgTurnaroundTime));
    }

    // Print Gantt charts
    public void printGanttChartFCFS() {
        int currentTime = 0;
        System.out.print("\nGantt Chart for FCFS:");
        for (PCB job : completedJobs) {
            System.out.print("| P" + job.id + " (" + currentTime + " - " + (currentTime + job.burstTime) + ") ");
            currentTime += job.burstTime;
        }
        System.out.println("|");
    }

    public void printGanttChartRR(List<String> ganttChart) {
        System.out.print("\nGantt Chart for Round-Robin: ");
        for (String process : ganttChart) {
            System.out.print("| " + process + " ");
        }
        System.out.println("|");
    }

 // Gantt Chart for Priority Scheduling
    public void printGanttChartPriorityScheduling() {
        int currentTime = 0;
        System.out.print("\nGantt Chart for Priority Scheduling: ");
        for (PCB job : completedJobs) {
            System.out.print("| P" + job.id + " (" + currentTime + " - " + (currentTime + job.burstTime) + ") ");
            currentTime += job.burstTime;
        }
        System.out.println("|");
    }
    public static void main(String[] args) {
        CPU_Scheduler scheduler = new CPU_Scheduler();

        // Load processes from the file
        scheduler.loadProcesses("C:\\Users\\ohali\\Downloads\\csc227\\csc227\\job.txt");

        // Load jobs into the ready queue
        scheduler.loadJobsIntoReadyQueue();

        // Create a new thread for scheduling
        Runnable mainSchedulingTask = new Runnable() {
            @Override
            public void run() {
                // User selects scheduling algorithm
                Scanner scanner = new Scanner(System.in);
                System.out.println("Select scheduling algorithm: 1. FCFS 2. Round-Robin 3. Priority");
                int choice = scanner.nextInt();

                // Perform scheduling based on user selection
                switch (choice) {
                    case 1:
                        scheduler.FCFS();
                        break;
                    case 2:
                        System.out.println("Enter time quantum for Round-Robin:");
                        int timeQuantum = scanner.nextInt();
                        scheduler.RR(timeQuantum);
                        break;
                    case 3:
                        scheduler.priorityScheduling();
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
                scanner.close();
            }
        };

        // Start the main scheduling thread
        Thread mainSchedulingThread = new Thread(mainSchedulingTask);
        mainSchedulingThread.start();
    }
}
    /*
    public static void main(String[] args) {
        CPU_Scheduler scheduler = new CPU_Scheduler();

        // Load processes from the file
        scheduler.loadProcesses("C:\\Users\\ohali\\Downloads\\csc227\\csc227\\job.txt");

        // Load jobs into the ready queue
        scheduler.loadJobsIntoReadyQueue();

        // User selects scheduling algorithm
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select scheduling algorithm: 1. FCFS 2. Round-Robin 3. Priority");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                scheduler.FCFS();
                break;
            case 2:
                System.out.println("Enter time quantum for Round-Robin:");
                int timeQuantum = scanner.nextInt();
                scheduler.RR(timeQuantum);
                break;
            case 3:
                scheduler.priorityScheduling();
                break;
            default:
                System.out.println("Invalid choice.");
        }

        scanner.close();
    }
}*/
