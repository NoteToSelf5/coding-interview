package com.codechallenge.fishingrecords.web;

import com.codechallenge.fishingrecords.model.FishingRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Deliberately not layered: CSV read/write is inlined into each handler below rather than
 * extracted into a persistence class. This is the starter state for the coding-interview
 * challenge's refactor task (extract persistence from request handling), and the
 * location-filter handler additionally contains a planted, deterministic defect (substring
 * match instead of exact match) for the bugfix task. See specs/001-fishing-records-api/spec.md.
 */
@RestController
@RequestMapping("/api/fishing-records")
public class FishingRecordController {

    private final String storagePath;

    public FishingRecordController(@Value("${fishingrecords.storage.path}") String storagePath) {
        this.storagePath = storagePath;
    }

    @PostMapping
    public ResponseEntity<?> createRecord(@RequestBody Map<String, Object> body) {
        String species = (String) body.get("species");
        String location = (String) body.get("location");
        String anglerName = (String) body.get("anglerName");
        String caughtAtRaw = (String) body.get("caughtAt");
        double weightKg = ((Number) body.get("weightKg")).doubleValue();
        double lengthCm = ((Number) body.get("lengthCm")).doubleValue();

        if (weightKg <= 0) {
            return errorResponse("weightKg must be greater than 0");
        }
        if (lengthCm <= 0) {
            return errorResponse("lengthCm must be greater than 0");
        }

        try {
            List<CSVRecord> existingRows;
            try (Reader reader = new FileReader(storagePath)) {
                existingRows = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).getRecords();
            }

            long nextId = 1L;
            for (CSVRecord row : existingRows) {
                long id = Long.parseLong(row.get("id"));
                if (id >= nextId) {
                    nextId = id + 1;
                }
            }

            FishingRecord created = new FishingRecord(nextId, species, weightKg, lengthCm,
                    location, LocalDateTime.parse(caughtAtRaw), anglerName);

            try (Writer writer = new FileWriter(storagePath, true)) {
                CSVFormat.DEFAULT.print(writer).printRecord(
                        created.getId(), created.getSpecies(), created.getWeightKg(),
                        created.getLengthCm(), created.getLocation(), created.getCaughtAt(),
                        created.getAnglerName());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read/write fishing records CSV", e);
        }
    }

    @GetMapping
    public ResponseEntity<?> listRecords(@RequestParam(name = "location", required = false) String location) {
        try {
            List<FishingRecord> all = readAllRecords();

            if (location == null) {
                return ResponseEntity.ok(all);
            }

            // Planted defect (Story 3 bugfix target): this uses a substring match instead of
            // an exact match, so querying a shorter location name that happens to be a prefix
            // of a longer one (e.g. "Lake Constance" vs. "Lake Constance North") incorrectly
            // also returns the longer location's records.
            List<FishingRecord> filtered = new ArrayList<>();
            for (FishingRecord record : all) {
                if (record.getLocation().contains(location)) {
                    filtered.add(record);
                }
            }
            return ResponseEntity.ok(filtered);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read fishing records CSV", e);
        }
    }

    @GetMapping("/top-by-species")
    public ResponseEntity<?> topBySpecies() {
        // Not implemented in the starter — this endpoint is the Story 1 (junior-calibrated)
        // candidate task: return the single largest-weight record for each distinct species.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    private List<FishingRecord> readAllRecords() throws IOException {
        List<FishingRecord> records = new ArrayList<>();
        try (Reader reader = new FileReader(storagePath)) {
            for (CSVRecord row : CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {
                records.add(new FishingRecord(
                        Long.parseLong(row.get("id")),
                        row.get("species"),
                        Double.parseDouble(row.get("weightKg")),
                        Double.parseDouble(row.get("lengthCm")),
                        row.get("location"),
                        LocalDateTime.parse(row.get("caughtAt")),
                        row.get("anglerName")));
            }
        }
        return records;
    }

    private ResponseEntity<?> errorResponse(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", message);
        return ResponseEntity.badRequest().body(body);
    }
}
