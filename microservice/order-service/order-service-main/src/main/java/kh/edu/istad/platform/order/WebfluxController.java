package kh.edu.istad.platform.order;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Controller
public class WebfluxController {

    @RestController
    @RequestMapping("/api/v1")
    public class WebFluxController {
        @GetMapping("/flux")
        public Flux<String> healthCheck() {
            return Flux.just("A", "B", "C", "D", "F", "0")
                    .log()
                    .delayElements(Duration.ofSeconds(1));
        }
    }

}
