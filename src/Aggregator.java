// src/analysis/Aggregator.java
//package analysis;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Aggregator {

    static class Row { int step, S, I, R; Row(int a,int b,int c,int d){step=a;S=b;I=c;R=d;} }

    static List<Row> readRunCsv(Path path) throws IOException {
        List<Row> rows = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] t = line.trim().split(",");
                if (t.length < 4) continue;
                int step = Integer.parseInt(t[0]);
                int S = Integer.parseInt(t[1]);
                int I = Integer.parseInt(t[2]);
                int R = Integer.parseInt(t[3]);
                rows.add(new Row(step,S,I,R));
            }
        }
        return rows;
    }

    static Map<String, Double> summarizeOneRun(Path csv, int N) throws IOException {
        List<Row> rows = readRunCsv(csv);
        // peak I and time-to-peak
        int peakI = -1, timeToPeak = -1;
        for (Row r : rows) {
            if (r.I > peakI) { peakI = r.I; timeToPeak = r.step; }
        }
        // duration = last step with I>0 (if none, duration=0)
        int duration = 0;
        for (Row r : rows) if (r.I > 0) duration = r.step;

        // final R = last row’s R (or 0)
        int finalR = rows.isEmpty() ? 0 : rows.get(rows.size()-1).R;

        Map<String, Double> out = new LinkedHashMap<>();
        out.put("peakI", (double)peakI);
        out.put("timeToPeak", (double)timeToPeak);
        out.put("finalR", (double)finalR);
        out.put("duration", (double)duration);
        out.put("attackRate", N == 0 ? 0.0 : (double)finalR / (double)N);
        return out;
    }

    static void writeSummaryCsv(Path out, List<Map<String, Double>> rows, List<String> runNames) throws IOException {
        Files.createDirectories(out.getParent());
        try (BufferedWriter w = Files.newBufferedWriter(out)) {
            w.write("run,peakI,timeToPeak,finalR,duration,attackRate\n");
            for (int i=0;i<rows.size();i++) {
                Map<String, Double> m = rows.get(i);
                String name = runNames.get(i);
                w.write(String.format(Locale.US, "%s,%.0f,%.0f,%.0f,%.0f,%.4f\n",
                    name, m.get("peakI"), m.get("timeToPeak"), m.get("finalR"),
                    m.get("duration"), m.get("attackRate")));
            }
        }
    }

    static void aggregateScenario(String scenario, int N) throws IOException {
        Path dir = Paths.get("runs", scenario);
        if (!Files.isDirectory(dir)) {
            System.err.println("No folder: " + dir.toString());
            return;
        }
        List<Path> runFiles = Files.list(dir)
            .filter(p -> p.getFileName().toString().startsWith("run_seed") && p.toString().endsWith(".csv"))
            .sorted()
            .collect(Collectors.toList());

        List<Map<String, Double>> rows = new ArrayList<>();
        List<String> names = new ArrayList<>();

        for (Path p : runFiles) {
            rows.add(summarizeOneRun(p, N));
            names.add(p.getFileName().toString().replace(".csv",""));
        }

        Path out = Paths.get("analysis", "summaries", scenario + "_summary.csv");
        writeSummaryCsv(out, rows, names);
        System.out.println("Wrote summary: " + out.toString());

        // Also compute and print means & 95% CI
        printStats("peakI", rows);
        printStats("timeToPeak", rows);
        printStats("finalR", rows);
        printStats("duration", rows);
        printStats("attackRate", rows);
    }

    static void printStats(String key, List<Map<String, Double>> rows) {
        int n = rows.size();
        if (n == 0) { System.out.println(key + ": no data"); return; }
        double[] arr = rows.stream().mapToDouble(m -> m.get(key)).toArray();
        double mean = Arrays.stream(arr).average().orElse(0.0);
        double var = 0.0;
        for (double v : arr) var += (v - mean)*(v - mean);
        var /= Math.max(1, n-1);
        double sd = Math.sqrt(var);
        // 95% CI ≈ mean ± 1.96 * sd / sqrt(n)  (use t if you want, this is fine for n>=20)
        double half = 1.96 * sd / Math.sqrt(n);
        System.out.printf(Locale.US, "%s: mean=%.3f, sd=%.3f, 95%% CI=[%.3f, %.3f], n=%d%n",
                key, mean, sd, mean - half, mean + half, n);
    }

    public static void main(String[] args) throws Exception {
        int N = 50; // keep in sync with BatchRunner baseline
        String[] scenarios = { "baseline", "vaccine30", "quarantine50", "combo" };
        for (String s : scenarios) aggregateScenario(s, N);
    }
}
