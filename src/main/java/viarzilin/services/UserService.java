package viarzilin.services;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import viarzilin.domain.Roles;
import viarzilin.domain.User;
import viarzilin.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
* Service layer for managing user-related operations.
* Handles user registration, activation, profile updates,
* subscriptions, and integration with Spring Security.
*
* @implNote Implements {@link org.springframework.security.core.userdetails.UserDetailsService}
*           for authentication integration.
*/
@Service
public class UserService implements UserDetailsService {

    /** Repository for user data persistence operations */
    private final UserRepository userRepository;

    /** Service for sending emails (activation, notifications) */
    private final MailSenderService mailSenderService;

    /** Encoder for securing user passwords */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new UserService with required dependencies.
     *
     * @param userRepository the user repository
     * @param mailSenderService the mail sending service
     * @param passwordEncoder the password encoder
     */
    public UserService(UserRepository userRepository, MailSenderService mailSenderService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSenderService = mailSenderService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Loads a user by their username for Spring Security authentication.
     *
     * @param username the username to load (non-null)
     * @return the UserDetails for the user
     * @throws UsernameNotFoundException if no user found with given username
     */
    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);

        if (user == null){
            throw new UsernameNotFoundException("User not found!");
        }

        return user;
    }

    /**
     * Registers a new user in the system.
     * <p>
     * The user is set as active, assigned the USER role,
     * provided with an activation code, and their password is encoded.
     * If the user has an email address, an activation message is sent.
     *
     * @param user the user to register (will be modified)
     * @return {@code true} if registration succeeded, {@code false} if username already exists
     */
    public boolean addUser(User user){
        User userFromDb = userRepository.findByUsername(user.getUsername());

        if (userFromDb !=null){
            return false;
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Roles.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        sendMessage(user);

        return true;
    }

    /**
     * Sends an activation email to the user.
     * <p>
     * Only sends if the user has a non-empty email address.
     * The message includes a welcome text and activation link.
     *
     * @param user the user to send activation email to
     */
    private void sendMessage(User user) {
        if(StringUtils.hasText(user.getEmail())){
            String message = String.format(

                    "Hello, %s! \n" +
                            "Welcome to Sweater. Please, visit next link: http://localhost:8080/activate/%s",
                    user.getUsername(),
                    user.getActivationCode()
            );
            mailSenderService.send(user.getEmail(), "Activation code", message);
        }
    }

    /**
     * Activates a user account using the provided activation code.
     * <p>
     * Upon successful activation, the activation code is cleared
     * and the user is saved. Returns whether activation was successful.
     *
     * @param code the activation code to validate
     * @return {@code true} if user was activated, {@code false} if code is invalid
     */
    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);

        if (user == null) {
            return false;
        } else {

            user.setActivationCode(null);
            userRepository.save(user );

            return true;
        }
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all registered users
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Updates a user's roles based on form submission.
     * <p>
     * Clears existing roles and assigns new ones based on the keys
     * in the form map that match valid role names. The username is also updated.
     *
     * @param user the user to update
     * @param username the new username to set
     * @param form the form data containing role assignments (key=role name, value=any)
     */
    public void saveUser(User user, String username, Map<String, String> form) {
        user.setUsername(username);

        Set<String> roles = Arrays.stream(Roles.values())
                .map(Roles::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        form.keySet().stream()
                .filter(roles::contains)
                .forEach(key -> user.getRoles().add(Roles.valueOf(key)));

        userRepository.save(user);
    }

    /**
     * Updates a user's profile information (email and/or password).
     * <p>
     * If the email is changed, a new activation code is generated
     * and an activation email is sent. If a password is provided,
     * it is encoded before saving.
     *
     * @param user the user to update
     * @param password the new password (may be empty; ignored if blank)
     * @param email the new email address
     */
    public void updateProfile(User user, String password, String email) {
        String userEmail = user.getEmail();

        boolean isEmailChanged = !Objects.equals(email, userEmail);

        if(isEmailChanged){
            user.setEmail(email);
            if(StringUtils.hasText(email)){
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }

        if (StringUtils.hasText(password)){
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepository.save(user);

        if (isEmailChanged) {
            sendMessage(user);
        }

    }

    /**
     * Subscribes the current user to another user's content.
     * <p>
     * Adds the current user to the target user's subscribers list
     * and persists the change.
     *
     * @param currentUser the user who is subscribing
     * @param user the user being subscribed to
     */
    public void subscribe(User currentUser, User user) {
        user.getSubscribers().add(currentUser);

        userRepository.save(user);
    }

    /**
     * Unsubscribes the current user from another user's content.
     * <p>
     * Removes the current user from the target user's subscribers list
     * and persists the change.
     *
     * @param currentUser the user who is unsubscribing
     * @param user the user being unsubscribed from
     */
    public void unsubscribe(User currentUser, User user) {
        user.getSubscribers().remove(currentUser);

        userRepository.save(user);
    }
}
