package com.okarath.assessment.service.impl;

import com.okarath.assessment.dto.MatchDto;
import com.okarath.assessment.dto.UpdateMatchDto;
import com.okarath.assessment.entity.Match;
import com.okarath.assessment.entity.Sport;
import com.okarath.assessment.exception.ResourceAlreadyExistsException;
import com.okarath.assessment.exception.ResourceNotFoundException;
import com.okarath.assessment.repository.MatchRepository;
import com.okarath.assessment.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchServiceImplTest {

    @Mock
    private MatchRepository matchRepository;
    @Captor
    private ArgumentCaptor<Match> captor;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


    private MatchService matchService;

    private MatchDto matchDtoToSave;

    private MatchDto matchDtoSaved;
    private Match matchWithId;

    private String teamA = "OSFP";
    private String teamB = "PAOK";
    private String date = "21/02/2025";
    private String time = "12:45";
    private Sport sport = Sport.BASKETBALL;
    private Long id = 1L;

    @BeforeEach
    public void setUp() {
        matchDtoToSave = MatchDto.builder()
                .teamB(teamB)
                .teamA(teamA)
                .sport(sport)
                .date(date)
                .time(time)
                .build();
        matchDtoSaved = MatchDto.builder()
                .id(id)
                .teamB(teamB)
                .teamA(teamA)
                .sport(sport)
                .date(date)
                .time(time)
                .description(String.format("%s-%s", teamA, teamB))
                .odds(new HashSet<>())
                .build();
        matchWithId = Match.builder()
                .id(id)
                .teamB(teamB)
                .teamA(teamA)
                .sport(sport)
                .description(String.format("%s-%s", teamA, teamB))
                .matchDate(LocalDate.parse(date, dateFormatter))
                .matchTime(LocalTime.parse(time, timeFormatter))
                .build();
        matchService = new MatchServiceImpl(matchRepository);
    }


    @Test
    public void shouldSaveNewMatch() {
        when(matchRepository.existsByTeamAAndTeamBAndSportAndMatchDate(teamA, teamB, sport, LocalDate.parse(date, dateFormatter))).thenReturn(false);

        when(matchRepository.save(any())).thenReturn(matchWithId);
        MatchDto savedMatch = matchService.save(matchDtoToSave);

        assertEquals(id, savedMatch.id());
        verify(matchRepository, times(1)).save(captor.capture());
        Match match = captor.getValue();
        assertNull(match.getId());
        assertEquals(teamA, match.getTeamA());
        assertEquals(String.format("%s-%s", teamA, teamB), match.getDescription());
    }

    @Test
    public void shouldNotSaveExistingMatch() {
        when(matchRepository.existsByTeamAAndTeamBAndSportAndMatchDate(teamA, teamB, sport, LocalDate.parse(date, dateFormatter))).thenReturn(true);

        ResourceAlreadyExistsException thrown = assertThrows(ResourceAlreadyExistsException.class,
                () -> matchService.save(matchDtoToSave));

        assertEquals(String.format("%s match between %s and %s on %s already exists.", sport, teamA, teamB, date),
                thrown.getMessage());

    }

    @Test
    public void shouldFindById() {
        when(matchRepository.findById(id)).thenReturn(Optional.of(matchWithId));

        MatchDto matchDto = matchService.findById(id);

        assertEquals(matchDtoSaved, matchDto);
    }

    @Test
    public void shouldThrowExceptionWhenMatchNotFound() {
        when(matchRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> matchService.findById(id));
    }

    @Test
    public void shouldGetMatches() {
        Pageable pageable = Pageable.ofSize(10);
        when(matchRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(matchWithId), pageable, 1));

        Page<MatchDto> page = matchService.getMatches(pageable);

        assertEquals(1, page.getNumberOfElements());
        assertEquals(matchDtoSaved, page.get().findFirst().get());
    }

    @Test
    public void shouldReturnTrueIfDeleted() {
        when(matchRepository.existsById(id)).thenReturn(true);

        boolean deleted = matchService.delete(id);

        assertTrue(deleted);
        verify(matchRepository, times(1)).deleteById(id);
    }

    @Test
    public void shouldReturnFalseIfNotDeleted() {
        when(matchRepository.existsById(id)).thenReturn(false);

        boolean deleted = matchService.delete(id);

        assertFalse(deleted);
        verify(matchRepository, times(0)).deleteById(id);
    }

    @Test
    public void shouldUpdateMatchDescription() {
        when(matchRepository.findById(id))
                .thenReturn(Optional.of(matchWithId));
        UpdateMatchDto updateMatchDto = new UpdateMatchDto(null, "new description");

        matchService.updateMatch(id, updateMatchDto);

        matchWithId.setDescription("new description");

        verify(matchRepository, times(1)).save(matchWithId);
    }
}
