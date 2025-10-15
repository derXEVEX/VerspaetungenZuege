package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class VerspaetungsAnalyzer {

    /**
     */
    public static Map<String, List<Integer>> gruppiereNachBahnhof(List<Map<String, String>> daten) {
        return daten.stream()
                .filter(row -> row.containsKey("HALTESTELLEN_NAME") && row.containsKey("VERSPAETUNG_AN"))
                .collect(Collectors.groupingBy(
                        row -> row.get("HALTESTELLEN_NAME"),
                        Collectors.mapping(row -> parseVerspaetung(row.get("VERSPAETUNG_AN")), Collectors.toList())
                ));
    }

    private static int parseVerspaetung(String text) {
        try {
            int sek = Integer.parseInt(text);
            return sek / 60;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void analyze(Map<String, List<Integer>> verspaetungenProBahnhof) {
        System.out.println("\n=== ÖV PÜNKTLICHKEITSANALYSE SCHWEIZ ===\n");

        System.out.println("1) Verspätungen > 5 Minuten:");
        verspaetungenProBahnhof.forEach((bahnhof, liste) ->
                liste.stream().filter(v -> v > 5)
                        .forEach(v -> System.out.println("   " + bahnhof + ": " + v + " Min"))
        );
        System.out.println();

        Map<String, Double> durchschnitt = verspaetungenProBahnhof.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().mapToInt(Integer::intValue).average().orElse(0.0)
                ));

        System.out.println("2) Durchschnittsverspätung pro Bahnhof:");
        durchschnitt.forEach((b, d) -> System.out.printf("   %-20s %.2f Min%n", b, d));
        System.out.println();

        double gesamt = durchschnitt.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        System.out.printf("3) Gesamtdurchschnitt: %.2f Min%n%n", gesamt);

        String minBahnhof = Collections.min(durchschnitt.entrySet(), Map.Entry.comparingByValue()).getKey();
        String maxBahnhof = Collections.max(durchschnitt.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.printf("4) Pünktlichster Bahnhof: %s (%.2f Min)%n", minBahnhof, durchschnitt.get(minBahnhof));
        System.out.printf("   Unpünktlichster Bahnhof: %s (%.2f Min)%n%n", maxBahnhof, durchschnitt.get(maxBahnhof));

        long puenktlich = verspaetungenProBahnhof.values().stream()
                .flatMap(List::stream)
                .filter(v -> v < 1)
                .count();
        System.out.println("5) Anzahl pünktliche Fahrten (<1 Min): " + puenktlich);

        System.out.println("\n6) Ranking (nach Durchschnittsverspätung):");
        durchschnitt.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> System.out.printf("   %-20s %.2f Min%n", e.getKey(), e.getValue()));
    }
}
