package com.jiandong.grpc;

import com.jiandong.proto.HelloReply;
import com.jiandong.proto.HelloRequest;
import com.jiandong.proto.HelloServiceGrpc;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.ssl.SslAutoConfiguration;
import org.springframework.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureInProcessTransport;
import org.springframework.boot.grpc.test.autoconfigure.InProcessTestAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.grpc.client.ImportGrpcClients;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = GrpcServerService.class)
@AutoConfigureInProcessTransport
@ImportGrpcClients(basePackageClasses = HelloServiceGrpc.class)
@ImportAutoConfiguration(classes = {
		GrpcServerAutoConfiguration.class,
		SslAutoConfiguration.class,
		InProcessTestAutoConfiguration.class // allow channel to support all target, not just [in-process:]
})
class GrpcServerServiceTest {

	@Autowired
	HelloServiceGrpc.HelloServiceBlockingStub stub;

	@Test
	void contextLoads() {
		HelloRequest request = HelloRequest.newBuilder().setName("Test").build();
		HelloReply reply = stub.sayHello(request);
		assertThat(reply.getMessage()).isEqualTo(("Hello ==> Test"));
	}

}