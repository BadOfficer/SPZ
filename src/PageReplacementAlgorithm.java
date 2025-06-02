import java.util.List;

public interface PageReplacementAlgorithm {
    PhysicalPage selectPageToReplace(List<PhysicalPage> physicalPages, Kernel kernel);
}
