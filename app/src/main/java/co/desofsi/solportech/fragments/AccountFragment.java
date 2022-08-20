package co.desofsi.solportech.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import co.desofsi.solportech.R;
import co.desofsi.solportech.activities.ChangeasswordActivity;
import co.desofsi.solportech.activities.EditProfileActivity;
import co.desofsi.solportech.activities.HomeActivity;
import co.desofsi.solportech.init.AuthActivity;
import co.desofsi.solportech.models.User;
import co.desofsi.solportech.routes.Routes;
import de.hdodenhof.circleimageview.CircleImageView;


public class AccountFragment extends Fragment {
    private View view;
    private TextView txt_name, txt_address, txt_phone, txt_dni, txt_email, txt_user;
    private Button btn_logout, btn_edit;
    private ImageView btn_edit_pass;
    private CircleImageView img_user;
    private ProgressDialog dialog;
    SharedPreferences sharedPreferences;
    private User user_login;
    String external_id = "";
    public AccountFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);
        init();
        eventsButtons();
        getProfile();
        return view;
    }

    public void init() {
        dialog = new ProgressDialog(getContext());
        user_login = new User();
        dialog.setCancelable(false);
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        external_id = sharedPreferences.getString("external_id", "");
        txt_name = view.findViewById(R.id.account_fragment_txt_name);
        txt_address = view.findViewById(R.id.account_fragment_txt_address);
        txt_dni = view.findViewById(R.id.account_fragment_txt_dni);
        txt_phone = view.findViewById(R.id.account_fragment_txt_phone);
        txt_email = view.findViewById(R.id.account_fragment_txt_email);

        btn_edit = view.findViewById(R.id.account_fragment_btn_edit);
        btn_edit_pass = view.findViewById(R.id.account_fragment_btn_edit_pass);
        btn_logout = view.findViewById(R.id.account_fragment_btn_logout);
        txt_user = view.findViewById(R.id.account_fragment_txt_user);
        img_user = view.findViewById(R.id.account_fragment_image);

    }

    public void eventsButtons() {
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(((HomeActivity) getContext()));
                builder.setMessage("¿Desea cerrar sesión?")
                        .setCancelable(false)
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               if(external_id.equalsIgnoreCase("")){
                                   logout();
                               }else{
                                   disconnectFromFacebook();
                                   logout();
                               }

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
        });
        btn_edit_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ChangeasswordActivity.class));
            }
        });
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                intent.putExtra("user", user_login);
                startActivity(intent);
            }
        });
    }
    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }
    public void logout() {
        dialog.setMessage("Cerrando sesión");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Routes.LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                startActivity(new Intent(((HomeActivity) getContext()), AuthActivity.class));
                                ((HomeActivity) getContext()).finish();
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
                        Toast.makeText(getContext(), "Las credenciales no coinciden", Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                Map<String, String> map = new HashMap<String, String>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void getProfile() {
        dialog.setMessage("Cargando");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Routes.GET_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONObject profile = object.getJSONObject("profile");
                                JSONObject user = object.getJSONObject("user");

                                User user_profile = new User();
                                user_profile.setProfile_id(profile.getInt("id"));
                                user_profile.setNombre(profile.getString("nombre"));
                                user_profile.setTipo_documento(profile.getString("tipo_documento"));
                                user_profile.setNum_documento(profile.getString("num_documento"));
                                user_profile.setDireccion(profile.getString("direccion"));
                                user_profile.setTelefono(profile.getString("telefono"));
                                user_profile.setUser_id(user.getInt("id"));
                                user_profile.setEmail(user.getString("email"));
                                user_profile.setUsuario(user.getString("usuario"));
                                user_profile.setUrl_image(profile.getString("url_image"));

                                user_login = user_profile;
                                loadProfile(user_profile);

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
                        Toast.makeText(getContext(), "Las credenciales no coinciden", Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                Map<String, String> map = new HashMap<String, String>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void loadProfile(User user) {

        txt_name.setText(user.getNombre());
        txt_dni.setText(user.getNum_documento());
        txt_address.setText(user.getDireccion());
        txt_email.setText(user.getEmail());
        txt_phone.setText(user.getTelefono());
       // txt_user.setText(user.getUsuario());
        if (!user.getUrl_image().equals("")) {
            Picasso.get().load(Routes.URL + user.getUrl_image()).into(img_user);
        }

    }
}
