package org.example;


import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class CsvReader {

    public static List<Map<String, String>> readCsv(Path path, String delimiter) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String headerLine = reader.readLine();
            if (headerLine == null) return List.of();

            String[] headers = headerLine.split(delimiter, -1);

            return reader.lines()
                    .map(line -> parseLine(line, headers, delimiter))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Lesen der CSV-Datei: " + e.getMessage(), e);
        }
    }

    private static Map<String, String> parseLine(String line, String[] headers, String delimiter) {
        String[] values = line.split(delimiter, -1);
        if (values.length != headers.length) return null;

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            map.put(headers[i].trim(), values[i].trim());
        }
        return map;
    }
}
