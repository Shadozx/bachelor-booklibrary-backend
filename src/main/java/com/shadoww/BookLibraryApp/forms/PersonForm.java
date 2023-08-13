package com.shadoww.BookLibraryApp.forms;


import com.shadoww.BookLibraryApp.models.user.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class PersonForm {

    private String username;

    private String password;

    private Role role;

    public boolean isEmpty() {
        return isUsernameEmpty() && isPasswordEmpty() && isRoleEmpty();
    }

    public boolean isUsernameEmpty() {
        return username == null || username.equals("");
    }

    public boolean isPasswordEmpty() {
        return password == null || password.equals("");
    }

    public boolean isRoleEmpty() {
        return role == null;
    }

    @Override
    public String toString() {
        return "PersonForm{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                "role=" + role +
                '}';
    }
}
