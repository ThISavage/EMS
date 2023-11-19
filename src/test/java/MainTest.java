import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ems.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MainTest {

    @Test
    void testIsSorted() throws IOException {
        File file = new File("src/test/resources/test_sorted");
        try(PrintWriter writer = new PrintWriter(new FileWriter(file))){
            writer.println("A:15");
            writer.println("B:12");
            writer.println("C:10");
        }
        Assertions.assertFalse(Main.isSorted(new File("src/test/resources/unsorted.txt")));
        Assertions.assertTrue(Main.isSorted(file));
        file.delete();
    }
}
