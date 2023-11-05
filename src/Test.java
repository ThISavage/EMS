import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Test {
    private static final String UNSORTED_FILE = "src" + File.separator + "resources" + File.separator + "unsorted.txt"; // Путь до сгенерированного неотсортированного файла
    private static final int FILE_SIZE = 10000; // Количество строк в генерируемом файле
    public static void main(String[] args) throws IOException {
        System.out.println("Start: " + new Date());
        File source = generate(UNSORTED_FILE, FILE_SIZE);
        System.out.println("Файл сгенерирован: " + new Date());
        System.out.println("Проверка после генерации: " + new Date() + " = " + isSorted(source));
        File sortedFile = ExternalMergeSort.sortFile(source);
        System.out.println("Отсортированный файл сгенерирован: " + new Date());
        System.out.println("Проверка после сортировки: " + new Date() + " = " + isSorted(sortedFile));
        System.out.println("Stop: " + new Date());
    }

    /**
     * Генерирует файл с указанным именем и заданным количеством случайных чисел.
     *
     * @param name  Имя файла, который будет создан.
     * @param count Количество случайных чисел, которые будут записаны в файл.
     * @return Сгенерированный файл.
     * @throws IOException Если возникает ошибка ввода-вывода при создании файла.
     */
    public static File generate(String name, int count) throws IOException {
        Random random = new Random();
        File file = new File(name);
        try (PrintWriter pw = new PrintWriter(file)) {
            for (int i = 0; i < count; i++) {
                pw.println(random.nextLong());
            }
            pw.flush();
        }
        return file;
    }

    /**
     * Проверяет, отсортированы ли числа в файле в порядке возрастания.
     *
     * @return true, если числа отсортированы в порядке возрастания, в противном случае false.
     */
    public static boolean isSorted(File file) throws IOException {
        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            long prev = Long.MIN_VALUE;
            while (scanner.hasNextLong()) {
                long current = scanner.nextLong();
                if (current < prev) {
                    return false;
                } else {
                    prev = current;
                }
            }
            return true;
        }
    }


}
