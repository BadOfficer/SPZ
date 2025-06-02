import java.util.List;

public class WSClockPageReplacement implements PageReplacementAlgorithm {
    private int clockHand = 0;
    private static final long TAU = 5;

    @Override
    public PhysicalPage selectPageToReplace(List<PhysicalPage> physicalPages, Kernel kernel) {
        int n = physicalPages.size();
        long currentTime = kernel.getSimulatedTime();

        for (int i = 0; i < n; i++) {
            PhysicalPage page = physicalPages.get(clockHand);
            Process process = kernel.getProcessById(page.ownerProcessId);

            if (process != null) {
                PageTableEntry pte = process.getPageTable().getEntry(page.virtualPageNumber);

                if (pte.referenced) {
                    pte.referenced = false;
                } else {
                    long age = currentTime - pte.lastAccessTime;
                    if (!pte.modified && age > TAU) {
                        PhysicalPage result = page;
                        advanceHand(n);
                        System.out.printf(Config.YELLOW_COLOR + "WSClock replacement chose PPN %d (owner P%d VPN %d)\n",
                                result.number, result.ownerProcessId, result.virtualPageNumber, Config.RESET_COLOR);
                        return result;
                    }

                    if (pte.modified) {
                        pte.modified = false;
                    }
                }
            }
            advanceHand(n);
        }

        PhysicalPage fallback = physicalPages.get(clockHand);
        advanceHand(n);
        System.out.printf(Config.RED_COLOR + "WSClock fallback chose PPN %d (owner P%d VPN %d)\n",
                fallback.number, fallback.ownerProcessId, fallback.virtualPageNumber, Config.RESET_COLOR);
        return fallback;
    }

    private void advanceHand(int n) {
        clockHand = (clockHand + 1) % n;
    }
}
