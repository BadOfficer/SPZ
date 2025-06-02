import java.util.ArrayList;
import java.util.List;

public class PageTable {
    private final List<PageTableEntry> entries;

    public PageTable(int size) {
        entries = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            entries.add(new PageTableEntry());
        }
    }

    public PageTableEntry getEntry(int vpn) {
        return entries.get(vpn);
    }

    public int size() {
        return entries.size();
    }
}
