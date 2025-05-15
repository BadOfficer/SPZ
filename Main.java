public class Main {
    public static void main(String[] args) {
        FileSystem fs = new FileSystem();
        fs.mkfs(10);
        fs.create("file1");

        System.out.print("\n");
        fs.ls();
        fs.stat("file1");

        System.out.print("\n");
        int fd1 = fs.open("file1");

        System.out.print("\n");
        fs.write(fd1, 100);
        fs.stat("file1");
        fs.seek(fd1, 0);
        fs.read(fd1, 50);

        System.out.print("\n");
        fs.write(fd1, 2700);
        fs.stat("file1");

        System.out.print("\n");
        fs.link("file1", "file2");
        fs.stat("file1");

        System.out.print("\n");
        fs.unlink("file2");
        fs.stat("file1");

        System.out.print("\n");
        fs.truncate("file1", 50);
        fs.stat("file1");

        System.out.print("\n");
        fs.create("file2");
        int fd2 = fs.open("file2");
        fs.ls();

        fs.write(fd2, 100);

        System.out.print("\n");
        fs.seek(fd2, 50);
        fs.stat("file2");

        System.out.print("\n");
        fs.close(fd1);
        fs.close(fd2);

        fs.ls();

        fs.write(fd1, 100);
    }
}
