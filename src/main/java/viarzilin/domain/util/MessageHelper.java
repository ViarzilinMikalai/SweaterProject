package viarzilin.domain.util;

import viarzilin.domain.User;

public abstract class MessageHelper {
    public static String getAuthorName(User author){
       return author != null ? author.getUsername() : "none";
    }
}
