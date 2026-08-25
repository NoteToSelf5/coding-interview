package com.codechallenge.fishingrecords.support;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class CsvFixtureSupport {

    private CsvFixtureSupport() {
    }

    public static Path copySeedFixture(Path tempDir) throws IOException {
        Path target = tempDir.resolve("fishing-records-test.csv");
        try (InputStream in = CsvFixtureSupport.class.getClassLoader()
                .getResourceAsStream("data/fishing-records-seed.csv")) {
            if (in == null) {
                throw new IOException("Seed CSV fixture not found on classpath");
            }
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return target;
    }
}
