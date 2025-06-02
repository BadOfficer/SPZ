public class PageTableEntry {
    public boolean present = false;
    public boolean referenced = false;
    public boolean modified = false;
    public int ppn = -1;
    public long lastAccessTime = 0;
}
