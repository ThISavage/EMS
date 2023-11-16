import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ems.ExternalMergeSort;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ExternalMergeSortTest {

    @Test
    void testSplitIntoBlocks() throws IOException {
        File source = new File("src/test/resources/unsorted.txt");
        List<String> blocks = ExternalMergeSort.splitIntoBlocks(source);
        Assertions.assertEquals(100, blocks.size());
    }

    @Test
    void testCombiningBlocks() throws IOException {
        File file = new File("src/test/resources/unsorted.txt");
        List<String> mergedBlocks = ExternalMergeSort.combiningBlocks(ExternalMergeSort.splitIntoBlocks(file));
        Assertions.assertEquals(1, mergedBlocks.size());
    }

    @Test
    void testSaveToFile() throws IOException {
        List<String> testList = new ArrayList<>(Arrays.asList("src/test/resources/unsorted.txt"));
        Assertions.assertEquals(ExternalMergeSort.saveToFile(testList).getPath(),"src"+File.separator+"main"+File.separator+"resources"+File.separator+"sorted.txt");
    }

    @Test
    void testSortFile() throws IOException {
        File source = new File("src/test/resources/unsorted.txt");
        File sortedFile = ExternalMergeSort.sortFile(source);
        Assertions.assertEquals("sorted.txt", sortedFile.getName());
        Assertions.assertEquals(50371, sortedFile.length());
    }


}