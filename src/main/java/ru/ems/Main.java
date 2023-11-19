package ru.ems;

import ru.ems.enums.MainFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;

public class Main {

    // Не очень понял как сортируем, поэтому реализовал несколько вариантов сортировки:
    /**
     * Компаратор для сравнения строк на основе числа (разделенного символом ":") в порядке убывания.
     */
//    public static final Comparator<String> comparator = Comparator.comparing(s -> -Long.parseLong(s.split(":")[1]));
    /**
     * Компаратор для сравнения строк на основе первого символа первого поля (разделенного символом ":") в
     * порядке убывания, затем по числовому полю в порядке возрастания.
     */
//    public static final Comparator<String> comparator = Comparator.comparing((String s) -> s.split(":")[0].charAt(0))
//            .thenComparingLong(s -> -Long.parseLong(s.split(":")[1])).reversed()
//            .thenComparing(s -> s.split(":")[0].substring(1) + ":" + s.split(":")[1]).reversed();
    /**
      Компаратор для сравнения строк на основе первой подстроки (разделенного символом ":").
     *
     */
    public static final Comparator<String> comparator = Comparator.comparing((String s) -> s.split(":")[0]);


    /**
     * Размер блока данных, используемый в процессе сортировки. Рассчитывается на основе доступной памяти.
     */
    public static int BLOCK_SIZE = (int) ((ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted() - ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()) / (30 * 4));

    public static void main(String[] args) throws IOException {
            System.out.println("Start: " + new Date());
            System.out.println("Проверка после генерации: " + new Date() + " = " + isSorted(MainFile.UNSORTED.getFile()));
            System.out.println("Объем блока после генерации: " + BLOCK_SIZE);
            ExternalMergeSort.sort();
            System.out.println("Отсортированный файл создан: " + new Date());
            System.out.println("Проверка после сортировки: " + new Date() + " = " + isSorted(MainFile.SORTED.getFile()));
            System.out.println("Stop: " + new Date());
    }


    /**
     * Проверяет, отсортирован ли содержимое указанного файла согласно компаратору.
     *
     * @param file Файл, содержимое которого требуется проверить на сортировку.
     * @return {@code true}, если содержимое файла отсортировано по компоратору;
     *         {@code false}, если содержимое файла не отсортировано или содержит ошибки.
     * @throws IOException Возникает в случае ошибок ввода/вывода при чтении файла.
     */
    public static boolean isSorted(File file) throws IOException {
        try (Scanner scanner = new Scanner(new FileInputStream(file))) {
            String prev = null;
            while (scanner.hasNextLine()) {
                String current = scanner.nextLine();
                if (prev != null && comparator.compare(current, prev) < 0) {
                    return false;
                } else {
                    prev = current;
                }
            }
            return true;
        }
    }


}
