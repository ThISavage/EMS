package ru.ems;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

public class Main {
    private static final String UNSORTED_FILE = "src" + File.separator+"main" + File.separator + "resources" + File.separator + "unsorted.txt"; // Путь до неотсортированного файла

    public static void main(String[] args) throws IOException {
        System.out.println("Start: " + new Date());
        File source = new File(UNSORTED_FILE);
        System.out.println("Проверка после генерации: " + new Date() + " = " + isSorted(source));
        File sortedFile = ExternalMergeSort.sortFile(source);
        System.out.println("Отсортированный файл сгенерирован: " + new Date());
        System.out.println("Проверка после сортировки: " + new Date() + " = " + isSorted(sortedFile));
        System.out.println("Stop: " + new Date());
    }


    /**
     * Проверяет, отсортированы ли строки в файле в порядке возрастания.
     *
     * @return true, если строки отсортированы в порядке возрастания, в противном случае false.
     */
    public static boolean isSorted(File file) throws IOException {
        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            String prev = null;
            while (scanner.hasNextLine()) {
                String current = scanner.nextLine();
                if (prev != null && current.compareTo(prev) < 0) {
                    return false;
                } else {
                    prev = current;
                }
            }
            return true;
        }
    }


}
