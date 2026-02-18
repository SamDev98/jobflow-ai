package com.jobflow.dto.response;

import java.util.UUID;

public record SalaryRangeResponse(
        UUID researchId,
        int rangeLowUsd,
        int rangeMidUsd,
        int rangeHighUsd,
        String reasoning,
        int confidenceScore
) {}
