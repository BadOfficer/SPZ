public class PhysicalPage {
    public final int number;
    public int ownerProcessId = -1;
    public int virtualPageNumber = -1;

    public PhysicalPage(int number) {
        this.number = number;
    }

    public boolean isFree() {
        return ownerProcessId == -1;
    }
}
