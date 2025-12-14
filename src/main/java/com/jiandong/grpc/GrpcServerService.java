package com.jiandong.grpc;

import com.jiandong.proto.HelloReply;
import com.jiandong.proto.HelloRequest;
import com.jiandong.proto.SimpleGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class GrpcServerService extends SimpleGrpc.SimpleImplBase {

	private static final Logger log = LoggerFactory.getLogger(GrpcServerService.class);

	@Override
	public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
		log.info("Hello {} ", req.getName());
		if (req.getName().startsWith("error")) {
			throw new IllegalArgumentException("Bad name: " + req.getName());
		}
		if (req.getName().startsWith("internal")) {
			throw new RuntimeException();
		}
		HelloReply reply = HelloReply.newBuilder().setMessage("Hello ==> " + req.getName()).build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}

	@Override
	public void streamHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
		log.info("Hello {} ", req.getName());
		int count = 0;
		while (count < 10) {
			HelloReply reply = HelloReply.newBuilder().setMessage("Hello(" + count + ") ==> " + req.getName()).build();
			responseObserver.onNext(reply);
			count++;
			try {
				Thread.sleep(1000L);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				responseObserver.onError(e);
				return;
			}
		}
		responseObserver.onCompleted();
	}

}
