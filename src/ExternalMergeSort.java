import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Класс ExternalMergeSort предоставляет метод для сортировки чисел во входном файле и сохранения отсортированных данных в выходном файле.
 */
public class ExternalMergeSort {
    private static final int BLOCK_SIZE = 1024; // Размер блока на который дробится исходный файл
    private static final String OUT_FILE_NAME = "src" + File.separator + "resources" + File.separator + "sorted.txt"; // Путь до отсортированного файла
    private static final String PREFIX = "block";
    private static final String SUFFIX = ".txt";

    /**
     * Сортирует числа во входном файле и сохраняет отсортированные данные в выходном файле.
     *
     * @param source Входной файл с числами, который нужно отсортировать.
     * @return Файл с отсортированными числами.
     * @throws IOException Если возникает ошибка ввода-вывода при работе с файлами.
     */
    public static File sortFile(File source) throws IOException {
        List<String> blocks = splitIntoBlocks(source); // Разбиение всех данных на блоки удовлетворительного размера
        combiningBlocks(blocks); // Слияние всех блоков в 1
        return saveToFile(blocks); // Запись в файл
    }

    /**
     * Разбивает содержимое файла на блоки, сортирует их и сохраняет во временные файлы.
     *
     * @param source Файл, содержимое которого нужно разбить на блоки.
     * @return Список временных файлов, в которых хранятся отсортированные блоки.
     * @throws IOException Если произошла ошибка при разбиении файла на блоки или при записи во временные файлы.
     */
    private static List<String> splitIntoBlocks(File source) throws IOException {
        List<Long> currentBlock = new ArrayList<>(BLOCK_SIZE);
        List<String> blocks = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(source); Scanner scanner = new Scanner(fileInputStream)) {
            while (scanner.hasNextLong()) {
                Long valueFromFile = scanner.nextLong();
                currentBlock.add(valueFromFile);
                if (currentBlock.size() == BLOCK_SIZE) {
                    Collections.sort(currentBlock);
                    blocks.add(writeToTempFile(currentBlock));
                    currentBlock.clear();
                }
            }
            if (!currentBlock.isEmpty()) {
                Collections.sort(currentBlock);
                blocks.add(writeToTempFile(currentBlock));
            }
        }
        return blocks;
    }

    /**
     * Объединяет блоки в списке до тех пор, пока не останется только один блок.
     *
     * @param allBlocks Список блоков, которые нужно объединить.
     * @throws IOException Если произошла ошибка при объединении блоков.
     */
    private static void combiningBlocks(List<String> allBlocks) throws IOException {
        while (allBlocks.size() > 1) {
            List<String> mergedBlocks = new ArrayList<>();
            for (int i = 0; i < allBlocks.size(); i += 2) {
                String mergedBlock = sortAndMergeTwoBlocks(allBlocks.get(i), i + 1 < allBlocks.size() ? allBlocks.get(i + 1) : null);
                mergedBlocks.add(mergedBlock);
            }
            allBlocks = mergedBlocks;
        }
    }



    /**
     * Сохраняет значения из списка в файл и возвращает созданный файл.
     *
     * @param outBlock Список строк, значения которых нужно записать в файл.
     * @return Файл, в который были записаны значения из списка.
     * @throws IOException Если произошла ошибка при чтении или записи файла.
     */
    private static File saveToFile(List<String> outBlock) throws IOException {
        File output = new File(OUT_FILE_NAME);
        try (FileInputStream fileInputStream = new FileInputStream(outBlock.get(0)); Scanner scanner = new Scanner(fileInputStream)) {
            try (PrintWriter pw = new PrintWriter(OUT_FILE_NAME)) {
                while (scanner.hasNextLong()) {
                    Long stringFromFile = scanner.nextLong();
                    pw.println(stringFromFile);
                }
                pw.flush();
            }
        }
        return output;
    }

    /**
     * Записывает список чисел во временный файл и возвращает его путь.
     *
     * @param block Список чисел, который нужно записать в файл.
     * @return Путь к временному файлу с записанными числами.
     * @throws IOException Если возникает ошибка ввода-вывода при создании временного файла.
     */
    private static String writeToTempFile(List<Long> block) throws IOException {
        File file = File.createTempFile(PREFIX, SUFFIX);
        file.deleteOnExit();
        try (PrintWriter pw = new PrintWriter(file)) {
            for (Long element : block) {
                pw.println(element);
            }
            pw.flush();
        }
        return file.getAbsolutePath();
    }

    /**
     * Объединяет два отсортированных блока в один отсортированный блок и возвращает путь к временному файлу с объединенными данными.
     *
     * @param firstBlockPath  Путь к первому блоку.
     * @param secondBlockPath Путь ко второму блоку.
     * @return Путь к временному файлу с объединенными данными.
     * @throws IOException Если возникает ошибка ввода-вывода при объединении блоков и создании временного файла.
     */
    private static String sortAndMergeTwoBlocks(String firstBlockPath, String secondBlockPath) throws IOException {
        if (secondBlockPath == null) {
            return firstBlockPath;
        }
        List<Long> mergedBlock = new ArrayList<>(2 * BLOCK_SIZE);
        try (FileInputStream fileInputStream = new FileInputStream(firstBlockPath); Scanner firstBlock = new Scanner(fileInputStream); FileInputStream fileInputStream2 = new FileInputStream(secondBlockPath); Scanner secondBlock = new Scanner(fileInputStream2)) {
            Long value1 = firstBlock.hasNextLong() ? firstBlock.nextLong() : null;
            Long value2 = secondBlock.hasNextLong() ? secondBlock.nextLong() : null;
            while (value1 != null || value2 != null) {
                if (value1 == null) {
                    mergedBlock.add(value2);
                    value2 = secondBlock.hasNextLong() ? secondBlock.nextLong() : null;
                } else if (value2 == null) {
                    mergedBlock.add(value1);
                    value1 = firstBlock.hasNextLong() ? firstBlock.nextLong() : null;
                } else if (value1 < value2) {
                    mergedBlock.add(value1);
                    value1 = firstBlock.hasNextLong() ? firstBlock.nextLong() : null;
                } else {
                    mergedBlock.add(value2);
                    value2 = secondBlock.hasNextLong() ? secondBlock.nextLong() : null;
                }
            }
            String mergedBlockPath = writeToTempFile(mergedBlock);
            mergedBlock.clear();
            return mergedBlockPath;
        }
    }
}