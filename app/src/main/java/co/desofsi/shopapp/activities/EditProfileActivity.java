package co.desofsi.shopapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import co.desofsi.shopapp.R;
import co.desofsi.shopapp.models.User;
import co.desofsi.shopapp.routes.Routes;

public class EditProfileActivity extends AppCompatActivity {

    private TextView txt_btn_image;
    private Button btn_save;
    private ImageButton btn_back;
    private ImageView img_user;
    private TextInputLayout ly_name, ly_last_name, ly_phone, ly_movil;
    private TextInputEditText name, phone, ci, address; //last_name
    private User user;

    private ProgressDialog dialog;
    private static final int GALLERY_ADD_PROFILE = 1;
    private Bitmap bitmap = null;
    private SharedPreferences userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        user = (User) getIntent().getExtras().getSerializable("user");
        init();
        eventButtos();
        loadData();
    }

    public void loadData() {
        if (!user.getUrl_image().equals("")) {
            Picasso.get().load(Routes.URL + user.getUrl_image()).into(img_user);
        }
        name.setText(user.getNombre());

       // last_name.setText(user.getLast_name());
        ci.setText(user.getNum_documento());
        phone.setText(user.getTelefono());
        address.setText(user.getDireccion());


    }

    private void init() {
        userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(EditProfileActivity.this);
        dialog.setCancelable(false);
        img_user = findViewById(R.id.edit_profile_img);
        ly_name = findViewById(R.id.edit_profile_name_ly);
      //  ly_last_name = findViewById(R.id.edit_profile_last_name_ly);
        ly_movil = findViewById(R.id.edit_profile_movil_ly);
        ly_phone = findViewById(R.id.edit_profile_phone_ly);
        name = findViewById(R.id.edit_profile_name);
     //   last_name = findViewById(R.id.edit_profile_last_name);
        ci = findViewById(R.id.edit_profile_movil);
        phone = findViewById(R.id.edit_profile_phone);
        address = findViewById(R.id.edit_profile_address);

        txt_btn_image = findViewById(R.id.edit_profile_txt_img);
        btn_save = findViewById(R.id.edit_profile_btn_save);
        btn_back = findViewById(R.id.edit_profile_btn_back);
    }

    public void eventButtos() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validate()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                    builder.setMessage("¿Desea cambiar los datos?")
                            .setCancelable(false)
                            .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    saveProfile();
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
        txt_btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_ADD_PROFILE);
            }
        });
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!name.getText().toString().isEmpty()) {
                    ly_name.setErrorEnabled(false);


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
      /*  last_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!last_name.getText().toString().isEmpty()) {
                    ly_last_name.setErrorEnabled(false);


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        ci.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!ci.getText().toString().isEmpty()) {
                    ly_movil.setErrorEnabled(false);


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!phone.getText().toString().isEmpty()) {
                    ly_phone.setErrorEnabled(false);


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void saveProfile() {
        dialog.setMessage("Actualizando");
        dialog.show();

        final String names = name.getText().toString().trim();
        //final String last_names = last_name.getText().toString().trim();
        final String cis = ci.getText().toString().trim();
        final String phones = phone.getText().toString().trim();
        final String address_u = address.getText().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Routes.UPDATE_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                startActivity(new Intent(EditProfileActivity.this, HomeActivity.class));
                                finish();
                                Toast.makeText(EditProfileActivity.this, "Actualizado", Toast.LENGTH_SHORT).show();

                            }else {
                                String message = object.getString("message");

                                Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EditProfileActivity.this, "Error al guardar", Toast.LENGTH_LONG).show();
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
                map.put("name", names);
               // map.put("last_name", last_names);
                map.put("ci", cis);
                map.put("phone", phones);
                map.put("address", address_u);
                if(bitmap != null){
                    map.put("url_image", bitmapToString(bitmap));
                }

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(EditProfileActivity.this);
        requestQueue.add(stringRequest);

    }

    private String bitmapToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] array = byteArrayOutputStream.toByteArray();
            //  System.out.println("ARCHIVO BASE 64  "+Base64.encodeToString(array, Base64.DEFAULT));
            return Base64.encodeToString(array, Base64.DEFAULT);
        }
        return "#";
    }

    public boolean validate() {

        if (name.getText().toString().isEmpty()) {
            ly_name.setErrorEnabled(true);
            ly_name.setError("El nombre es obligatorio");
            return false;
        }
       /* if (last_name.getText().toString().isEmpty()) {
            ly_last_name.setErrorEnabled(true);
            ly_last_name.setError("El apellido es obligatorio");
            return false;
        }*/
        if (ci.getText().toString().isEmpty()) {
            ly_movil.setErrorEnabled(true);
            ly_movil.setError("La identificación es obligatoria");
            return false;
        }
        if (phone.getText().toString().isEmpty()) {
            ly_phone.setErrorEnabled(true);
            ly_phone.setError("El télefono es obligatorio");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_ADD_PROFILE && resultCode == RESULT_OK) {
            Uri imgUri = data.getData();
            img_user.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}