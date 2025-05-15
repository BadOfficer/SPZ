public class DirectoryEntry {
    public String name;
    public int descriptorIndex;
    public boolean isHasLinks = true;

    public DirectoryEntry(String name, int descriptorIndex) {
        this.name = name;
        this.descriptorIndex = descriptorIndex;
    }
}
