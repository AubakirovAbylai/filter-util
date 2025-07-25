package org.example;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        boolean append = false;
        boolean shortStats = false;
        boolean fullStats = false;
        String prefix = "";
        String outputDir = ".";
        List<String> inputFiles = new ArrayList<>();

        // Парсинг аргументов
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-a" -> append = true;
                case "-s" -> shortStats = true;
                case "-f" -> fullStats = true;
                case "-p" -> {
                    if (i + 1 < args.length) prefix = args[++i];
                    else System.err.println("Ожидается префикс после -p");
                }
                case "-o" -> {
                    if (i + 1 < args.length) outputDir = args[++i];
                    else System.err.println("Ожидается путь после -o");
                }
                default -> inputFiles.add(args[i]);
            }
        }

        if (inputFiles.isEmpty()) {
            System.err.println("Не указаны входные файлы.");
            System.exit(1);
        }

        List<String> integers = new ArrayList<>();
        List<String> floats = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        // Чтение всех файлов
        for (String filename : inputFiles) {
            File file = new File(filename);
            if (!file.exists()) {
                System.err.println("Файл не найден: " + filename);
                continue;
            }

            try (Scanner input = new Scanner(file)) {
                while (input.hasNextLine()) {
                    String line = input.nextLine().trim();

                    if (line.isEmpty()) continue;

                    if (isInteger(line)) {
                        integers.add(line);
                    } else if (isFloat(line)) {
                        floats.add(line);
                    } else {
                        strings.add(line);
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка чтения файла: " + filename);
            }
        }

        // Запись в файлы
        if(!integers.isEmpty()){
            writeToFile(outputDir, prefix + "integers.txt", integers, append);
        }

        if(!floats.isEmpty()){
            writeToFile(outputDir, prefix + "floats.txt", floats, append);
        }

        if(!strings.isEmpty()){
            writeToFile(outputDir, prefix + "strings.txt", strings, append);
        }

        // Статистика
        if (shortStats || fullStats) {
            printShortStats(integers, floats, strings);
        }

        if (fullStats) {
            printFullStats(integers, floats, strings);
        }

    }

    static void writeToFile(String dir, String filename, List<String> data, boolean append) {
        if (data.isEmpty()) return;
        File file = new File(dir, filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, append))) {
            for (String s : data) {
                writer.write(s);
                writer.newLine();
            }
            System.out.println("Запись в файл: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Ошибка записи в файл: " + file.getPath());
        }
    }
    static boolean isInteger(String s) {
        try {
            if (s.contains(".") || s.contains("e") || s.contains("E")) return false;
            new java.math.BigInteger(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static boolean isFloat(String s) {
        try {
            if (s.matches("[-+]?\\d*\\.\\d+([eE][-+]?\\d+)?")
                    || s.matches("[-+]?\\d+[eE][-+]?\\d+")) {
                Double.parseDouble(s);
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static String preview(List<String> data) {
        return data.stream().toList().toString();
    }

    static void printShortStats(List<String> integers, List<String> floats, List<String> strings) {
        System.out.println("\n\nКраткая статистика:");
        System.out.println("Целые числа: " + integers.size());
        System.out.println("Дробные числа: " + floats.size());
        System.out.println("Строки: " + strings.size());
    }

    static void printFullStats(List<String> integers, List<String> floats, List<String> strings) {
        System.out.println("\n\nСодержимое:");
        System.out.println("Int: " + preview(integers));
        System.out.println("Float: " + preview(floats));
        System.out.println("String: " + preview(strings));

        // Int stats
        if (!integers.isEmpty()) {
            List<BigInteger> intValues = integers.stream()
                    .map(BigInteger::new)
                    .toList();
            BigInteger min = intValues.stream().min(BigInteger::compareTo).orElse(BigInteger.ZERO);
            BigInteger max = intValues.stream().max(BigInteger::compareTo).orElse(BigInteger.ZERO);
            BigInteger sum = intValues.stream().reduce(BigInteger.ZERO, BigInteger::add);
            double avg = sum.doubleValue() / intValues.size();

            System.out.println("\n\nСтатистика целых чисел:");
            System.out.println("Минимум: " + min);
            System.out.println("Максимум: " + max);
            System.out.println("Сумма: " + sum);
            System.out.printf(Locale.US, "Среднее: %.5f%n", avg);
        }

        // Float stats
        if (!floats.isEmpty()) {
            List<Double> floatValues = floats.stream()
                    .map(Double::parseDouble)
                    .toList();
            double min = floatValues.stream().min(Double::compare).orElse(0.0);
            double max = floatValues.stream().max(Double::compare).orElse(0.0);
            double sum = floatValues.stream().reduce(0.0, Double::sum);
            double avg = sum / floatValues.size();

            System.out.println("\n\nСтатистика дробных чисел:");
            System.out.println("Минимум: " + min);
            System.out.println("Максимум: " + max);
            System.out.printf(Locale.US, "Сумма: %.5f%n", sum);
            System.out.printf(Locale.US, "Среднее: %.5f%n", avg);
        }

        // String stats
        if (!strings.isEmpty()) {
            int minLen = strings.stream().mapToInt(String::length).min().orElse(0);
            int maxLen = strings.stream().mapToInt(String::length).max().orElse(0);
            System.out.println("\n\nСтатистика строк:");
            System.out.println("Минимальная длина: " + minLen);
            System.out.println("Максимальная длина: " + maxLen);
        }
    }
}

