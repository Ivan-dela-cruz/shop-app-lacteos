package co.desofsi.shopapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import co.desofsi.shopapp.R;
import co.desofsi.shopapp.activities.HomeActivity;
import co.desofsi.shopapp.routes.Routes;
import co.desofsi.shopapp.init.AuthActivity;

public class SingInFragment extends Fragment {
    private View view;
    private TextInputLayout layout_email, layout_password;
    private TextInputEditText txt_email, txt_password;
    private Button btn_sin_in;
    private TextView txt_sing_in;
    private ProgressDialog dialog;
    CallbackManager callbackManager;
    LoginButton loginButton;


    public SingInFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sing_in_layout, container, false);
        init();
        return view;
    }

    public void init() {
        layout_email = view.findViewById(R.id.text_email_layout_sing_in);
        layout_password = view.findViewById(R.id.text_password_layout_sing_in);
        txt_email = view.findViewById(R.id.text_email_sing_in);
        txt_password = view.findViewById(R.id.text_password_sing_in);
        btn_sin_in = view.findViewById(R.id.btn_sing_in);
        txt_sing_in = view.findViewById(R.id.text_sing_in);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        loginButton.setFragment(this);

        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        checkLoginStatus();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult c) {
                String token  =  c.getAccessToken().getUserId() + "\n" + "Auth Token: " + c.getAccessToken().getToken();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        txt_sing_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_auth_container, new SingUpFragment()).commit();
            }
        });
        btn_sin_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validated()) {
                    userLogin();
                }

            }
        });
        txt_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txt_email.getText().toString().isEmpty()) {
                    layout_email.setErrorEnabled(false);


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (txt_password.getText().toString().length() > 7) {
                    layout_password.setErrorEnabled(false);


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    AccessTokenTracker accessToken = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if (currentAccessToken == null) {
                // circleImageView.setImageResource(0);
                // Toast.makeText(LoginActivity.this, "User logged out", Toast.LENGTH_SHORT).show();
            } else {
                LoadUserProfile(currentAccessToken);

            }
        }
    };

    private void LoadUserProfile(AccessToken newAccessToken) {

        GraphRequest graphRequest = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                FacebookRequestError error = response.getError();
                if (error != null) {
                    // handle your error
                    return;
                }
                try {
                    String  first_name = object.getString("first_name");
                    String  last_name = object.getString("last_name");
                    String  email = object.getString("email");
                    String id = object.getString("id");

                    String  image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                    Register( first_name,last_name,email,id );


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();

        parameters.putString("fields", "first_name,last_name,email,id");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    private void checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken() != null) {
            //goMainScreen();
        }
    }

    public boolean validated() {

        if (txt_email.getText().toString().isEmpty()) {
            layout_email.setErrorEnabled(true);
            layout_email.setError("El correo es obligatorio");
            return false;
        }
        if (txt_password.getText().toString().length() < 7) {
            layout_password.setErrorEnabled(true);
            layout_password.setError("La contraseña debe tener al menos 8 carácteres");
            return false;
        }

        return true;

    }


    private void userLogin() {
        dialog.setMessage("Iniciando sesión");
        dialog.show();
        final String email = txt_email.getText().toString().trim();
        final String password = txt_password.getText().toString().trim();
        System.out.println(Routes.LOGIN);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Routes.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            System.out.println(object);
                            if (object.getBoolean("success")) {
                                String role = "";
                                JSONObject user = object.getJSONObject("user");
                                SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user", getContext().MODE_PRIVATE);
                                SharedPreferences.Editor editor = userPref.edit();

                                editor.putString("token", object.getString("token"));
                                editor.putInt("id", user.getInt("id"));
                                editor.putString("name", user.getString("nombres"));
                                editor.putString("username", user.getString("usuario"));
                                editor.putString("email", user.getString("email"));
                                editor.putString("url_image", user.getString("url_image"));
                                editor.putBoolean("isLoggedIn", true);
                                //editor.putString("role", object.getString("role"));
                              //  role = object.getString("role");
                                editor.apply();


                                startActivity(new Intent(((AuthActivity) getContext()), HomeActivity.class));
                                ((AuthActivity) getContext()).finish();
                                Toast.makeText(getContext(), "Conectado", Toast.LENGTH_SHORT).show();


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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("email", email);
                map.put("password", password);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }
    private void Register(String first_name  , String last_name  , String email  ,String id ) {
       // dialog.setMessage("Registrando");
       // dialog.show();

        try{
            final String email_f  = email;
            final String password = email;
            final String name_f  = first_name;
            final String user = first_name;
            final String phone = "";
            final String last_name_f = last_name;
            final String external_id = id;
            System.out.println(Routes.REGISTER);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Routes.REGISTER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject object = new JSONObject(response);
                                if (object.getBoolean("success")) {
                                    JSONObject user = object.getJSONObject("user");
                                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user", getContext().MODE_PRIVATE);
                                    SharedPreferences.Editor editor = userPref.edit();
                                    editor.putString("token", object.getString("token"));
                                    editor.putInt("id", user.getInt("id"));
                                    editor.putString("name", user.getString("nombres"));
                                    editor.putString("username", user.getString("usuario"));
                                    editor.putString("url_image", user.getString("url_image"));
                                    editor.putString("external_id", user.getString("external_id"));
                                    editor.putBoolean("isLoggedIn",true);
                                    // editor.putString("role", object.getString("role"));
                                    editor.apply();


                                    startActivity(new Intent(((AuthActivity)getContext()), HomeActivity.class));
                                    ((AuthActivity)getContext()).finish();
                                    Toast.makeText(getContext(), "Conectado", Toast.LENGTH_SHORT).show();
                                } else {
                                    String message = object.getString("message");
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getContext(),"Las credenciales no coinciden",Toast.LENGTH_LONG ).show();
                            System.out.println(error);
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> map = new HashMap<String,String>();
                    map.put("email",email_f);
                    map.put("password",password);
                    map.put("name",name_f);
                    map.put("last_name",last_name_f);
                    map.put("username",user);
                    map.put("phone",phone);
                    map.put("external_id",external_id);
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        }catch (Exception e){
            disconnectFromFacebook();
            Toast.makeText(getContext(),"Lo sentimos existe un problema con la conectividad de internet",Toast.LENGTH_LONG ).show();
        }

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
}
