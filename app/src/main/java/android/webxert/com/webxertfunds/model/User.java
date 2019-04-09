package android.webxert.com.webxertfunds.model;

public class User {
    String id;
    String email;
    String password;
    String created_at;
    String name;

    public User() {
    }

    public User(String id, String email, String password, String created_at, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.created_at = created_at;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
