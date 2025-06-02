import java.util.*;

public class Process {
    private final int id;
    private final PageTable pageTable;
    private Set<Integer> workingSet = new HashSet<>();
    private final List<Integer> workingSetList = new ArrayList<>();
    private Random random = new Random();
    private int pageFaultCount = 0;

    public Process(int id) {
        this.id = id;
        this.pageTable = new PageTable(Config.ADDRESS_SPACE_SIZE);
        initWorkingSet();
    }

    public int getId() {
        return id;
    }

    public PageTable getPageTable() {
        return pageTable;
    }

    public int getNextPageAccess() {
        if (random.nextDouble() < Config.WORKING_SET_ACCESS_PROB && !workingSetList.isEmpty()) {
            return workingSetList.get(random.nextInt(workingSetList.size()));
        } else {
            return random.nextInt(Config.ADDRESS_SPACE_SIZE);
        }
    }

    public void updateWorkingSet() {
        workingSet.clear();
        workingSetList.clear();
        while (workingSet.size() < Config.WORKING_SET_SIZE) {
            int page = random.nextInt(Config.ADDRESS_SPACE_SIZE);
            if (workingSet.add(page)) {
                workingSetList.add(page);
            }
        }
    }

    public void incrementPageFaultCount() {
        pageFaultCount++;
    }

    public int getPageFaultCount() {
        return pageFaultCount;
    }

    private void initWorkingSet() {
        updateWorkingSet();
    }
}
