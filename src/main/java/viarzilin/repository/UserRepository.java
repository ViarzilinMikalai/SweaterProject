package viarzilin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import viarzilin.domain.User;

public interface UserRepository extends JpaRepository <User, Long> {
    User findByUsername(String username);

    User findByActivationCode(String code);
}
