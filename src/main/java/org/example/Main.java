package org.example;


import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Path filePath = Path.of("data/istdaten.csv");

        List<Map<String, String>> daten = CsvReader.readCsv(filePath, ";");

        Map<String, List<Integer>> verspaetungenProBahnhof = VerspaetungsAnalyzer.gruppiereNachBahnhof(daten);

        VerspaetungsAnalyzer.analyze(verspaetungenProBahnhof);
    }
}
