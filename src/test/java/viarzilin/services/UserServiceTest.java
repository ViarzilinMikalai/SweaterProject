package viarzilin.services;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import viarzilin.domain.User;
import viarzilin.repository.UserRepository;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private MailSenderService mailSenderService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    void addUser() {
        User user = new User();
        user.setEmail("some@mail.ru");

        userService.addUser(user);

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verify(mailSenderService, Mockito.times(1))
                .send(
                        ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );
    }

    @Test
    void addUserFailTest(){
        User user = new User();
        user.setUsername("John");

        Mockito.doReturn(new User())
                .when(userRepository)
                .findByUsername("John");

        Mockito.verify(userRepository, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
        Mockito.verify(mailSenderService, Mockito.times(0))
                .send(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );

    }

    @Test
void activateUser() {
        User user = new User();
        user.setActivationCode("bingo!");

        Mockito.doReturn(user)
                .when(userRepository)
                .findByActivationCode("activate");

        userService.activateUser("activate");

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }
}
