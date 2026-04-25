package viarzilin.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import viarzilin.domain.Message;
import viarzilin.domain.User;
import viarzilin.domain.dtos.MessageDto;
import viarzilin.repository.MessageRepository;
import viarzilin.services.MessageService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


@Controller
public class MessageController {


    private final MessageRepository messageRepository;

    private final MessageService messageService;

    @Value("${upload.path}")
    private String uploadPath;

    public MessageController(MessageRepository messageRepository, MessageService messageService) {
        this.messageRepository = messageRepository;
        this.messageService = messageService;
    }

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String filter,
                       Model model,
                       @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC)Pageable pageable,
                       @AuthenticationPrincipal User user
                       ) {
        Page<MessageDto> page = messageService.messageList(pageable, filter, user);

        model.addAttribute("page", page);
        model.addAttribute("url", "/main");
        model.addAttribute("filter", filter);

        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @Valid Message message,
            BindingResult bindingResult,
            Model model,
            @RequestParam("file") MultipartFile file,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable
    ) throws IOException {
            message.setAuthor(user);

            if (bindingResult.hasErrors()){
                Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
                model.mergeAttributes(errorsMap);
                model.addAttribute("message", message);
            } else {
                ControllerUtils.saveFile(message, file, uploadPath);

                model.addAttribute("message", null);
                messageRepository.save(message);
            }

        Iterable<Message> messages = messageRepository.findAll();
        model.addAttribute("messages", messages);

        Page<MessageDto> page = messageService.messageList(pageable, "", user);

        model.addAttribute("page", page);
        model.addAttribute("url", "/main");

        return "main";
    }

    @GetMapping("/messages/{message}/like")
    public String like(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Message message,
            RedirectAttributes redirectAttributes,
            @RequestHeader(required = false) String referer
    ) {
        Set<User> likes = message.getLikes();

        if (likes.contains(currentUser)) {
            likes.remove(currentUser);
        } else {
            likes.add(currentUser);
        }

        if (StringUtils.hasText(referer)) {
            UriComponents components;
            components = UriComponentsBuilder.fromUriString(referer).build();

            // Копируем параметры (для сохранения пагинации/поиска)
            components.getQueryParams().forEach(redirectAttributes::addAttribute);

            return "redirect:" + components.getPath();
        }

        return "redirect:/main";
    }
}
