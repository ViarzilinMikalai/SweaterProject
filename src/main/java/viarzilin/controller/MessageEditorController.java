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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import viarzilin.domain.Message;
import viarzilin.domain.User;
import viarzilin.domain.dtos.MessageDto;
import viarzilin.repository.MessageRepository;
import viarzilin.services.MessageService;

import java.io.IOException;

@Controller
public class MessageEditorController {

    private final MessageRepository messageRepository;

    private final MessageService messageService;

    @Value("${upload.path}")
    private String uploadPath;

    public MessageEditorController(MessageRepository messageRepository, MessageService messageService) {
        this.messageRepository = messageRepository;
        this.messageService = messageService;
    }

    @GetMapping("/user-messages/{author}")
    public String userMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User author,
            Model model,
            @RequestParam(required = false) Message message,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable
    ){

        Page<MessageDto> pages = messageService.messageListForUser(pageable, currentUser, author);

        model.addAttribute("userChannel", author);
        model.addAttribute("subscriptionsCount", author.getSubscriptions().size());
        model.addAttribute("subscribersCount", author.getSubscribers().size());
        model.addAttribute("isSubscriber", author.getSubscribers().contains(currentUser));
        model.addAttribute("page", pages);
        model.addAttribute("message", message);
        model.addAttribute("isCurrentUser", currentUser.equals(author));
        model.addAttribute("url", "/user-messages/" + author.getId());

        return "userMessages";

    }


    @PostMapping("/user-messages/{user}")
    public String updateMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long user,
            @Valid  Message message,
            @RequestParam("file") MultipartFile file

    ) throws IOException {
        // Если сообщение не найдено в базе по ID, просто делаем редирект обратно
        if (message == null) {
            return "redirect:/user-messages/" + user;
        }

        message.setAuthor(currentUser);
        ControllerUtils.saveFile(message, file, uploadPath);
        messageRepository.save(message);

        return "redirect:/user-messages/" + user;
    }
}
