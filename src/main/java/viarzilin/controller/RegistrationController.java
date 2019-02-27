package viarzilin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import viarzilin.domain.Roles;
import viarzilin.domain.User;
import viarzilin.repository.UserRepository;

import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Map<String, Object> model){
        User userFromDb = userRepository.findByUsername(user.getUsername());
        String message = "User exists!";
        if(userFromDb != null){
            model.put("message", message);
            return "registration";
        } else {
            user.setActive(true);
            user.setRoles(Collections.singleton(Roles.USER));
            userRepository.save(user);
            return "redirect:/login";
        }
    }
}
