package com.codechallenge.fishingrecords.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FishingRecord {

    @EqualsAndHashCode.Include
    private Long id;

    private String species;
    private double weightKg;
    private double lengthCm;
    private String location;
    private LocalDateTime caughtAt;
    private String anglerName;
}
