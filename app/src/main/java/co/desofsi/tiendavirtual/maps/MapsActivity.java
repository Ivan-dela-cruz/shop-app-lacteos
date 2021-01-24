package co.desofsi.tiendavirtual.maps;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.desofsi.tiendavirtual.R;
import co.desofsi.tiendavirtual.activities.ListCategoriesActivity;
import co.desofsi.tiendavirtual.routes.Routes;
import co.desofsi.tiendavirtual.models.Company;
import co.desofsi.tiendavirtual.models.TypeCompany;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ArrayList<Company> lis_companies;
    private TypeCompany typeCompany_selected;
    private SharedPreferences sharedPreferences;
    private ImageButton btn_back;
    private ArrayList<MarkerOptions> rutas;
    LocationManager localizar;
    Location llocaliza;
    JSONObject object2;
    SupportMapFragment mapFragment;

    ///DETALLE MAPA
    ImageView imgmarker;
    private BottomSheetBehavior mBottomSheetBehavior1;
    private RoundedImageView image;
    private TextView txt_name, txt_description, txt_address, txt_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        init();
        getPositionUser();
        getCompanies();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void init() {
        object2 = new JSONObject();
        typeCompany_selected = (TypeCompany) getIntent().getExtras().getSerializable("type_company_selected");
        sharedPreferences = MapsActivity.this.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_back = findViewById(R.id.map_companies_btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();


        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMarkerClickListener(this);

        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(positionUser, 16.0f));
        lis_companies = new ArrayList<>();
        rutas = new ArrayList<>();

        String url = Routes.COMPANIES + "/" + typeCompany_selected.getId();
        System.out.println(url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            object2 = object;
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("companies"));

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject type_object = array.getJSONObject(i);

                                    Company company = new Company();
                                    company.setId(type_object.getInt("id"));
                                    company.setCompany_name(type_object.getString("company_name"));
                                    company.setCompany_address(type_object.getString("company_address"));
                                    company.setCompany_phone(type_object.getString("company_phone"));
                                    company.setCompany_description(type_object.getString("company_description"));
                                    company.setLatitude(type_object.getString("latitude"));
                                    company.setLongitude(type_object.getString("longitude"));
                                    company.setUrl_merchant(type_object.getString("url_merchant"));

                                    lis_companies.add(company);

                                    double latitude = Double.parseDouble(company.getLatitude());
                                    double longitude = Double.parseDouble(company.getLongitude());
                                    LatLng coordenadas = new LatLng(latitude, longitude);
                                    MarkerOptions markerOptions = new MarkerOptions().position(coordenadas).title(company.getCompany_name());
                                    rutas.add(markerOptions);

                                }
                                for (MarkerOptions ruta : rutas) {
                                    mMap.addMarker(ruta);

                                }
                                LatLng positionUser = new LatLng(llocaliza.getLatitude(), llocaliza.getLongitude());
                                mMap.addMarker(new MarkerOptions().icon(bitmapCustomer(getApplicationContext(), R.drawable.ic_custom_pin_circle_40)).anchor(0.0f, 1.0f).position(positionUser).title("Mi posición"));
                                //mMap.addMarker(new MarkerOptions().position(positionUser).title("Mi posición"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(llocaliza.getLatitude(), llocaliza.getLongitude()), 12));
                                // mMap.moveCamera(CameraUpdateFactory.newLatLng(positionUser));


                            }
                        } catch (Exception e) {

                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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
        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);


    }

    public void getPositionUser() {

        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            Toast.makeText(MapsActivity.this, "no hay permiso", Toast.LENGTH_LONG).show();
        else {
            localizar = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
            llocaliza = localizar.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (llocaliza != null) {
                //  txt_lon.setText(String.valueOf(llocaliza.getLongitude()));
                // txt_lati.setText(String.valueOf(llocaliza.getLatitude()));
                //Toast.makeText(MapsActivity.this, " ubicacion  " + llocaliza.getLatitude() + " , " + llocaliza.getLongitude(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MapsActivity.this, "no hay ubicacion", Toast.LENGTH_LONG).show();
            }
        }

    }

    public BitmapDescriptor bitmapCustomer(Context context, int vectorResId) {
        Drawable veDrawable = ContextCompat.getDrawable(context, vectorResId);
        veDrawable.setBounds(0, 0, veDrawable.getIntrinsicWidth(), veDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(veDrawable.getIntrinsicWidth(), veDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        veDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }

    public void getCompanies() {
        lis_companies = new ArrayList<>();
        rutas = new ArrayList<>();
        String url = Routes.COMPANIES + "/" + typeCompany_selected.getId();
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            object2 = object;
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("companies"));

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject type_object = array.getJSONObject(i);

                                    Company company = new Company();
                                    company.setId(type_object.getInt("id"));
                                    company.setCompany_name(type_object.getString("company_name"));
                                    company.setCompany_address(type_object.getString("company_address"));
                                    company.setCompany_phone(type_object.getString("company_phone"));
                                    company.setCompany_description(type_object.getString("company_description"));
                                    company.setLatitude(type_object.getString("latitude"));
                                    company.setLongitude(type_object.getString("longitude"));
                                    company.setUrl_merchant(type_object.getString("url_merchant"));

                                    lis_companies.add(company);

                                    double latitude = Double.parseDouble(company.getLatitude());
                                    double longitude = Double.parseDouble(company.getLongitude());
                                    LatLng coordenadas = new LatLng(latitude, longitude);
                                    MarkerOptions markerOptions = new MarkerOptions().position(coordenadas).title(company.getCompany_name());
                                    rutas.add(markerOptions);

                                }


                            }
                        } catch (Exception e) {

                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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
        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        System.out.println("ARRAY = >>> \n" + marker.getTitle());
        String name_company = "";
        int id = 0;
        String description_company = "";
        String phone_company = "";
        String address_company = "";
        String url_image="";
        Company company_selected  = new Company();
        for (Company company : lis_companies) {
            if (company.getCompany_name().equals(marker.getTitle())) {
                name_company = company.getCompany_name();
                id = company.getId();
                description_company = company.getCompany_description();
                phone_company = company.getCompany_phone();
                address_company = company.getCompany_address();
                url_image = company.getUrl_merchant();
                company_selected = company;
            }
        }
        if (id != 0) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MapsActivity.this, R.style.BottomSheetDialogTheme);
            View buttomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_detail_map,
                    (LinearLayout) findViewById(R.id.liner_detail_map));
            txt_name = buttomSheetView.findViewById(R.id.bottom_sheet_name);
            txt_description = buttomSheetView.findViewById(R.id.bottom_sheet_description);
            txt_address = buttomSheetView.findViewById(R.id.bottom_sheet_address);
            txt_phone = buttomSheetView.findViewById(R.id.bottom_sheet_phone);
            image = buttomSheetView.findViewById(R.id.bottom_sheet_image);

            ///CARGAR DATOS
            txt_name.setText(name_company);
            txt_description.setText(description_company);
            txt_phone.setText(phone_company);
            txt_address.setText(address_company);
            Picasso.get().load(Routes.URL+url_image).into(image);


            final Company finalCompany_selected = company_selected;
            buttomSheetView.findViewById(R.id.bottom_sheet_btn_shop).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();
                    Intent intent =  new Intent(MapsActivity.this, ListCategoriesActivity.class);
                    intent.putExtra("company_selected", finalCompany_selected);
                    startActivity(intent);
                }
            });
            bottomSheetDialog.setContentView(buttomSheetView);
            bottomSheetDialog.show();
        }

        return false;
    }
}