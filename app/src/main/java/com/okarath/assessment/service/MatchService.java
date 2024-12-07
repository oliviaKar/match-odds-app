package com.okarath.assessment.service;

import com.okarath.assessment.dto.MatchDto;
import com.okarath.assessment.dto.UpdateMatchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MatchService {
    MatchDto save(MatchDto m);

    MatchDto findById(long id);

    Page<MatchDto> getMatches(Pageable pageable);

    boolean delete(Long id);

    void updateMatch(Long id, UpdateMatchDto updateMatchDto);
}
