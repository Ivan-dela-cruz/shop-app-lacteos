package co.desofsi.tiendavirtual.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import co.desofsi.tiendavirtual.R;
import co.desofsi.tiendavirtual.routes.Routes;

public class ChangeasswordActivity extends AppCompatActivity {

    private Button btn_save;
    private TextInputEditText password, confirm_password;
    private TextInputLayout password_ly, cofirm_password_ly;
    private ImageButton btn_back;

    private ProgressDialog dialog;
    private SharedPreferences userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeassword);
        init();
        eventButtons();
    }

    private void init() {
        userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(ChangeasswordActivity.this);
        dialog.setCancelable(false);
        btn_save = findViewById(R.id.edit_change_btn_save);
        btn_back = findViewById(R.id.edit_change_btn_back);
        password = findViewById(R.id.edit_change_pass);
        confirm_password = findViewById(R.id.edit_change_con_pass);
        password_ly = findViewById(R.id.edit_change_pass_ly);
        cofirm_password_ly = findViewById(R.id.edit_change_con_pass_ly);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password.getText().toString().length() > 7) {
                    password_ly.setErrorEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (confirm_password.getText().toString().equals(password.getText().toString())) {
                    cofirm_password_ly.setErrorEnabled(false);


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void eventButtons() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validate()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangeasswordActivity.this);
                    builder.setMessage("¿Desea cambiar la contraseña")
                            .setCancelable(false)
                            .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    changePassword();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }


            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public boolean validate() {


        if (password.getText().toString().length() < 7) {
            password_ly.setErrorEnabled(true);
            password_ly.setError("La contraseña debe tener al menos 8 carácteres");
            return false;
        }
        if (!confirm_password.getText().toString().equals(password.getText().toString())) {
            cofirm_password_ly.setErrorEnabled(true);
            cofirm_password_ly.setError("Las contraseñas no son iguales");
            return false;
        }
        return true;
    }


    private void changePassword() {
        dialog.setMessage("Actualizando");
        dialog.show();

        final String passwords = password.getText().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Routes.UPDATE_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                startActivity(new Intent(ChangeasswordActivity.this, HomeActivity.class));
                                finish();
                                Toast.makeText(ChangeasswordActivity.this, "Actualizado", Toast.LENGTH_SHORT).show();

                            }
                        } catch (Exception e) {
                            dialog.dismiss();
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(ChangeasswordActivity.this, "Error al guardar", Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token", "");
                Map<String, String> map = new HashMap<String, String>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("password", passwords);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ChangeasswordActivity.this);
        requestQueue.add(stringRequest);

    }
}