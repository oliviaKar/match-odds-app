package com.okarath.assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okarath.assessment.dto.*;
import com.okarath.assessment.entity.Match;
import com.okarath.assessment.entity.MatchOdd;
import com.okarath.assessment.entity.Specifier;
import com.okarath.assessment.entity.Sport;
import com.okarath.assessment.repository.MatchOddRepository;
import com.okarath.assessment.repository.MatchRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchOddRepository matchOddRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    public void contextLoads() {

    }

    @Test
    @Order(2)
    public void testDbConnection() {
        assertNotNull(matchRepository);
        assertNotNull(matchOddRepository);
    }

    @Test
    @Order(3)
    public void shouldCreateMatchAndOdds() throws Exception {
        MatchDto match = MatchDto.builder()
                .teamA("OSFP")
                .teamB("PAO")
                .sport(Sport.BASKETBALL)
                .date("21/02/2025")
                .time("21:30")
                .odds(Set.of(
                        OddDto.builder().specifier(Specifier.ONE).odd(1.4).build(),
                        OddDto.builder().specifier(Specifier.TWO).odd(1.3).build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/matches")
                        .content(objectMapper.writeValueAsString(match))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpect(header().exists("location"));

        assertEquals(1, matchRepository.findAll().size());
        assertEquals(2, matchOddRepository.findAll().size());
    }

    @Test
    @Order(4)
    public void shouldNotRecreateExistingMatch() throws Exception {
        MatchDto match = MatchDto.builder()
                .teamA("OSFP")
                .teamB("PAO")
                .sport(Sport.BASKETBALL)
                .date("21/02/2025")
                .time("15:30")
                .build();

        mockMvc.perform(post("/api/v1/matches")
                        .content(objectMapper.writeValueAsString(match))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"BASKETBALL match between OSFP and PAO on 21/02/2025 already exists.\"}"));

        assertEquals(1, matchRepository.findAll().size());
        assertEquals(2, matchOddRepository.findAll().size());
    }

    @Test
    @Order(5)
    public void shouldCreateMatchWithoutOdds() throws Exception {
        MatchDto match = MatchDto.builder()
                .teamA("PAOK")
                .teamB("PAO")
                .sport(Sport.FOOTBALL)
                .date("15/04/2025")
                .time("17:30")
                .build();

        mockMvc.perform(post("/api/v1/matches")
                        .content(objectMapper.writeValueAsString(match))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated());

        assertEquals(2, matchRepository.findAll().size());
        assertEquals(2, matchOddRepository.findAll().size());
    }

    @Test
    @Order(6)
    public void shouldGetMatch() throws Exception {
        MatchDto expected = MatchDto.builder()
                .id(1L)
                .description("OSFP-PAO")
                .teamA("OSFP")
                .teamB("PAO")
                .sport(Sport.BASKETBALL)
                .date("21/02/2025")
                .time("21:30")
                .odds(Set.of(
                        OddDto.builder().id(1L).specifier(Specifier.ONE).odd(1.4).build(),
                        OddDto.builder().id(2L).specifier(Specifier.TWO).odd(1.3).build()
                ))
                .build();
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/matches/1")
                ).andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertEquals(expected, objectMapper.readValue(responseBody, MatchDto.class));
    }

    @Test
    @Order(7)
    public void shouldReturnNotFoundIfMatchDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/matches/100")
                ).andExpect(status().isNotFound())
                .andExpect(content().string("{\"message\":\"Match with id 100 not found\"}"));

    }

    @Test
    @Order(8)
    public void shouldReturnPaginatedResult() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/matches")
                ).andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();

        MatchPage matchDtos = objectMapper.readValue(contentAsString, MatchPage.class);

        assertEquals(2, matchDtos.getNumberOfElements());
    }

    @Test
    @Order(9)
    public void shouldReturnNotFoundWhenDeleteNonExisting() throws Exception {
        mockMvc.perform(delete("/api/v1/matches/100")
                ).andExpect(status().isNotFound());
        assertEquals(2, matchRepository.findAll().size());
    }

    @Test
    @Order(10)
    public void shouldUpdateMatchDescriptionAndTime() throws Exception {
        UpdateMatchDto updateMatchDto = new UpdateMatchDto("19:00", "New Description");
        mockMvc.perform(patch("/api/v1/matches/1")
                    .content(objectMapper.writeValueAsString(updateMatchDto))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        Match updateMatch = matchRepository.findById(1L).get();
        assertEquals("New Description", updateMatch.getDescription());
        assertEquals(LocalTime.parse("19:00", DateTimeFormatter.ofPattern("HH:mm")), updateMatch.getMatchTime());
    }

    @Test
    @Order(11)
    public void shouldCreteOddForMatch() throws Exception {
        OddDto oddDto = OddDto.builder().specifier(Specifier.X).odd(3.44).build();
        mockMvc.perform(post("/api/v1/matches/1/odds")
                        .content(objectMapper.writeValueAsString(oddDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        assertEquals(3, matchOddRepository.findAll().size());
    }

    @Test
    @Order(12)
    public void shouldNotCreteOddForMatchForExistingSpecifier() throws Exception {
        OddDto oddDto = OddDto.builder().specifier(Specifier.X).odd(3.66).build();
        mockMvc.perform(post("/api/v1/matches/1/odds")
                        .content(objectMapper.writeValueAsString(oddDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(3, matchOddRepository.findAll().size());
    }

    @Test
    @Order(13)
    public void shouldUpdateOddForMatch() throws Exception {
        UpdateOddDto oddDto = new UpdateOddDto(5.88);
        String s  = objectMapper.writeValueAsString(oddDto);
        mockMvc.perform(patch("/api/v1/matches/1/odds/3")
                        .content(objectMapper.writeValueAsString(oddDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertEquals(3, matchOddRepository.findAll().size());
        MatchOdd updatedOdd = matchOddRepository.findById(3L).get();
        assertEquals(5.88, updatedOdd.getOdd());
    }

    @Test
    @Order(14)
    public void shouldDeleteOddForMatch() throws Exception {
        mockMvc.perform(delete("/api/v1/matches/1/odds/3"))
                .andExpect(status().isNoContent());

        assertEquals(2, matchOddRepository.findAll().size());

    }


    @Test
    @Order(100) //last one
    public void shouldDeleteMatchAndOdds() throws Exception {
        mockMvc.perform(delete("/api/v1/matches/1")
            ).andExpect(status().isNoContent());
        assertEquals(1, matchRepository.findAll().size());
        assertEquals(0, matchOddRepository.findAll().size());
    }


}
