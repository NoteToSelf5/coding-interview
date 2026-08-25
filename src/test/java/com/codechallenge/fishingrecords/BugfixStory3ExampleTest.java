package com.codechallenge.fishingrecords;

import com.codechallenge.fishingrecords.support.CsvFixtureSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Visible example test (shipped to candidates) for the Story 3 task: fix a planted defect in
 * the catch-location filter. This illustrates the contract using a location value that does
 * not happen to trigger the defect — the assertion that actually reproduces the bug is in the
 * hidden test suite used for grading.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BugfixStory3ExampleTest {

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void csvProperties(DynamicPropertyRegistry registry) throws IOException {
        Path csv = CsvFixtureSupport.copySeedFixture(tempDir);
        registry.add("fishingrecords.storage.path", csv::toString);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void filtersRecordsByExactLocation() throws Exception {
        mockMvc.perform(get("/api/fishing-records").param("location", "Lake Constance North"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }
}
