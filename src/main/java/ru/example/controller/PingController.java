package ru.example.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.client.TestFeignClient;
import ru.example.client.TestDomain;

@RestController
@RequiredArgsConstructor
public class PingController {

    private final TestFeignClient testFeignClient;

    @GetMapping(value = "/hello")
    public ResponseEntity<String> greet() {
        return new ResponseEntity<>("{\"msg\": \"Hello World\"}", HttpStatus.OK);
    }

    @GetMapping("/ccgate")
    public ResponseEntity<TestDomain[]> getTestDomains() {
        return ResponseEntity.ok()
                .body(testFeignClient.getTestDomains("number-test"));
    }
}
