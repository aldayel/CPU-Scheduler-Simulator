import java.util.*;

public class SimpleSystemCall implements SystemCall {
    
    @Override
    public void fork() {
        System.out.println("Forking a new process...");
        // Simulate creating a new process (dummy job)
        PCB newJob = new PCB(generateNewId(), 10, 1, 50);
        System.out.println("New process created with ID: " + newJob.id);
    }

    @Override
    public void waitt() {
        System.out.println("Waiting for a process to finish...");
        // Simulate the process waiting
        try {
            Thread.sleep(300);  // Simulate a process waiting
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Process has finished waiting.");
    }

    @Override
    public void exit(PCB job) {
        System.out.println("Process " + job.id + " has exited.");
        // Simulate process termination
    }

    @Override
    public void abort(PCB job) {
        System.out.println("Process " + job.id + " has been aborted.");
        // Simulate aborting the job
    }

    // Helper function to generate a unique process ID
    private int generateNewId() {
        return new Random().nextInt(1000);
    }
}
