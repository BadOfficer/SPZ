public class Main {
    public static void main(String[] args) {
        FileSystem fs = new FileSystem();
        fs.mkfs(10);

        System.out.print("\n");
        fs.mkdir("dir 1");
        fs.create("file 1");
        fs.ls();

        System.out.print("\n");
        fs.cd("dir 1");
        fs.mkdir("dir 2");
        fs.ls();

        System.out.print("\n");
        fs.cd("dir 3");

        System.out.print("\n");
        fs.symlink("dir 2", "dir2_symlink");
        fs.cd("dir2_symlink");
        fs.ls();

        System.out.print("\n");
        fs.cd("..");
        fs.stat("dir2_symlink");

        System.out.print("\n");
        fs.cd("..");
        fs.cd("..");

        System.out.print("\n");
        fs.mkdir("dir 3");
        fs.rmdir("dir 3");

        System.out.print("\n");
        fs.rmdir("dir 2");

        System.out.print("\n");
        fs.symlink("dir 2", "dir2_symlink");

        System.out.print("\n");
        fs.stat("dir 1");

        System.out.print("\n");
        int fd1 = fs.open("file 1");
        fs.write(fd1, 100);
        fs.read(fd1, 100);

        System.out.print("\n");
        fs.truncate("file 1", 30);
        fs.seek(fd1, 15);
        fs.read(fd1, 50);

        System.out.print("\n");
        fs.stat("file 1");
    }
}
