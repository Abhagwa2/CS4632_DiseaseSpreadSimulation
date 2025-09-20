package metrics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Collects S/I/R counts per step for plotting/screenshots. */
public class MetricsCollector {
    public static final class Row {
        public final int step, S, I, R;
        public Row(int step, int s, int i, int r) { this.step = step; this.S = s; this.I = i; this.R = r; }
    }

    private final List<Row> rows = new ArrayList<>();
    private boolean includeHeader = true;

    /** Record counts for the given step. */
    public void record(int step, int s, int i, int r) {
        rows.add(new Row(step, s, i, r));
    }

    /** Write a simple CSV: step,S,I,R */
    public void writeCsv(Path path) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(path)) {
            if (includeHeader) w.write("step,S,I,R\n");
            for (Row r : rows) {
                w.write(r.step + "," + r.S + "," + r.I + "," + r.R + "\n");
            }
        }
    }

    public int size() { return rows.size(); }
    public void clear() { rows.clear(); }
    public void setIncludeHeader(boolean include) { this.includeHeader = include; }
}
