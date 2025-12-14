package com.jiandong.grpc;

import com.jiandong.proto.HelloReply;
import com.jiandong.proto.HelloRequest;
import com.jiandong.proto.SimpleGrpc;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.ssl.SslAutoConfiguration;
import org.springframework.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration;
import org.springframework.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureInProcessTransport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {
		"spring.grpc.client.default-channel.address=localhost:8080"
})
@SpringBootTest(classes = GrpcServerService.class)
@AutoConfigureInProcessTransport
@ImportAutoConfiguration(classes = {
		GrpcServerAutoConfiguration.class,
		GrpcServerFactoryAutoConfiguration.class,
		SslAutoConfiguration.class,
})
class GrpcServerServiceTest {

	@Autowired
	SimpleGrpc.SimpleBlockingStub stub;

	@Test
	void contextLoads() {
		HelloRequest request = HelloRequest.newBuilder().setName("Test").build();
		HelloReply reply = stub.sayHello(request);
		assertThat(reply.getMessage()).isEqualTo(("Hello ==> Test"));
	}

}
