package viarzilin.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import viarzilin.domain.User;
import viarzilin.domain.dtos.CaptchaResponseDto;
import viarzilin.services.UserService;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Controller
public class RegistrationController {

    private static final String CAPTCHA_URL= "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";
    private static final String REGISTRATION_URL = "/registration";
    private static final String REGISTRATION = "registration";
    private static final String MESSAGE_TYPE = "messageType";
    private static final String MESSAGE = "message";

    private final UserService userService;

    @Value("${recaptcha.secret}")
    private String secret;

    final
    RestTemplate restTemplate;

    public RegistrationController(UserService userService, RestTemplate restTemplate) {
        this.userService = userService;
        this.restTemplate = restTemplate;
    }

    @GetMapping(REGISTRATION_URL)
    public String registration() {
        return REGISTRATION;
    }

    @PostMapping(REGISTRATION_URL)
    public String addUser(
            @RequestParam("passwordConfirm") String passwordConfirm,
            @RequestParam("g-recaptcha-response") String captchaResponce,
            @Valid User user,
            BindingResult bindingResult,
            Model model
    ){
        String url = String.format(CAPTCHA_URL, secret, captchaResponce);
        CaptchaResponseDto captchaResponseDto = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);

        assert captchaResponseDto != null;
        if (!captchaResponseDto.success){
            model.addAttribute("captchaError", "Fill Captcha");
        }

        boolean isConfirmEmpty = !StringUtils.hasText(passwordConfirm);

        if (isConfirmEmpty) {
            model.addAttribute("passwordConfirmError","Password confirmation can't be empty");
        }

        if (user.getPassword() != null && !Objects.equals(user.getPassword(), passwordConfirm)){
            model.addAttribute("passwordError", "Passwords are differents!");
        }

        if (isConfirmEmpty || bindingResult.hasErrors() || !captchaResponseDto.success){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);

            return REGISTRATION;
        }

        if(!userService.addUser(user)){
            model.addAttribute("usernameError", "User exists!");

            return REGISTRATION;
        } else {
            model.addAttribute(MESSAGE, "You must activate your account. Activation link sent to your email.");
            model.addAttribute(MESSAGE_TYPE, "info");

            return "login";
        }
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        boolean isActivated = userService.activateUser(code);

        if(isActivated){
            model.addAttribute(MESSAGE_TYPE, "success");
            model.addAttribute(MESSAGE, "User successfully activated");
        } else {
            model.addAttribute(MESSAGE_TYPE, "danger");
            model.addAttribute(MESSAGE, "Activation code is not found!");
        }

        return "login";
    }
}
