package edu.upc.dsa;

import edu.upc.dsa.models.ItemInventarioDTO;
import edu.upc.dsa.models.Users;
import edu.upc.dsa.models.Items;
import edu.upc.dsa.models.UsersScoreDTO;

import java.util.List;
import java.util.Map;

public interface WebManager {

    public int registerUser(String username, String correo, String password);

    public Boolean existeUser(String user);
    public Boolean existeEmail(String email);

    public Boolean loginUser(String correo, String password);

    public List<Items> getAllShopItems();

    public void addShopItem(Items item);

    public List<Users> getAllUsers();
    public Users getUser(String usuario);
    public boolean eliminarUsuario(String usuario);
    public boolean actualizarUsuario(String usuario, String nuevoUsuario);
    public boolean actualizarCorreo(String usuario, String nuevoCorreo);
    public boolean actualizarContrasena(String usuario, String nuevaContrasena);
    public String getCorreo(String usuario);
    public String getUsername(String correo);
    public int comprarItems(String usuario, Map<Integer, Integer> itemsACobrar);
    public List<Items> getAllItems();
    public Integer getMoney(String username);
    public Integer getScore(String username);
    public String getInventarioPorUsuario(String username);
    public List<UsersScoreDTO> getAllUsersScoresDTO();
}
