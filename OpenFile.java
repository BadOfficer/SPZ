public class OpenFile {
    public int descriptorIndex;
    public int offset;

    public OpenFile(int descriptorIndex) {
        this.descriptorIndex = descriptorIndex;
        this.offset = 0;
    }
}
