package org.salaboy.jbcnconf.servicea;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@RestController
@EnableScheduling
public class ServiceAApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ServiceAApplication.class,
                args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(" --------- STARTED ---------- ");

    }

    @GetMapping
    public String sayHello() {
        return "Hi there from service A";
    }


    @Scheduled(fixedDelay = 5000)
    public void callFunction() {
        System.out.println(" --------------------------> Calling Function ");
        WebClient webClient = WebClient.builder().baseUrl("http://" + "example-function-a" + ".knativetutorial.svc.cluster.local").build();
        webClient.get().uri("/").retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> System.out.println(">> Example Function A is not available, return a default answer"))
                .subscribe(System.out::print);
    }


    @Scheduled(fixedDelay = 5000)
    public void callServiceB() {
        System.out.println(" --------------------------> Calling service ");
        WebClient webClient = WebClient.builder().baseUrl("http://" + "service-b").build();
        webClient.get().uri("/").retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> System.out.println(">> Example Service B is not available, return a default answer"))
                .subscribe(System.out::print);
    }

}
