import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ems.ExternalMergeSort;
import ru.ems.enums.MainFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;


class ExternalMergeSortTest {

    @Test
    void testSplitIntoBlocks() throws IOException {
        ExternalMergeSort.splitIntoBlocks();
        Assertions.assertTrue(MainFile.BLOCKS.getFile().length() > 0);
        Files.delete(MainFile.BLOCKS.getFile().toPath());
    }

    @Test
    void testCombiningBlocks() throws IOException {
        ExternalMergeSort.splitIntoBlocks();
        ExternalMergeSort.combiningBlocks();
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(MainFile.BLOCKS.getFile()))) {
            while (reader.readLine() != null) {
                lineCount++;
            }
        }
        Assertions.assertEquals(1, lineCount);
        Files.delete(MainFile.BLOCKS.getFile().toPath());
    }

    @Test
    void testSortFile() throws IOException {
        ExternalMergeSort.sort();
        Assertions.assertEquals("sorted.txt", MainFile.SORTED.getFile().getName());
        Assertions.assertEquals(MainFile.UNSORTED.getFile().length(), MainFile.SORTED.getFile().length());
        Files.delete(MainFile.SORTED.getFile().toPath());
    }
}