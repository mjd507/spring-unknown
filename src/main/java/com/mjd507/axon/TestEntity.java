package com.mjd507.axon;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "test_entity")
@Data
@NoArgsConstructor
@Aggregate
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @AggregateIdentifier
    private long id;

    @Column
    private String name;

    @Column
    private LocalDateTime time;

    @AggregateMember
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "entityId")
    private List<TestSubEntity> subEntities;

    @CommandHandler
    public TestEntity(CreateEntityCommand command) {
        this.name = command.getName();
        this.time = command.getTime();
        this.subEntities = command.getSubEntities();
    }
}
