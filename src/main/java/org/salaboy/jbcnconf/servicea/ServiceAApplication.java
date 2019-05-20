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
        logger.info("> Service A is now: " + ((on) ? "ON" : "OFF"));
    }

    @GetMapping
    public String sayHelloFromA() {
        return "Hi there from A";
    }

    @PostMapping
    public void turnOnOff() {
        on = !on;
        logger.info("Service A is now: " + ((on) ? "ON" : "OFF"));
    }

    @GetMapping("/status")
    public String serviceStatus() {
        return String.valueOf(on);
    }

    //@TODO: function call per application
    @Scheduled(fixedDelay = 10000)
    public void callFunction() {
        if (on) {
            logger.info(" --- Function example-function-a --- ");
            logger.info("\t >> Calling Downstream Function example-function-a.default.svc.cluster.local");
            WebClient webClient = WebClient.builder().baseUrl("http://" + "example-function-a.default.svc.cluster.local").build();
            webClient.get().uri("/").retrieve()
                    .bodyToMono(String.class)
                    .doOnError(e -> {
                        logger.info("\t >> Example Function A is NOT AVAILABLE.");
                        logger.info(" --- End Error Function example-function-a --- \n ");
                    })
                    .subscribe(r -> {
                        logger.info("\t >> Example Function A output: ");
                        logger.info("\t " + r);
                        logger.info(" --- End OK Function example-function-a --- \n");
                    });
        }
    }

    //@TODO: service-b call per application
    @Scheduled(fixedDelay = 10000, initialDelay = 5000)
    public void callServiceB() {
        if (on) {
            logger.info(" --- Service B --- ");
            logger.info("\t >> Calling Downstream Service service-b");
            WebClient webClient = WebClient.builder().baseUrl("http://" + "service-b").build();
            webClient.get().uri("/").retrieve()
                    .bodyToMono(String.class)
                    .doOnError(e -> {
                        logger.info("\t >> Service B is NOT AVAILABLE.");
                        logger.info(" --- End Error Service B --- \n ");
                    })
                    .subscribe(r -> {
                        logger.info("\t >> Service B output: ");
                        logger.info("\t " + r);
                        logger.info(" --- End OK Service B --- \n");
                    });
        }
    }

}
