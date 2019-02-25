package viarzilin.repository;

import org.springframework.data.repository.CrudRepository;
import viarzilin.domain.Message;

import java.util.List;

public interface MessageRepository extends CrudRepository <Message, Long> {
    List<Message> findByTag(String tag);
}
