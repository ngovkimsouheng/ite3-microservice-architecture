package kh.edu.istad.platform.customer.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import vn.istad.common.grpc.customer.CustomerGrpcServiceGrpc;
import vn.istad.common.grpc.customer.CustomerRequest;
import vn.istad.common.grpc.customer.CustomerResponse;

@GrpcService
public class CustomerGrpcServiceImpl extends CustomerGrpcServiceGrpc.CustomerGrpcServiceImplBase {

    @Override
    public void getCustomerById(CustomerRequest request, StreamObserver<CustomerResponse> responseObserver) {
        // Build mock response (you can plug in your actual repository/service later)
        CustomerResponse response = CustomerResponse.newBuilder()
                .setId(request.getId())
                .setName("Souheng Ngov")
                .setEmail("souheng@istad.edu.kh")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}