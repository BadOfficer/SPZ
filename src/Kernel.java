import java.util.ArrayList;
import java.util.List;

public class Kernel {
    private final List<PhysicalPage> physicalPages = new ArrayList<>();
    private final List<Process> processes = new ArrayList<>();
    private final MMU mmu;
    private final PageReplacementAlgorithm pageReplacementAlgorithm;

    public Kernel(PageReplacementAlgorithm algorithm) {
        for (int i = 0; i < Config.NUM_PHYSICAL_PAGES; i++) {
            physicalPages.add(new PhysicalPage(i));
        }
        this.pageReplacementAlgorithm = algorithm;
        mmu = new MMU(this);
    }

    public void handlePageFault(Process process, int vpn) {
        process.incrementPageFaultCount();
        PhysicalPage freePage = findFreePage();
        if (freePage == null) {
            freePage = pageReplacementAlgorithm.selectPageToReplace(physicalPages, this);

            Process victimProcess = getProcessById(freePage.ownerProcessId);
            if (victimProcess != null) {
                PageTableEntry victimEntry = victimProcess.getPageTable().getEntry(freePage.virtualPageNumber);
                victimEntry.present = false;
            }
        }

        freePage.ownerProcessId = process.getId();
        freePage.virtualPageNumber = vpn;

        PageTableEntry pte = process.getPageTable().getEntry(vpn);
        pte.present = true;
        pte.ppn = freePage.number;
    }

    private PhysicalPage findFreePage() {
        for (PhysicalPage page : physicalPages) {
            if (page.isFree()) {
                return page;
            }
        }
        return null;
    }

    public Process getProcessById(int id) {
        return processes.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public MMU getMMU() {
        return mmu;
    }

    public List<PhysicalPage> getPhysicalPages() {
        return physicalPages;
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public void addProcess(Process process) {
        processes.add(process);
    }

    public long getSimulatedTime() {
        return mmu.getSimulatedTime();
    }
}
