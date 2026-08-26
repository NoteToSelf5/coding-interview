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

@RestController
@RequestMapping("/api/fishing-records")
public class FishingRecordController {

    private final String storagePath;

    public FishingRecordController(@Value("${fishingrecords.storage.path}") String storagePath) {
        this.storagePath = storagePath;
    }

    @PostMapping
    public ResponseEntity<?> createRecord(@RequestBody FishingRecord body) {
        if (body.getWeightKg() <= 0) {
            return errorResponse("weightKg must be greater than 0");
        }
        if (body.getLengthCm() <= 0) {
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

            body.setId(nextId);

            try (Writer writer = new FileWriter(storagePath, true)) {
                CSVFormat.DEFAULT.print(writer).printRecord(
                        body.getId(), body.getSpecies(), body.getWeightKg(),
                        body.getLengthCm(), body.getColor(), body.getAnglerName(),
                        body.getAnglerLicenseNumber(), body.getAnglerEmail(),
                        body.getLocation(), body.getCaughtAt(),
                        body.getWeatherCondition(), body.getBaitUsed());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(body);
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
                        row.get("color"),
                        row.get("anglerName"),
                        row.get("anglerLicenseNumber"),
                        row.get("anglerEmail"),
                        row.get("location"),
                        LocalDateTime.parse(row.get("caughtAt")),
                        row.get("weatherCondition"),
                        row.get("baitUsed")));
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
