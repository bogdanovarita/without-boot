package ru.example.client;

import feign.Param;
import feign.RequestLine;

public interface TestFeignClient {

    @RequestLine("GET /testdomain/{number}")
    TestDomain[] getTestDomains(@Param("number") String number);
}
