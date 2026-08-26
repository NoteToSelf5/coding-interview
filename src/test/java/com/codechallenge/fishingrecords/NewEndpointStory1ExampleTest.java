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

@SpringBootTest
@AutoConfigureMockMvc
class NewEndpointStory1ExampleTest {

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
    void returnsLargestWeightPerSpecies() throws Exception {
        // Response is a species-to-weight map, e.g. {"Carp": 6.8, "Perch": 0.6, ...}.
        mockMvc.perform(get("/api/fishing-records/top-by-species"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Carp").value(6.8))
                .andExpect(jsonPath("$.Perch").value(0.6));
    }
}
