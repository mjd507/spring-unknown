package com.jiandong.axon;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.EntityId;

import java.time.LocalDateTime;
@Entity(name = "test_sub_entity")
@Data
@NoArgsConstructor
public class TestSubEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    @EntityId
    private long entityId;

    @Column
    private String subName;

    @Column
    private LocalDateTime subTime;
}
