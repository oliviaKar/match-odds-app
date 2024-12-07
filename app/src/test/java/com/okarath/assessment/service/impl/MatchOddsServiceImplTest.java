package com.okarath.assessment.service.impl;

import com.okarath.assessment.dto.OddDto;
import com.okarath.assessment.dto.UpdateOddDto;
import com.okarath.assessment.entity.Match;
import com.okarath.assessment.entity.MatchOdd;
import com.okarath.assessment.entity.Specifier;
import com.okarath.assessment.exception.ResourceAlreadyExistsException;
import com.okarath.assessment.exception.ResourceNotFoundException;
import com.okarath.assessment.repository.MatchOddRepository;
import com.okarath.assessment.repository.MatchRepository;
import com.okarath.assessment.service.MatchOddsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchOddsServiceImplTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchOddRepository matchOddRepository;

    private MatchOddsService matchOddsService;

    private OddDto oddDtoToSave;


    @Mock
    private Match match;

    private Specifier specifier = Specifier.ONE;
    private double odd = 1.5;

    private long id = 1L;
    @BeforeEach
    public void setUp() {
        oddDtoToSave = new OddDto(null, specifier, odd);
        matchOddsService = new MatchOddsServiceImpl(matchRepository, matchOddRepository);
    }

    @Test
    public void shouldCreateOdd() {
        OddDto savedOddDto = new OddDto(id, specifier, odd);
        MatchOdd savedOdd = MatchOdd.builder().id(id).specifier(specifier).odd(odd).build();
        when(matchRepository.findById(id)).thenReturn(Optional.of(match));
        when(matchOddRepository.existsByMatchAndSpecifier(match, specifier)).thenReturn(false);
        when(matchOddRepository.save(any())).thenReturn(savedOdd);

        OddDto result = matchOddsService.createOddForMatch(id, oddDtoToSave);
        assertEquals(savedOddDto, result);
    }

    @Test
    public void shouldThrowExceptionIfOddForSpecifierAlreadyExists() {
        when(matchRepository.findById(id)).thenReturn(Optional.of(match));
        when(matchOddRepository.existsByMatchAndSpecifier(match, specifier)).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class,
                () -> matchOddsService.createOddForMatch(id, oddDtoToSave));
    }

    @Test
    public void shouldGetOddsForMatch() {
        OddDto savedOddDto = new OddDto(id, specifier, odd);

        when(matchRepository.findById(id)).thenReturn(Optional.of(match));
        when(match.getOdds()).thenReturn(Set.of(MatchOdd.builder().id(id).match(match).odd(odd).specifier(specifier).build()));

        Set<OddDto> result = matchOddsService.getOddsForMatch(id);

        assertEquals(1, result.size());
        assertEquals(savedOddDto, result.stream().findFirst().get());
    }

    @Test
    public void shouldUpdateOdd(){
        UpdateOddDto updateOddDto = new UpdateOddDto(1.99);
        when(matchRepository.findById(id)).thenReturn(Optional.of(match));
        when(matchOddRepository.findByMatch(match)).thenReturn(Set.of(MatchOdd.builder().id(id).match(match).odd(odd).specifier(specifier).build()));

        matchOddsService.updateOdd(id, id, updateOddDto);
        verify(matchOddRepository, times(1)).save(any());

    }

    @Test
    public void shouldNotUpdateOddThatDoesNotExist(){
        UpdateOddDto updateOddDto = new UpdateOddDto(1.99);

        when(matchRepository.findById(id)).thenReturn(Optional.of(match));
        when(matchOddRepository.findByMatch(match)).thenReturn(Set.of(MatchOdd.builder().id(id).match(match).odd(odd).specifier(Specifier.TWO).build()));

        assertThrows(ResourceNotFoundException.class, () -> matchOddsService.updateOdd(id, 2L, updateOddDto));
        verify(matchOddRepository, times(0)).save(any());

    }
}
