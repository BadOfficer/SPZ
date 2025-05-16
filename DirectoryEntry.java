public class DirectoryEntry {
    public String name;
    public int descriptorIndex;
    public boolean isHasLinks = true;
    public boolean isDirectory;
    public Directory subDirectory;
    String symlinkContent = null;

    public DirectoryEntry(String name, int descriptorIndex, boolean isDirectory) {
        this.name = name;
        this.descriptorIndex = descriptorIndex;
        this.isDirectory = isDirectory;
        if (isDirectory) {
            this.subDirectory = new Directory(name, null);
        }
    }
}
