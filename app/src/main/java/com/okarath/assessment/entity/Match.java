package com.okarath.assessment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "matches",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "teamA", "teamB", "sport", "date" })})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    @Column(nullable = false, name = "match_date")
    private LocalDate matchDate;

    @Column(nullable = false, name = "match_time")
    private LocalTime matchTime;

    @Column(nullable = false, name = "team_a")
    private String teamA;

    @Column(nullable = false, name = "team_b")
    private String teamB;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "INTEGER")
    private Sport sport;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MatchOdd> odds = new HashSet<>();
}
