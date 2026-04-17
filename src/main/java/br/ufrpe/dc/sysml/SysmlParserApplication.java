package br.ufrpe.dc.sysml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class SysmlParserApplication {

	public static void main(String[] args) {
		SpringApplication.run(SysmlParserApplication.class, args);
	}
	
    @GetMapping("/hello")
    public String sayHello() {
    	System.out.println("In the Spring Boot 3.x app!");
    	return "Hello Updated Parser Spring World!";
    }
}
