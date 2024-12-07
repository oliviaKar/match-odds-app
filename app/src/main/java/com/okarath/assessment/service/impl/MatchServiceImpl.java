package com.okarath.assessment.service.impl;

import com.okarath.assessment.dto.MatchDto;
import com.okarath.assessment.dto.OddDto;
import com.okarath.assessment.dto.UpdateMatchDto;
import com.okarath.assessment.entity.Match;
import com.okarath.assessment.entity.MatchOdd;
import com.okarath.assessment.exception.ResourceAlreadyExistsException;
import com.okarath.assessment.exception.ResourceNotFoundException;
import com.okarath.assessment.repository.MatchRepository;
import com.okarath.assessment.service.MatchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public MatchServiceImpl(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public MatchDto save(MatchDto match) {
        Match m = convertToEntity(match);
        if (matchRepository.existsByTeamAAndTeamBAndSportAndMatchDate(m.getTeamA(), m.getTeamB(), m.getSport(), m.getMatchDate())) {
            throw new ResourceAlreadyExistsException(String.format("%s match between %s and %s on %s already exists.", m.getSport(), m.getTeamA(), m.getTeamB(), match.date()));
        }
        Match savedMatch = matchRepository.save(m);
        return convertToDto(savedMatch);
    }

    @Override
    public MatchDto findById(long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Match with id %s not found", id)));
        return convertToDto(match);
    }

    @Override
    public Page<MatchDto> getMatches(Pageable pageable) {
        return matchRepository.findAll(pageable).map(this::convertToDto);
    }

    @Override
    public boolean delete(Long id) {
        if (matchRepository.existsById(id)) {
            matchRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void updateMatch(Long id, UpdateMatchDto updateMatchDto) {
        Match existingMatch = matchRepository.findById(id)
                .orElseThrow(() ->  new ResourceNotFoundException(String.format("Match with id %s not found", id)));

        ofNullable(updateMatchDto.time())
                .ifPresent(it -> existingMatch.setMatchTime(LocalTime.parse(it, timeFormatter)));
        ofNullable(updateMatchDto.description())
                .ifPresent(existingMatch::setDescription);
        matchRepository.save(existingMatch);
    }

    private Match convertToEntity(MatchDto match){
        Match m = Match.builder()
                .teamA(match.teamA())
                .teamB(match.teamB())
                .description(match.description() == null ? String.format("%s-%s", match.teamA(), match.teamB()) : match.description())
                .matchDate(LocalDate.parse(match.date(), dateFormatter))
                .matchTime(LocalTime.parse(match.time(), timeFormatter))
                .sport(match.sport())
                .build();

        if (match.odds() != null) {
            m.setOdds(match.odds().stream().map(odd -> MatchOdd.builder()
                    .match(m)
                    .specifier(odd.specifier())
                    .odd(odd.odd())
                    .build()).collect(Collectors.toSet()));
        }

        return m;
    }

    private MatchDto convertToDto(Match match) {
        return MatchDto.builder()
                .id(match.getId())
                .description(match.getDescription())
                .teamA(match.getTeamA())
                .teamB(match.getTeamB())
                .time(match.getMatchTime().format(timeFormatter))
                .date(match.getMatchDate().format(dateFormatter))
                .sport(match.getSport())
                .odds(match.getOdds().stream().map(odd -> OddDto
                        .builder()
                        .id(odd.getId())
                        .specifier(odd.getSpecifier())
                        .odd(odd.getOdd())
                        .build()).collect(Collectors.toSet()))
                .build();
    }
}
