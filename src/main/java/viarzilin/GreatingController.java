package viarzilin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class GreatingController {

    @GetMapping("greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue ="World") String name, Map<String, Object> model){
//        model.addAttribute("name", name);
        model.put("name", name);
        return "greeting";
    }

    @GetMapping
    public String main(Map<String, Object> model){
        model.put("some", "Hello, letsCode!");
        return "main";
    }
}
