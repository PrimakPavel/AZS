package su.moy.chernihov.mapapplication;

public class BlogPost {
    // имена должны совпадать с именами в БД
    private String author;
    private String message;
    private String rating;
    private String date;


    public BlogPost() {
        // empty default constructor, necessary for Firebase to be able to deserialize blog posts
    }
    public BlogPost(String author, String message, String rating, String date) {
        this.author = author;
        this.message = message;
        this.rating = rating;
        this.date = date;
    }
    public String getAuthor() {
        return author;
    }
    public String getMessage() {
        return message;
    }
    public String getRating() {
        return rating;
    }
    public String getDate() {
        return date;
    }

}
