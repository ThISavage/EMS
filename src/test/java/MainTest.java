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
            writer.println("A");
            writer.println("Б");
            writer.println("Я");
        }
        Assertions.assertEquals(false, Main.isSorted(new File("src/test/resources/unsorted.txt")));
        Assertions.assertEquals(true, Main.isSorted(file));
        file.delete();
    }
}
