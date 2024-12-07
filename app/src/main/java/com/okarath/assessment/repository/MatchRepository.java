package com.okarath.assessment.repository;

import com.okarath.assessment.entity.Match;
import com.okarath.assessment.entity.Sport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Match save(Match match);

    Optional<Match> findById(Long id);

    Page<Match> findAll(Pageable pageable);

    void deleteById(Long id);

    boolean existsByTeamAAndTeamBAndSportAndMatchDate(String teamA, String teamB, Sport sport, LocalDate matchDate);
}
