public interface SystemCall {
    void fork();                  // Simulate process creation
    void waitt();                  // Simulate waiting for a process to finish
    void exit(PCB job);           // Simulate process exit
    void abort(PCB job);          // Simulate aborting a process
}
