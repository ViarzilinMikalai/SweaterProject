package viarzilin.domain.dtos;

import lombok.Getter;
import lombok.ToString;
import viarzilin.domain.Message;
import viarzilin.domain.User;
import viarzilin.domain.util.MessageHelper;

import java.util.Objects;

@Getter
@ToString
public class MessageDto {
    private Long id;
    private String text;
    private String tag;
    private User author;
    private String filename;
    private Long likes;
    boolean meLiked;


    public MessageDto(Message message, Long likes, boolean meLiked) {
        this.id = message.getId();
        this.text = message.getText();
        this.tag = message.getTag();
        this.author = message.getAuthor();
        this.filename = message.getFilename();
        this.likes = likes;
        this.meLiked = meLiked;
    }


    public boolean isMeLiked() {
        return meLiked;
    }
    @Override
    public String toString() {
        return "MessageDto{" +
                "id=" + id +
                ", author=" + author +
                ", likes=" + likes +
                ", meLiked=" + meLiked +
                '}';
    }

    //    public String getAuthorName(){
//        return MessageHelper.getAuthorName(author);
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public String getText() {
//        return text;
//    }
//
//    public String getTag() {
//        return tag;
//    }
//
//    public User getAuthor() {
//        return author;
//    }
//
//    public String getFilename() {
//        return filename;
//    }
//
//    public Long getLikes() {
//        return likes;
//    }


}
