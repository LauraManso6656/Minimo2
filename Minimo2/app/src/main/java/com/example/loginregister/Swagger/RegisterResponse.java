package com.example.loginregister.Swagger;

public class RegisterResponse {
    private boolean status;
    private String message;
    private String username;
    private String correo;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public String getCorreo() {
        return correo;
    }
}
