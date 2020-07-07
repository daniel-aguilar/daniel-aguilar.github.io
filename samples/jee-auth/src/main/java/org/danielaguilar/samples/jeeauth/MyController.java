package org.danielaguilar.samples.jeeauth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }

    @GetMapping("/admin/hello")
    public String helloAdmin() {
        return "Hello, Admin";
    }
}
