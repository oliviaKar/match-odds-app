package com.okarath.assessment.service;

import com.okarath.assessment.dto.OddDto;
import com.okarath.assessment.dto.UpdateOddDto;

import java.util.Set;

public interface MatchOddsService {
    OddDto createOddForMatch(Long matchId, OddDto matchOddsDto);

    Set<OddDto> getOddsForMatch(Long matchId);

    void updateOdd(Long matchId, Long oddId, UpdateOddDto oddDto);

    boolean delete(Long matchId, Long oddId);
}
