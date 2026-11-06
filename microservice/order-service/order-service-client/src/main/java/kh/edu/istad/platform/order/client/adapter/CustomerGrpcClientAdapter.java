package kh.edu.istad.platform.order.client.adapter;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;
import vn.istad.common.grpc.customer.CustomerGrpcServiceGrpc;
import vn.istad.common.grpc.customer.CustomerRequest;
import vn.istad.common.grpc.customer.CustomerResponse;

@Component
public class CustomerGrpcClientAdapter {

    private final CustomerGrpcServiceGrpc.CustomerGrpcServiceBlockingStub customerStub;

    public CustomerGrpcClientAdapter() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        this.customerStub = CustomerGrpcServiceGrpc.newBlockingStub(channel);
    }

    public CustomerResponse getCustomer(String customerId) {
        CustomerRequest request = CustomerRequest.newBuilder()
                .setId(customerId)
                .build();
        return customerStub.getCustomerById(request);
    }
}