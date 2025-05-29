package edu.upc.dsa.services;

import edu.upc.dsa.WebManagerImpl;
import edu.upc.dsa.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import edu.upc.dsa.WebManagerImpl;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import edu.upc.dsa.models.User;
import java.util.List;

@Api(value = "/users", description = "Endpoint for user registration and login")
@Path("")
public class UserService {
    WebManagerImpl wm = WebManagerImpl.getInstance();
    private static final String ADMIN_PASSWORD = "admin123";
    public UserService() {
        if (wm.getAllUsers().isEmpty()) {
            this.wm.RegisterUser("Omar089", "omar@gmail.com", "1234");
            this.wm.RegisterUser("VicPin", "victor@gmail.com", "5678");
            this.wm.RegisterUser("Jan", "jan@gmail.com", "123");
        }

    }

    @Path("users")
    @GET
    @ApiOperation(value = "Get All Users")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        return WebManagerImpl.getInstance().getAllUsers();
    }


    @POST
    @Path("/register")
    @ApiOperation(value = "Register a new user", notes = "Provide username, email, and password")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response register(
            @ApiParam(value = "Username", required = true) @FormParam("username") String username,
            @ApiParam(value = "Email", required = true) @FormParam("correo") String correo,
            @ApiParam(value = "Password", required = true) @FormParam("password") String password) {

        int registro = wm.RegisterUser(username, correo, password);

        switch (registro) {
            case 1: // Registro exitoso
                return Response.ok("{\"status\":true, \"message\":\"Registro Completado\", \"username\":\"" + username + "\", \"correo\":\"" + correo + "\"}")
                        .type(MediaType.APPLICATION_JSON).build();
            case 2: // Usuario ya existe
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"status\":false, \"message\":\"El usuario ya existe\"}")
                        .type(MediaType.APPLICATION_JSON).build();
            case 3: // Correo ya existe
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"status\":false, \"message\":\"El correo ya existe\"}")
                        .type(MediaType.APPLICATION_JSON).build();
            default: // Otro error
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"status\":false, \"message\":\"Error inesperado\"}")
                        .type(MediaType.APPLICATION_JSON).build();
        }
    }




    @POST
    @Path("/login")
    @ApiOperation(value = "Login a user", notes = "Provide email and password")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(
            @ApiParam(value = "Email", required = true) @FormParam("correo") String correo,
            @ApiParam(value = "Password", required = true) @FormParam("password") String password) {

        boolean success = wm.LoginUser(correo, password);
        if (success) {
            String usuario = wm.usuarioPorCorreo(correo);
            String token = JwtUtil.generateToken(usuario);
            System.out.println("login correcto: " + usuario + " token: " + token);
            return Response.ok("{\"status\":true, \"message\":\"Login exitoso\", \"user\":\"" + usuario + "\", \"token\":\"" + token + "\", \"correo\":\"" + correo + "\"}")
                    .type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Credenciales incorrectas\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    @GET
    @Path("/validate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateToken(@HeaderParam("Authorization") String tokenHeader) {
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"valid\":false, \"message\":\"Token no proporcionado o inválido\"}")
                    .build();
        }

        String token = tokenHeader.substring("Bearer ".length());

        if (!JwtUtil.validateToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"valid\":false, \"message\":\"Token inválido o expirado\"}")
                    .build();
        }

        // Extraer usuario del token
        String usuario = JwtUtil.getUsernameFromToken(token);

        // Verificar que el usuario aún exista
        if (!wm.existeUser(usuario)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"valid\":false, \"message\":\"Usuario ya no existe\"}")
                    .build();
        }

        // Todo OK
        System.out.println("Token de usuario: " + usuario +" valido");
        return Response.ok("{\"valid\":true}").build();
    }

    @PUT
    @Path("/actualizarUsuario")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarUsuario(
            @HeaderParam("Authorization") String tokenHeader,
            @FormParam("nuevoUsuario") String nuevoUsuario) {

        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Token no válido\"}").build();
        }

        String token = tokenHeader.substring("Bearer ".length());
        String usuario = JwtUtil.getUsernameFromToken(token);

        // Aquí ya hacemos la verificación de existencia del nuevo usuario
        if (wm.existeUser(nuevoUsuario)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"status\":false, \"message\":\"El nombre de usuario ya está en uso\"}")
                    .build();
        }

        // Si no existe, procedemos a actualizar
        boolean actualizado = wm.actualizarUsuario(usuario, nuevoUsuario);

        if (actualizado) {
            // Si se actualizó exitosamente, generamos nuevo token
            String nuevoToken = JwtUtil.generateToken(nuevoUsuario);
            return Response.ok("{\"status\":true, \"message\":\"Usuario actualizado\", \"newToken\":\"" + nuevoToken + "\"}")
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"status\":false, \"message\":\"Error al actualizar usuario\"}")
                .build();
    }



    @PUT
    @Path("/actualizarCorreo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response actualizarCorreo(
            @HeaderParam("Authorization") String tokenHeader,
            @FormParam("nuevoCorreo") String nuevoCorreo) {

        if (tokenHeader == null || !tokenHeader.startsWith("Bearer "))
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"status\":false, \"message\":\"Token no válido\"}").build();

        String token = tokenHeader.substring("Bearer ".length());
        String usuario = JwtUtil.getUsernameFromToken(token);

        if (wm.actualizarCorreo(usuario, nuevoCorreo)) {
            return Response.ok("{\"status\":true, \"message\":\"Correo actualizado\"}").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"status\":false, \"message\":\"Error al actualizar correo\"}").build();
    }

    @POST
    @Path("/eliminarUsuario")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarUsuario(
            @HeaderParam("Authorization") String tokenHeader,
            @FormParam("contrasena") String contrasena) {

        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Token no proporcionado o inválido\"}")
                    .build();
        }

        String token = tokenHeader.substring("Bearer ".length());
        if (!JwtUtil.validateToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Token inválido o expirado\"}")
                    .build();
        }

        String usuario = JwtUtil.getUsernameFromToken(token);
        User user = WebManagerImpl.getInstance().getUser(usuario);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"status\":false, \"message\":\"Usuario no encontrado\"}")
                    .build();
        }

        if (!user.getPassword().equals(contrasena)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Contraseña incorrecta\"}")
                    .build();
        }

        WebManagerImpl.getInstance().eliminarUsuario(usuario);
        return Response.ok("{\"status\":true, \"message\":\"Usuario eliminado correctamente\"}").build();
    }
    @POST
    @Path("/actualizarContrasena")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarContrasena(
            @HeaderParam("Authorization") String tokenHeader,
            @FormParam("contrasenaActual") String contrasenaActual,
            @FormParam("nuevaContrasena") String nuevaContrasena) {

        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Token no proporcionado o inválido\"}")
                    .build();
        }

        String token = tokenHeader.substring("Bearer ".length());
        if (!JwtUtil.validateToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Token inválido o expirado\"}")
                    .build();
        }

        String usuario = JwtUtil.getUsernameFromToken(token);
        User user = WebManagerImpl.getInstance().getUser(usuario);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"status\":false, \"message\":\"Usuario no encontrado\"}")
                    .build();
        }

        if (!user.getPassword().equals(contrasenaActual)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Contraseña actual incorrecta\"}")
                    .build();
        }

        WebManagerImpl.getInstance().actualizarContrasena(usuario, nuevaContrasena);
        return Response.ok("{\"status\":true, \"message\":\"Contraseña actualizada correctamente\"}")
                .build();
    }
    @POST
    @Path("/eliminarUsuarioAdmin")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarUsuarioAdmin(
            @FormParam("usuario") String usuario,
            @FormParam("adminPassword") String adminPassword) {

        if (!ADMIN_PASSWORD.equals(adminPassword)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Contraseña de administrador incorrecta\"}")
                    .build();
        }

        boolean eliminado = WebManagerImpl.getInstance().eliminarUsuario(usuario);
        if (eliminado) {
            return Response.ok("{\"status\":true, \"message\":\"Usuario eliminado correctamente\"}").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"status\":false, \"message\":\"Usuario no encontrado\"}")
                    .build();
        }
    }

    @POST
    @Path("/actualizarUsuarioAdmin")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarUsuarioAdmin(
            @FormParam("usuario") String usuario,
            @FormParam("nuevoUsuario") String nuevoUsuario,
            @FormParam("adminPassword") String adminPassword) {

        if (!ADMIN_PASSWORD.equals(adminPassword)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Contraseña de administrador incorrecta\"}")
                    .build();
        }

        boolean actualizado = WebManagerImpl.getInstance().actualizarUsuario(usuario, nuevoUsuario);
        return Response.ok("{\"status\":" + actualizado + ", \"message\":\"Usuario actualizado\"}").build();
    }


    @POST
    @Path("/actualizarCorreoAdmin")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarCorreoAdmin(
            @FormParam("usuario") String usuario,
            @FormParam("nuevoCorreo") String nuevoCorreo,
            @FormParam("adminPassword") String adminPassword) {

        if (!ADMIN_PASSWORD.equals(adminPassword)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Contraseña de administrador incorrecta\"}")
                    .build();
        }

        boolean actualizado = WebManagerImpl.getInstance().actualizarCorreo(usuario, nuevoCorreo);
        return Response.ok("{\"status\":" + actualizado + ", \"message\":\"Correo actualizado\"}").build();
    }

    @POST
    @Path("/actualizarContrasenaAdmin")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarContrasenaAdmin(
            @FormParam("usuario") String usuario,
            @FormParam("nuevaContrasena") String nuevaContrasena,
            @FormParam("adminPassword") String adminPassword) {

        if (!ADMIN_PASSWORD.equals(adminPassword)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Contraseña de administrador incorrecta\"}")
                    .build();
        }

        boolean actualizado = WebManagerImpl.getInstance().actualizarContrasena(usuario, nuevaContrasena);
        return Response.ok("{\"status\":" + actualizado + ", \"message\":\"Contraseña actualizada\"}").build();
    }
    @GET
    @Path("/correoPorToken")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCorreoPorToken(@HeaderParam("Authorization") String tokenHeader) {
        // Verifica si el token está presente y tiene el formato adecuado
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Token no proporcionado o inválido\"}")
                    .build();
        }

        // Extrae el token de la cabecera
        String token = tokenHeader.substring("Bearer ".length());

        // Valida el token
        if (!JwtUtil.validateToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Token inválido o expirado\"}")
                    .build();
        }

        // Extrae el nombre de usuario del token
        String username = JwtUtil.getUsernameFromToken(token);

        // Busca al usuario en el sistema usando el nombre de usuario
        User user = WebManagerImpl.getInstance().getUser(username);

        // Verifica si el usuario existe
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"status\":false, \"message\":\"Usuario no encontrado\"}")
                    .build();
        }

        // Devuelve el correo del usuario en la respuesta
        return Response.ok("{\"status\":true, \"email\":\"" + user.getCorreo() + "\"}")
                .build();
    }



}
