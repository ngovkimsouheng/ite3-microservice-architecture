package kh.edu.istad.platform.order.client.adapter;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import vn.istad.common.grpc.customer.CustomerGrpcServiceGrpc;
import vn.istad.common.grpc.customer.CustomerRequest;
import vn.istad.common.grpc.customer.CustomerResponse;

@Service
public class CustomerGrpcClientAdapter {

    @GrpcClient("customer-service")
    private CustomerGrpcServiceGrpc.CustomerGrpcServiceBlockingStub customerServiceStub;

    public CustomerResponse getCustomer(String customerId) {
        CustomerRequest request = CustomerRequest.newBuilder()
                .setId(customerId)
                .build();

        return customerServiceStub.getCustomerById(request);
    }
}