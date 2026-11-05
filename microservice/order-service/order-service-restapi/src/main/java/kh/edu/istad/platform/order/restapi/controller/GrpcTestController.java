package kh.edu.istad.platform.order.restapi.controller;

import kh.edu.istad.platform.order.client.adapter.CustomerGrpcClientAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.istad.common.grpc.customer.CustomerResponse;

@RestController
@RequestMapping("/test/grpc")
@RequiredArgsConstructor
public class GrpcTestController {

    private final CustomerGrpcClientAdapter customerGrpcClientAdapter;

    @GetMapping("/customers/{id}")
    public CustomerResponse getCustomer(@PathVariable("id") String id) {
        return customerGrpcClientAdapter.getCustomer(id);
    }
}