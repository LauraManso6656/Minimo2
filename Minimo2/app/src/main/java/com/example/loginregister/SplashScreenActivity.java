package com.example.loginregister;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.Toast;

public class SplashScreenActivity extends Activity {

    public class Constants {
        public static final String ORIGEN_HOME = "home";
        public static final String ORIGEN_LOGIN = "login";
        public static final String ORIGEN_REGISTER = "register";
        public static final String ORIGEN_LOBBY = "lobby"; // Nuevo origen para la tienda
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        String origen = getIntent().getStringExtra("origen");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            String user = "";
            String gmail = "";
            String token = "";
            String money = "";

            if (origen != null) {
                switch (origen) {
                    case Constants.ORIGEN_HOME:
                        intent = new Intent(this, StartActivity.class);
                        break;
                    case Constants.ORIGEN_LOGIN:
                    case Constants.ORIGEN_REGISTER:
                        intent = new Intent(this, LobbyActivity.class);
                        user = getIntent().getStringExtra("user");
                        gmail = getIntent().getStringExtra("correo");
                        token = getIntent().getStringExtra("token");
                        break;
                    case Constants.ORIGEN_LOBBY:
                        // Ir a `ShopActivity` directamente, sin pasar por Lobby
                        intent = new Intent(this, ShopActivity.class);
                        money = getIntent().getStringExtra("money");
                        token = getIntent().getStringExtra("token");
                        break;
                    default:
                        intent = new Intent(this, StartActivity.class);
                        Toast.makeText(this, "ERROR: DEFAULT CASE", Toast.LENGTH_LONG).show();
                }
            } else {
                intent = new Intent(this, StartActivity.class);
            }


            intent.putExtra("user", user);
            intent.putExtra("correo", gmail);
            intent.putExtra("token", token);
            intent.putExtra("money", money);


            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }, 3000);
    }
}
