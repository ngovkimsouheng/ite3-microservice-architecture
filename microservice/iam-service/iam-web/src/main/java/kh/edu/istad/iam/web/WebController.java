package kh.edu.istad.iam.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping(value = {
            "/",
            "/login",
            "/account/**",
            "/admin/**"
    })
    public String spaRoutes() {
        return "forward:/index.html";
    }
}