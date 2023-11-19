package ru.ems;

import ru.ems.enums.MainFile;
import ru.ems.enums.TempFile;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static ru.ems.Main.BLOCK_SIZE;
import static ru.ems.Main.comparator;

/**
 * Класс ExternalMergeSort предоставляет методы для внешней сортировки данных, используя алгоритм сортировки слиянием.
 * Данный класс предназначен для сортировки больших объемов данных, не умещающихся в оперативной памяти целиком.
 */
public class ExternalMergeSort {
    /**
     * Выполняет внешнюю сортировку данных.
     *
     * @throws IOException Возникает при ошибке ввода/вывода в процессе сортировки.
     */
    public static void sort() throws IOException {
        splitIntoBlocks(); // Разбиение всех данных на блоки удовлетворительного размера
        combiningBlocks(); // Слияние всех блоков в один
        saveToOutFile(); // Запись в итоговый файл
    }

    /**
     * Разбивает исходные данные на блоки заданного размера и сортирует каждый блок.
     *
     * @throws IOException Возникает при ошибке ввода/вывода в процессе разбиения данных.
     */
    public static void splitIntoBlocks() throws IOException {
        List<String> currentBlock = new ArrayList<>(BLOCK_SIZE);
        try (BufferedReader reader = new BufferedReader(new FileReader(MainFile.UNSORTED.getFile()))) {
            String valueFromFile;
            while ((valueFromFile = reader.readLine())!=null) {
                currentBlock.add(valueFromFile);
                if (currentBlock.size() == BLOCK_SIZE) {
                    currentBlock.sort(comparator);
                    String pathToCurrentBlock = addToTempFile(currentBlock);
                    addToMainFile(pathToCurrentBlock);
                    currentBlock.clear();
                }
            }
            if (!currentBlock.isEmpty()) {
                currentBlock.sort(comparator);
                String pathToCurrentBlock = addToTempFile(currentBlock);
                addToMainFile(pathToCurrentBlock);
                currentBlock.clear();
            }
        }
    }


    /**
     * Выполняет слияние отсортированных блоков данных в один файл.
     *
     * @throws IOException Возникает при ошибке ввода/вывода в процессе слияния блоков.
     */
    public static void combiningBlocks() throws IOException {
        String path = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(MainFile.BLOCKS.getFile()))) {
            String s1;
            String s2;
            while ((s1 = reader.readLine()) != null) {
                if (s1.equals(path)) {
                    reader.readLine();
                }
                s2 = reader.readLine();
                path = sortAndMergeTwoBlocks(s1, s2);
            }
        }finally {
            Files.delete(MainFile.BLOCKS.getFile().toPath());
        }
        addToMainFile(path);
    }

    /**
     * Сортирует и объединяет два блока из файлов и возвращает путь к созданному объединенному файлу.
     *
     * @param firstBlockPath  Путь к первому блоку для объединения.
     * @param secondBlockPath Путь ко второму блоку для объединения.
     * @return Путь к созданному объединенному временному файлу.
     * @throws IOException Если произошла ошибка ввода/вывода при чтении или записи файлов.
     */
    private static String sortAndMergeTwoBlocks(String firstBlockPath, String secondBlockPath) throws IOException {
        if (secondBlockPath == null) {
            return firstBlockPath;
        }
        try (BufferedReader firstBlockReader = new BufferedReader(new FileReader(firstBlockPath));
             BufferedReader secondBlockReader = new BufferedReader(new FileReader(secondBlockPath))) {
            String s1 = firstBlockReader.readLine();
            String s2 = secondBlockReader.readLine();
            while (s1 != null || s2 != null) {
                if (s1 == null) {
                    addToMergeFile(s2);
                    s2 = secondBlockReader.readLine();
                } else if (s2 == null) {
                    addToMergeFile(s1);
                    s1 = firstBlockReader.readLine();
                } else {
                    int comparisonResult = comparator.compare(s1, s2);
                    if (comparisonResult < 0) {
                        addToMergeFile(s1);
                        s1 = firstBlockReader.readLine();
                    } else {
                        addToMergeFile(s2);
                        s2 = secondBlockReader.readLine();
                    }
                }
            }
            String mergedBlockPath = writeToTempFile();
            addToMainFile(mergedBlockPath);
            return mergedBlockPath;
        }finally {
            new File(firstBlockPath).deleteOnExit();
            new File(secondBlockPath).deleteOnExit();
            Files.delete(MainFile.MERGE.getFile().toPath());
        }
    }

    /**
     * Сохраняет отсортированные данные в итоговый файл.
     *
     * @throws IOException Возникает при ошибке ввода/вывода в процессе записи в файл.
     */
    private static void saveToOutFile() throws IOException {
        try (BufferedReader file = new BufferedReader(new FileReader(MainFile.BLOCKS.getFile()))) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file.readLine()));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(MainFile.SORTED.getFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }

            }
        }finally {
            Files.delete(MainFile.BLOCKS.getFile().toPath());
        }
    }

    /**
     * Добавляет список строк во временный файл и возвращает путь к созданному файлу.
     *
     * @param block Список строк для добавления в файл.
     * @return Путь к созданному временному файлу.
     * @throws IOException Если произошла ошибка ввода/вывода при создании файла или записи в него.
     */
    protected static String addToTempFile(List<String> block) throws IOException {
        File file = File.createTempFile(TempFile.BLOCK_TEMP_FILE.getPrefix(), TempFile.BLOCK_TEMP_FILE.getSuffix());
        try (PrintWriter pw = new PrintWriter(file)) {
            for (String element : block) {
                pw.println(element);
            }
            pw.flush();
        }
        return file.getAbsolutePath();
    }

    /**
     * Записывает содержимое основного файла во временный файл и возвращает путь к созданному файлу.
     *
     * @return Путь к созданному временному файлу.
     * @throws IOException Если произошла ошибка ввода/вывода при создании файла или записи в него.
     */
    protected static String writeToTempFile() throws IOException {
        File out = File.createTempFile(TempFile.BLOCK_TEMP_FILE.getPrefix(), TempFile.BLOCK_TEMP_FILE.getSuffix());
        try (BufferedReader reader = new BufferedReader(new FileReader(MainFile.MERGE.getFile()));
             BufferedWriter writer = new BufferedWriter(new FileWriter(out))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
        return out.getAbsolutePath();
    }

    /**
     * Добавляет элемент в файл слияния.
     *
     * @param element Элемент для добавления в основной файл.
     * @throws IOException Если произошла ошибка ввода/вывода при записи в файл.
     */
    protected static void addToMergeFile(String element) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MainFile.MERGE.getFile(), true))) {
            writer.write(element);
            writer.newLine();
        }
    }

    /**
     * Добавляет путь к файлу в основной файл.
     *
     * @param path Путь к файлу для добавления в основной файл.
     * @throws IOException Если произошла ошибка ввода/вывода при записи в файл.
     */
    protected static void addToMainFile(String path) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MainFile.BLOCKS.getFile(), true))) {
            writer.write(path);
            writer.newLine();
        }
    }
}