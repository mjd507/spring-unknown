package com.jiandong.axon;

import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateEntityCommand {
    @TargetAggregateIdentifier
    private long id;
    private String name;
    private LocalDateTime time;

    private List<TestSubEntity> subEntities;
}
