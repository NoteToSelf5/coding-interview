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
 * Visible example test (shipped to candidates) for the Story 2 task: refactor the
 * catch-location filter's persistence logic without changing behavior. This illustrates the
 * contract with one straightforward correct-match case; additional pre-existing scenarios used
 * to detect a behavior regression are covered by the hidden test suite.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RefactorStory2ExampleTest {

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
        mockMvc.perform(get("/api/fishing-records").param("location", "Rhine River"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].location").value("Rhine River"));
    }
}
