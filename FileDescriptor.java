import java.util.Arrays;

public class FileDescriptor {
    public FileType fileType;
    public int linkCount;
    public int size;
    public int[] directBlocks = new int[FileSystem.DIRECT_BLOCKS];

    public FileDescriptor() {
        this.fileType = FileType.REGULAR;
        this.linkCount = 0;
        this.size = 0;
        Arrays.fill(directBlocks, -1);
    }
}
