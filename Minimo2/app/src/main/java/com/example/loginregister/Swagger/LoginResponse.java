package com.example.loginregister.Swagger;

public class LoginResponse {
    private boolean status;
    private String message;
    private String user;
    private String token;
    private String correo;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public String getCorreo(){return correo;}
}
