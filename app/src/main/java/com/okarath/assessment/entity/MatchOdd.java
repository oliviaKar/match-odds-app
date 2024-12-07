package com.okarath.assessment.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "match_odds",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "match_id", "specifier" })})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchOdd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "match_id", referencedColumnName = "id", nullable = false)
    private Match match;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Specifier specifier;

    @Column(nullable = false)
    private double odd;
}
