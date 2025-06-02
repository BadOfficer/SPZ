public class MMU {
    private final Kernel kernel;
    public static int pageFaultCount = 0;
    private long simulatedTime = 0;

    public MMU(Kernel kernel) {
        this.kernel = kernel;
    }

    public void accessPage(Process process, int vpn, boolean write) {
        simulatedTime += 1;
        PageTableEntry pte = process.getPageTable().getEntry(vpn);

        if (!pte.present) {
            pageFaultCount++;
            kernel.handlePageFault(process, vpn);
            pte = process.getPageTable().getEntry(vpn);
        }

        pte.referenced = true;
        if (write) {
            pte.modified = true;
        }
        pte.lastAccessTime = simulatedTime;
    }

    public long getSimulatedTime() {
        return simulatedTime;
    }

}
