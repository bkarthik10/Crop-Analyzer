package com.smartfarming.ai.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Thin wrapper around Apache Commons CSV for reading the bundled reference
 * datasets from the classpath (src/main/resources/datasets/...). Using a real
 * CSV parser (rather than String.split(",")) avoids subtle bugs on quoted
 * fields or embedded commas.
 */
@Component
public class CsvReader {

    /**
     * Reads a classpath CSV resource (e.g. "datasets/crop.csv") into a list of
     * records, keyed by header name. Every value is a raw String; callers are
     * responsible for parsing numeric columns via {@link DataProcessor}.
     */
    public List<CSVRecord> readClasspathCsv(String classpathLocation) throws IOException {
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        try (InputStream is = resource.getInputStream();
             InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .build();

            try (CSVParser parser = new CSVParser(reader, format)) {
                List<CSVRecord> records = new ArrayList<>();
                for (CSVRecord record : parser) {
                    records.add(record);
                }
                return records;
            }
        }
    }
}
