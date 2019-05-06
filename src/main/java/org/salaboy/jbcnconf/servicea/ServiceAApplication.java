package org.salaboy.jbcnconf.servicea;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@RestController
public class ServiceAApplication implements CommandLineRunner {


    public static void main(String[] args) {
        SpringApplication.run(ServiceAApplication.class,
                args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(" -------------------------- ");
        WebClient webClient = WebClient.builder().baseUrl("http://" + "service-b" + ".knativetutorial.svc.cluster.local").build();
        webClient.get().uri("/").retrieve()
                .bodyToMono(String.class).subscribe(System.out::print);

    }

    @GetMapping
    public String sayHello() {
        return "hi there from service A";
    }


}
