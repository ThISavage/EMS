package ru.ems;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Класс ExternalMergeSort предоставляет метод для сортировки чисел во входном файле и сохранения отсортированных данных в выходном файле.
 */
public class ExternalMergeSort {
    private static final int BLOCK_SIZE = 10; // Размер блока на который дробится исходный файл
    private static final String OUT_FILE_NAME = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "sorted.txt"; // Путь до отсортированного файла
    private static final String PREFIX = "block";
    private static final String SUFFIX = ".txt";

    /**
     * Сортирует строки во входном файле и сохраняет отсортированные данные в выходном файле.
     *
     * @param source Входной файл со строками, который нужно отсортировать.
     * @return Файл с отсортированными строками.
     * @throws IOException Если возникает ошибка ввода-вывода при работе с файлами.
     */
    public static File sortFile(File source) throws IOException {
        List<String> blocks = splitIntoBlocks(source); // Разбиение всех данных на блоки удовлетворительного размера
        List<String> mergedBlock = combiningBlocks(blocks); // Слияние всех блоков в 1
        return saveToFile(mergedBlock); // Запись в файл
    }

    /**
     * Разбивает содержимое файла на блоки, сортирует их и сохраняет во временные файлы.
     *
     * @param source Файл, содержимое которого нужно разбить на блоки.
     * @return Список временных файлов, в которых хранятся отсортированные блоки.
     * @throws IOException Если произошла ошибка при разбиении файла на блоки или при записи во временные файлы.
     */
    public static List<String> splitIntoBlocks(File source) throws IOException {
        List<String> currentBlock = new ArrayList<>(BLOCK_SIZE);
        List<String> blocks = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(source); Scanner scanner = new Scanner(fileInputStream)) {
            while (scanner.hasNextLine()) {
                String valueFromFile = scanner.nextLine();
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
     * Метод объединяет блоки текста, представленные в виде списка строк.
     * Процесс объединения продолжается до тех пор, пока в списке не останется
     * только один блок текста. Для объединения используется метод sortAndMergeTwoBlocks.
     *
     * @param allBlocks Список строк, представляющих блоки текста.
     * @return Список строк, содержащий единственный объединенный блок текста.
     * @throws IOException Если произошла ошибка ввода-вывода при сортировке и объединении блоков.
     */
    public static List<String> combiningBlocks(List<String> allBlocks) throws IOException {
        while (allBlocks.size() > 1) {
            List<String> mergedBlocks = new ArrayList<>();
            for (int i = 0; i < allBlocks.size(); i += 2) {
                String mergedBlock = sortAndMergeTwoBlocks(allBlocks.get(i), i + 1 < allBlocks.size() ? allBlocks.get(i + 1) : null);
                mergedBlocks.add(mergedBlock);
            }
            allBlocks = mergedBlocks;
        }
        return allBlocks;
    }


    /**
     * Сохраняет значения из списка в файл и возвращает созданный файл.
     *
     * @param outBlock Список строк, значения которых нужно записать в файл.
     * @return Файл, в который были записаны значения из списка.
     * @throws IOException Если произошла ошибка при чтении или записи файла.
     */
    public static File saveToFile(List<String> outBlock) throws IOException {
        File output = new File(OUT_FILE_NAME);
        try (FileInputStream fileInputStream = new FileInputStream(outBlock.get(0)); Scanner scanner = new Scanner(fileInputStream)) {
            try (PrintWriter pw = new PrintWriter(OUT_FILE_NAME)) {
                while (scanner.hasNextLine()) {
                    String stringFromFile = scanner.nextLine();
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
    protected static String writeToTempFile(List<String> block) throws IOException {
        File file = File.createTempFile(PREFIX, SUFFIX);
        file.deleteOnExit();
        try (PrintWriter pw = new PrintWriter(file)) {
            for (String element : block) {
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
        List<String> mergedBlock = new ArrayList<>(2 * BLOCK_SIZE);
        try (FileInputStream fileInputStream = new FileInputStream(firstBlockPath); Scanner firstBlock = new Scanner(fileInputStream); FileInputStream fileInputStream2 = new FileInputStream(secondBlockPath); Scanner secondBlock = new Scanner(fileInputStream2)) {
            String value1 = firstBlock.hasNextLine() ? firstBlock.nextLine() : null;
            String value2 = secondBlock.hasNextLine() ? secondBlock.nextLine() : null;
            while (value1 != null || value2 != null) {
                if (value1 == null) {
                    mergedBlock.add(value2);
                    value2 = secondBlock.hasNextLine() ? secondBlock.nextLine() : null;
                } else if (value2 == null) {
                    mergedBlock.add(value1);
                    value1 = firstBlock.hasNextLine() ? firstBlock.nextLine() : null;
                } else if (value1.compareTo(value2) < 0) {
                    mergedBlock.add(value1);
                    value1 = firstBlock.hasNextLine() ? firstBlock.nextLine() : null;
                } else {
                    mergedBlock.add(value2);
                    value2 = secondBlock.hasNextLine() ? secondBlock.nextLine() : null;
                }
            }
            String mergedBlockPath = writeToTempFile(mergedBlock);
            mergedBlock.clear();
            return mergedBlockPath;
        }
    }

}