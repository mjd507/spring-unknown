package com.mjd507.axon;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final CommandBus commandBus;

    @GetMapping("send-command")
    public void sendCommand() {
        CreateEntityCommand command = new CreateEntityCommand();
        LocalDateTime now = LocalDateTime.now();
        command.setName("name:"+now.getMinute()+"-"+now.getSecond());
        command.setTime(now);
        ArrayList<TestSubEntity> subEntities = new ArrayList<>();
        TestSubEntity subEntity = new TestSubEntity();
        subEntity.setSubName("sub "+command.getName() );
        subEntity.setSubTime(now);
        subEntities.add(subEntity);
        command.setSubEntities(subEntities);
        commandBus.dispatch(new GenericCommandMessage<>(command));
    }
}
