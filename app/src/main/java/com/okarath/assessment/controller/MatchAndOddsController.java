package com.okarath.assessment.controller;

import com.okarath.assessment.dto.*;
import com.okarath.assessment.service.MatchOddsService;
import com.okarath.assessment.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchAndOddsController {

    private final MatchService matchService;
    private final MatchOddsService matchOddsService;


    public MatchAndOddsController(MatchService matchService, MatchOddsService matchOddsService) {
        this.matchService = matchService;
        this.matchOddsService = matchOddsService;
    }

    @Operation(summary = "Create match")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Match created"),
            @ApiResponse(responseCode = "400", description = "Match already exists for teamA, teamB, date and sport")
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Void> createMatch(@Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(exampleMatchDto)))
                                                @RequestBody MatchDto match) {
        var savedMatch = matchService.save(match);
        var url = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .build(savedMatch.id());
        return ResponseEntity.created(url).build();
    }

    @Operation(summary = "Get match by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MatchDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Match not found", content = @Content)
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<MatchDto> getMatchById(@PathVariable Long id) {
        MatchDto match = matchService.findById(id);
        return ResponseEntity.ok(match);
    }

    @Operation(summary = "Get all matches paginated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match list retrieved",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MatchPage.class))})
    })
    @GetMapping()
    public ResponseEntity<MatchPage> getMatches(@ParameterObject Pageable pageable) {
        Page<MatchDto> matches = matchService.getMatches(pageable);

        return ResponseEntity.ok(new MatchPage(matches.getContent(), pageable, matches.getContent().size()));
    }

    @Operation(summary = "Delete match by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Match deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Match not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        var deleted = matchService.delete(id);
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }


    @Operation(summary = "Update match description and/or time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Match updated successfully"),
            @ApiResponse(responseCode = "404", description = "Match not found", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateMatch(@PathVariable Long id,
                                            @Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(exampleUpdateMatchDto)))
                                            @RequestBody UpdateMatchDto updateMatchDto){

        matchService.updateMatch(id, updateMatchDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create odd for match")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Match odd created successfully"),
            @ApiResponse(responseCode = "400", description = "Match odd already exists for the specified specifier", content = @Content),
            @ApiResponse(responseCode = "404", description = "Match not found", content = @Content)
    })
    @PostMapping(path = "/{matchId}/odds", consumes = "application/json")
    public ResponseEntity<Void> createOddForMatch(@PathVariable Long matchId,
                                                  @Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(exampleOddDto)))
                                                  @RequestBody OddDto oddDto){
        matchOddsService.createOddForMatch(matchId, oddDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Get odds for match")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match odds retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Match not found", content = @Content)
    })
    @GetMapping("/{matchId}/odds")
    public ResponseEntity<Set<OddDto>> getOddsForMatch(@PathVariable Long matchId){
        var odds = matchOddsService.getOddsForMatch(matchId);
        return ResponseEntity.ok(odds);
    }

    @Operation(summary = "Update odd for match")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Match odd updated successfully"),
            @ApiResponse(responseCode = "404", description = "Match not found", content = @Content)
    })
    @PatchMapping("/{matchId}/odds/{oddId}")
    public ResponseEntity<Void> updateOddForMatch(@PathVariable Long matchId, @PathVariable Long oddId,
                                                  @Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(exampleUpdateOddDto)))
                                                  @RequestBody UpdateOddDto oddDto) {
        matchOddsService.updateOdd(matchId, oddId, oddDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update odd for match")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Match odd deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Match odd not found", content = @Content)
    })
    @DeleteMapping("/{matchId}/odds/{oddId}")
    public ResponseEntity<Void> deleteOddForMatch(@PathVariable Long matchId, @PathVariable Long oddId) {
        var deleted = matchOddsService.delete(matchId, oddId);
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private static final String exampleMatchDto = """
            {
              "date": "13/10/2025",
              "time": "11:45",
              "description": "match",
              "teamA": "OSFP",
              "teamB": "PAOK",
              "sport": "FOOTBALL",
              "odds": [
                {
                  "specifier": "ONE",
                  "odd": 1.2
                }
              ]
            }""";

    private static final String exampleUpdateMatchDto = """
            {
              "time": "15:50",
              "description": "another description"
            }""";

    private static final String exampleOddDto = """
            {
              "specifier": "ONE",
              "odd": 1.3
            }""";
    private static final String exampleUpdateOddDto = """
            {
              "odd": 1.3
            }""";

}
