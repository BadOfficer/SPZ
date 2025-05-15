import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class FileSystem {
    public static final String RESET_COLOR = "\u001B[0m";
    public static final String RED_COLOR = "\u001B[31m";
    public static final String GREEN_COLOR = "\u001B[32m";
    public static final String YELLOW_COLOR = "\u001B[33m";

    private static final int BLOCK_SIZE = 256;
    public static final int DIRECT_BLOCKS = 10;
    private static final int MAX_FILES = 64;

    FileDescriptor[] descriptors = new FileDescriptor[MAX_FILES];
    Map<String, DirectoryEntry> directory = new HashMap<>();
    Map<Integer, OpenFile> openFiles = new HashMap<>();
    int nextFd = 0;

    public FileSystem() {
        Arrays.fill(descriptors, null);
    }

    public void mkfs(int n) {
        Arrays.fill(descriptors, new FileDescriptor());
        directory.clear();
        openFiles.clear();
        nextFd = 0;
        System.out.printf(GREEN_COLOR + "\nFile system initialized with %d descriptors" + RESET_COLOR, n);
    }

    public void create(String filename) {
        DirectoryEntry existingEntry = directory.get(filename);
        if (existingEntry != null && existingEntry.isHasLinks) {
            System.out.printf(RED_COLOR + "\nError: File already exists: %s" + RESET_COLOR, filename);
            return;
        }

        for (int i = 0; i < MAX_FILES; i++) {
            if (descriptors[i] != null && descriptors[i].linkCount == 0) {
                descriptors[i] = new FileDescriptor();
                descriptors[i].linkCount = 1;
                directory.put(filename, new DirectoryEntry(filename, i));
                System.out.printf(GREEN_COLOR + "\nFile created: %s" + RESET_COLOR, filename);
                return;
            }
        }
        System.out.println(RED_COLOR + "Error: No free descriptors" + RESET_COLOR);
    }

    public int open(String filename) {
        DirectoryEntry entry = directory.get(filename);
        if (entry == null || !entry.isHasLinks) {
            System.out.println(RED_COLOR + "\nError: File not found" + RESET_COLOR);
            return -1;
        }
        int fd = nextFd++;
        openFiles.put(fd, new OpenFile(entry.descriptorIndex));
        System.out.printf(YELLOW_COLOR + "\nFile opened: %s as fd = %d" + RESET_COLOR, filename, fd);
        return fd;
    }

    public void close(int fd) {
        if (openFiles.remove(fd) != null) {
            System.out.printf(GREEN_COLOR + "\nFile descriptor %d closed" + RESET_COLOR, fd);
        } else {
            System.out.printf(RED_COLOR + "\nInvalid fd: %d" + RESET_COLOR, fd);
        }
    }

    public void seek(int fd, int offset) {
        OpenFile of = openFiles.get(fd);
        if (of != null) {
            of.offset = offset;
            System.out.printf(GREEN_COLOR + "\nSeek set for fd = %d to offset = %d" + RESET_COLOR, fd, of.offset);
        }
    }

    public void write(int fd, int size) {
        OpenFile of = openFiles.get(fd);
        if (of == null) {
            System.out.printf(RED_COLOR + "\nInvalid fd: %d" + RESET_COLOR, fd);
            return;
        }
        FileDescriptor desc = descriptors[of.descriptorIndex];

        if (of.offset + size > DIRECT_BLOCKS * BLOCK_SIZE) {
            System.out.printf(RED_COLOR + "\nError: File too large. Max size is %d bytes" + RESET_COLOR, DIRECT_BLOCKS * BLOCK_SIZE);
            return;
        }

        of.offset += size;
        desc.size = Math.max(desc.size, of.offset);
        System.out.printf(GREEN_COLOR + "\nWrite of %d bytes to fd = %d" + RESET_COLOR, size, fd);
    }

    public void read(int fd, int size) {
        OpenFile of = openFiles.get(fd);
        if (of == null) {
            System.out.printf(RED_COLOR + "\nInvalid fd: %d" + RESET_COLOR, fd);
            return;
        }
        FileDescriptor desc = descriptors[of.descriptorIndex];

        int remaining = desc.size - of.offset;
        int toRead = Math.min(size, Math.max(0, remaining));

        of.offset += toRead;
        System.out.printf(YELLOW_COLOR + "\nRead of %d bytes from fd= %d" + RESET_COLOR, toRead, fd);
    }

    public void ls() {
        System.out.print(YELLOW_COLOR + "\nFiles: " + RESET_COLOR);
        for (DirectoryEntry entry : directory.values()) {
            if (entry.isHasLinks)
                System.out.printf(YELLOW_COLOR + "\n\t%s -> descriptor %d" + RESET_COLOR,entry.name, entry.descriptorIndex);
        }
    }

    public void stat(String filename) {
        DirectoryEntry entry = directory.get(filename);
        if (entry != null && entry.isHasLinks) {
            FileDescriptor desc = descriptors[entry.descriptorIndex];
            System.out.printf(YELLOW_COLOR + "\nFilename: %s, Type: %s, Size: %d, Links: %d" + RESET_COLOR, filename, desc.fileType, desc.size, desc.linkCount);
        } else {
            System.out.println(RED_COLOR + "File not found" + RESET_COLOR);
        }
    }

    public void link(String filename1, String filename2) {
        DirectoryEntry e1 = directory.get(filename1);
        if (e1 == null || !e1.isHasLinks) {
            System.out.printf(RED_COLOR + "\nError: Cannot create link. Source file not found: %s" + RESET_COLOR, filename1);
            return;
        }
        directory.put(filename2, new DirectoryEntry(filename2, e1.descriptorIndex));
        descriptors[e1.descriptorIndex].linkCount++;
        System.out.printf(GREEN_COLOR + "\nHard Link created: %s -> %s (descriptor %d)" + RESET_COLOR, filename2, filename1, e1.descriptorIndex);
    }

    public void unlink(String filename) {
        DirectoryEntry e = directory.get(filename);
        if (e == null || !e.isHasLinks) {
            System.out.printf(RED_COLOR + "\nError: Cannot unlink. File not found: %s" + RESET_COLOR, filename);
            return;
        }
        descriptors[e.descriptorIndex].linkCount--;
        e.isHasLinks = false;
        if (descriptors[e.descriptorIndex].linkCount == 0) {
            descriptors[e.descriptorIndex] = null;
        }
        System.out.printf(GREEN_COLOR + "\nUnlinked file %s" + RESET_COLOR, filename);
    }

    public void truncate(String filename, int size) {
        DirectoryEntry e = directory.get(filename);
        if (e == null || !e.isHasLinks) return;
        FileDescriptor desc = descriptors[e.descriptorIndex];
        if (size > DIRECT_BLOCKS * BLOCK_SIZE) {
            System.out.printf(RED_COLOR + "\nError: Cannot truncate beyond max file size (%d)" + RESET_COLOR, DIRECT_BLOCKS * BLOCK_SIZE);
            return;
        }
        desc.size = size;
        System.out.printf(GREEN_COLOR + "\nTruncated file %s to size %d" + RESET_COLOR, filename, size);
    }
}
