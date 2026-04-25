package viarzilin.domain.dtos;

import lombok.Getter;
import viarzilin.domain.Message;
import viarzilin.domain.User;

@Getter
public class MessageDto {
    private final Long id;
    private final String text;
    private final String tag;
    private final User author;
    private final String filename;
    private final Long likes;
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
}
