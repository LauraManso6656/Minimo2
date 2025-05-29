package edu.upc.dsa.services;

import edu.upc.dsa.WebManager;
import edu.upc.dsa.WebManagerImpl;
import edu.upc.dsa.util.JwtUtil;
import edu.upc.dsa.models.Items;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.ws.rs.core.Response;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "/Shop", description = "Endpoint to Shop Service")
@Path("/Shop")
public class ShopService {

    WebManager wm = WebManagerImpl.getInstance();

    public ShopService() {

    }


    @GET
    @Path("/items")
    @ApiOperation(value = "Get all shop items", notes = "Returns a list of all available shop items")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Items> getShopItems() {
        System.out.println("Shop Items are available!");
        return wm.getAllItems();
        //return wm.getAllShopItems();
    }
    @POST
    @Path("/comprar")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Compra múltiples items", notes = "Envía un mapa de ID de ítem y cantidades, junto con el token en el header.")
    public Response comprarItems(
            @HeaderParam("Authorization") String tokenHeader, String itemsString) {
        itemsString = quitarComillas(itemsString);
        // Validación del token
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Token no válido o no proporcionado\"}")
                    .build();
        }

        String token = tokenHeader.substring("Bearer ".length());

        if (!JwtUtil.validateToken(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"status\":false, \"message\":\"Token inválido o expirado\"}")
                    .build();
        }

        String usuario = JwtUtil.getUsernameFromToken(token);
        Map<Integer, Integer> itemsACobrar = parseItemsString(itemsString);
        int resultado = wm.comprarItems(usuario, itemsACobrar);

        switch (resultado) {
            case -1:
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"status\":false, \"message\":\"Usuario no encontrado\"}")
                        .build();
            case -2:
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"status\":false, \"message\":\"Algún item no existe\"}")
                        .build();
            case 0:
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"status\":false, \"message\":\"Dinero insuficiente\"}")
                        .build();
            case 1:
                return Response.ok("{\"status\":true, \"message\":\"Compra exitosa\"}")
                        .build();
            default:
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"status\":false, \"message\":\"Error inesperado\"}")
                        .build();
        }
    }

    private Map<Integer, Integer> parseItemsString(String itemsString) {
        Map<Integer, Integer> map = new HashMap<>();
        if (itemsString == null || itemsString.trim().isEmpty()) return map;

        // Quita espacios y separa por coma
        String[] pairs = itemsString.replaceAll("\\s+", "").split(",");
        for (String pair : pairs) {
            if (pair.isEmpty()) continue;
            String[] kv = pair.split(":");
            if (kv.length == 2) {
                try {
                    int key = Integer.parseInt(kv[0]);
                    int value = Integer.parseInt(kv[1]);
                    map.put(key, value);
                } catch (NumberFormatException e) {
                    // Puedes manejar el error aquí si lo deseas
                }
            }
        }
        return map;
    }
    public static String quitarComillas(String texto) {
        if (texto != null && texto.length() >= 2 &&
                texto.startsWith("\"") && texto.endsWith("\"")) {
            return texto.substring(1, texto.length() - 1);
        }
        return texto;
    }

}