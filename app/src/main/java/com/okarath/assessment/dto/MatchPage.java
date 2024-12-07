package com.okarath.assessment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable"})
public class MatchPage extends PageImpl<MatchDto> {
    public MatchPage(List<MatchDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public MatchPage() {
        super(new ArrayList<>());
    }
}
