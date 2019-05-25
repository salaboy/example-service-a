package org.salaboy.jbcnconf.servicea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@RestController
@EnableScheduling
public class ServiceAApplication implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(ServiceAApplication.class);

    // Is the service On?
    private boolean on = false;

    @Value("${my.application.name}:default-app")
    private String applicationName;

    public static void main(String[] args) {
        SpringApplication.run(ServiceAApplication.class,
                args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info(" --------- STARTED ---------- ");
        this.on = true;
        logger.info("> Service A is now: " + ((on) ? "ON" : "OFF") + "\n\n");

    }

    @GetMapping
    public String sayHelloFromA() {
        return "Hi there from A";
    }

    @PostMapping
    public void turnOnOff() {
        on = !on;
        logger.info("Service A is now: " + ((on) ? "ON" : "OFF") + "\n\n");
    }

    @GetMapping("/status")
    public String serviceStatus() {
        return String.valueOf(on);
    }

    //@TODO: function call per application
    @Scheduled(fixedDelay = 10000)
    public void callFunction() {
        if (on) {
            logger.info(" --- Function A --- ");
            logger.info("\t >> Calling Downstream Function A");
            WebClient webClient = WebClient.builder().baseUrl("http://" + "example-function-a.default.svc.cluster.local").build();
            webClient.get().uri("/").retrieve()
                    .bodyToMono(String.class)
                    .doOnError(e -> {
                        logger.info("\t >> Function A is NOT AVAILABLE.");
                        logger.info(" --- End Error Function A --- \n\n ");
                    })
                    .subscribe(r -> {
                        logger.info("\t >> Function A Output: ");
                        logger.info("\t " + r);
                        logger.info(" --- End OK Function A --- \n\n");
                    });
        }
    }

    //@TODO: my-service-b call per application
    @Scheduled(fixedDelay = 10000, initialDelay = 5000)
    public void callServiceB() {
        if (on) {
            logger.info(" --- Service B --- ");
            logger.info("\t >> Calling Downstream Service my-service-b");
            WebClient webClient = WebClient.builder().baseUrl("http://" + "my-service-b").build();
            webClient.get().uri("/").retrieve()
                    .bodyToMono(String.class)
                    .doOnError(e -> {
                        logger.info("\t >> Service B is NOT AVAILABLE.");
                        logger.info(" --- End Error Service B --- \n\n ");
                    })
                    .subscribe(r -> {
                        logger.info("\t >> Service B Output: ");
                        logger.info("\t " + r);
                        logger.info(" --- End OK Service B --- \n\n");
                    });
        }
    }

}
