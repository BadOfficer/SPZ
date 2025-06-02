import java.util.ArrayList;
import java.util.List;

public class Simulator {
    public static void main(String[] args) {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1));
        processes.add(new Process(2));
        processes.add(new Process(3));

        runSimulation(new WSClockPageReplacement(), "WSClock", processes);
        runSimulation(new RandomPageReplacement(), "Random", processes);
    }

    private static void runSimulation(PageReplacementAlgorithm algorithm, String algoName, List<Process> processes) {
        Kernel kernel = new Kernel(algorithm);

        for (Process p : processes) {
            kernel.addProcess(new Process(p.getId()));
        }

        List<Process> kernelProcesses = kernel.getProcesses();
        int numProcesses = kernelProcesses.size();

        for (int step = 0; step < Config.NUM_ACCESSES; step++) {
            Process currentProcess = kernelProcesses.get(step % numProcesses);

            int vpn = currentProcess.getNextPageAccess();
            boolean write = Math.random() < Config.WRITE_PROBABILITY;

            kernel.getMMU().accessPage(currentProcess, vpn, write);

            if (step % Config.WORKING_SET_UPDATE_INTERVAL == 0) {
                currentProcess.updateWorkingSet();
            }
        }

        System.out.println(Config.GREEN_COLOR + "\n=== Simulation Finished: " + algoName + " ===");
        System.out.println("Total page faults: " + MMU.pageFaultCount);

        System.out.println("Page faults per process:");
        for (Process p : kernel.getProcesses()) {
            System.out.printf("\tProcess %d: %d page faults\n", p.getId(), p.getPageFaultCount());
        }

        long usedPages = kernel.getPhysicalPages().stream().filter(p -> !p.isFree()).count();
        System.out.println("Physical pages: " + kernel.getPhysicalPages().size());
        System.out.println("Used physical pages: " + usedPages);
        System.out.println("Number of processes: " + kernel.getProcesses().size());
        System.out.println("\n" + Config.RESET_COLOR);

        MMU.pageFaultCount = 0;
    }

}
