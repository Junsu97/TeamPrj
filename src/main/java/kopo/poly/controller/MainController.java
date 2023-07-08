package kopo.poly.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class MainController {

    @GetMapping("/main")
    public String main() throws Exception{
        return "/main";
    }

    @GetMapping("/test")
    public String test() throws Exception{
        return "/test";
    }

}
