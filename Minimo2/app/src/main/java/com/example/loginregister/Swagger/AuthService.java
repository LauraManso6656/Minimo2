package com.example.loginregister.Swagger;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/TocaBolas/login")
    @FormUrlEncoded
    Call<LoginResponse> login(
            @retrofit2.http.Field("correo") String correo,
            @retrofit2.http.Field("password") String password
    );

    @FormUrlEncoded
    @POST("/TocaBolas/register")
    Call<RegisterResponse> register(
            @Field("username") String username,
            @Field("correo") String correo,
            @Field("password") String password
    );

    @GET("/TocaBolas/Shop/items")
    Call<List<ShopItem>> getShopItems();

    @FormUrlEncoded
    @POST("/TocaBolas/actualizarContrasena")
    Call<GenericResponse> actualizarContrasena(
            @Header("Authorization") String token,
            @Field("contrasenaActual") String contrasenaActual,
            @Field("nuevaContrasena") String nuevaContrasena
    );

    @FormUrlEncoded
    @POST("/TocaBolas/eliminarUsuario")
    Call<GenericResponse> eliminarUsuario(
            @Header("Authorization") String token,
            @Field("contrasena") String contrasena
    );

    @GET("/TocaBolas/userStats")
    Call<UserStatsResponse> getUserStats(
            @Header("Authorization") String token
    );


    @POST("/TocaBolas/Shop/comprar")
    @Headers("Content-Type: text/plain")
    Call<GenericResponse> comprarItems(
            @Header("Authorization") String token,
            @Body String itemsString
    );

    @GET("/TocaBolas/inventario")
    Call<List<InventoryResponse>> getInventario(
            @Header("Authorization") String username
    );

}
