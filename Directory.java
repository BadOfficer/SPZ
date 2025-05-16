import java.util.HashMap;
import java.util.Map;

public class Directory {
    public String name;
    public Map<String, DirectoryEntry> entries = new HashMap<>();
    public Directory parent;

    public Directory(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }
}
