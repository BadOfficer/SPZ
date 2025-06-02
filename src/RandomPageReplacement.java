import java.util.List;
import java.util.Random;

public class RandomPageReplacement implements PageReplacementAlgorithm {
    private final Random random = new Random();

    @Override
    public PhysicalPage selectPageToReplace(List<PhysicalPage> physicalPages, Kernel kernel) {
        PhysicalPage victim = physicalPages.get(random.nextInt(physicalPages.size()));
        System.out.printf(Config.YELLOW_COLOR + "Random replacement chose PPN %d (owner P%d VPN %d)\n",
                victim.number, victim.ownerProcessId, victim.virtualPageNumber, Config.RESET_COLOR);
        return victim;
    }
}
