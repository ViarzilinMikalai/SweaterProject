package viarzilin.domain;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import viarzilin.domain.util.MessageHelper;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotBlank(message =  "Please fill the Message")
    @Length(max = 2048, message = "Message too long (more than 2Kb)")
    private String text;

    @Length(max = 255, message = "Tag too long (more than 255b)")
    private String tag;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    private String filename;

    @ManyToMany
    @JoinTable(
            name = "message_likes",
            joinColumns = {@JoinColumn(name = "message_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<User> likes = new HashSet<>();

    public String getAuthorName(){
        return MessageHelper.getAuthorName(author);
    }

    public Message(String text, String tag, User user) {
        this.author = user;
        this.text = text;
        this.tag = tag;
    }
}
