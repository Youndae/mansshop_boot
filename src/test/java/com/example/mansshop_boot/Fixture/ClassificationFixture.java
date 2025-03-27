package com.example.mansshop_boot.Fixture;

import com.example.mansshop_boot.domain.entity.Classification;

import java.util.ArrayList;
import java.util.List;

public class ClassificationFixture {

    public static List<Classification> createClassification() {
        List<String> classificationIds = List.of("OUTER", "TOP", "BAGS", "PANTS", "SHOES");
        List<Classification> result = new ArrayList<>();
        int stepCount = 1;
        for(String name : classificationIds) {
            result.add(
                    Classification.builder()
                            .id(name)
                            .classificationStep(stepCount++)
                            .build()
            );
        }

        return result;
    }
}
