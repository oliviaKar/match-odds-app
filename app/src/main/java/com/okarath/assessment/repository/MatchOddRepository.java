package com.okarath.assessment.repository;

import com.okarath.assessment.entity.Match;
import com.okarath.assessment.entity.MatchOdd;
import com.okarath.assessment.entity.Specifier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface MatchOddRepository extends JpaRepository<MatchOdd, Long> {

    MatchOdd save(MatchOdd matchOdd);

    boolean existsByMatchAndSpecifier(Match match, Specifier specifier);

    Set<MatchOdd> findByMatch(Match match);
}
