package com.okarath.assessment.service.impl;

import com.okarath.assessment.dto.OddDto;
import com.okarath.assessment.dto.UpdateOddDto;
import com.okarath.assessment.entity.Match;
import com.okarath.assessment.entity.MatchOdd;
import com.okarath.assessment.exception.ResourceNotFoundException;
import com.okarath.assessment.exception.ResourceAlreadyExistsException;
import com.okarath.assessment.repository.MatchOddRepository;
import com.okarath.assessment.repository.MatchRepository;
import com.okarath.assessment.service.MatchOddsService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
public class MatchOddsServiceImpl implements MatchOddsService {

    private final MatchRepository matchRepository;
    private final MatchOddRepository matchOddRepository;

    public MatchOddsServiceImpl(MatchRepository matchRepository, MatchOddRepository matchOddRepository) {
        this.matchRepository = matchRepository;
        this.matchOddRepository = matchOddRepository;
    }

    @Override
    public OddDto createOddForMatch(Long matchId, OddDto oddDto) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Match with id %s not found", matchId)));

        if (matchOddRepository.existsByMatchAndSpecifier(match, oddDto.specifier())) {
            throw new ResourceAlreadyExistsException(String.format("Odd for specifier %s already exists for match with id %s", oddDto.specifier(), match.getId() ));
        }

        MatchOdd odd = convertToEntity(oddDto);
        odd.setMatch(match);

        var savedOdd = matchOddRepository.save(odd);

        return convertToDto(savedOdd);
    }

    @Override
    public Set<OddDto> getOddsForMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Match with id %s not found", matchId)));
        ofNullable(match.getOdds()).ifPresentOrElse(it -> {},
                () -> match.setOdds(new HashSet<>()));
        return match.getOdds().stream().map(this::convertToDto).collect(Collectors.toSet());
    }

    @Override
    public void updateOdd(Long matchId, Long oddId, UpdateOddDto oddDto) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Match with id %s not found", matchId)));

        matchOddRepository.findByMatch(match).stream()
                .filter(odd -> odd.getId() == oddId)
                .findAny()
                .ifPresentOrElse(existingOdd -> {
                    existingOdd.setOdd(oddDto.odd());
                    matchOddRepository.save(existingOdd);
                },() -> {
                    throw new ResourceNotFoundException(String.format("No odd with id %s for matchId %s found", oddId, matchId));
                });
    }

    @Override
    public boolean delete(Long matchId, Long oddId) {
        Optional<Match> matchOpt = matchRepository.findById(matchId);
        if (matchOpt.isEmpty()) return false;

        Optional<MatchOdd> oddOpt = matchOddRepository.findByMatch(matchOpt.get()).stream()
                .filter(odd -> odd.getId() == oddId)
                .findAny();

        if (oddOpt.isEmpty()) return false;

        matchOddRepository.delete(oddOpt.get());
        return true;
    }

    private OddDto convertToDto(MatchOdd odd) {
        return OddDto.builder()
                .id(odd.getId())
                .odd(odd.getOdd())
                .specifier(odd.getSpecifier())
                .build();
    }

    private MatchOdd convertToEntity(OddDto oddDto) {
        return MatchOdd.builder()
                .specifier(oddDto.specifier())
                .odd(oddDto.odd())
                .build();
    }
}
