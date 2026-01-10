package dto;

import enums.UserGender;

public class RegisterDTO {
    private String username;
    private String password;
    private UserGender gender;

    public RegisterDTO() {}

    public RegisterDTO(String username, String password, UserGender gender) {
        this.username = username;
        this.password = password;
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserGender getGender() {
        return gender;
    }

    public void setGender(UserGender gender) {
        this.gender = gender;
    }
}
