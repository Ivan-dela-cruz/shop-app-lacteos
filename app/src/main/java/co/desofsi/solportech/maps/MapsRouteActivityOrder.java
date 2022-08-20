package co.desofsi.solportech.maps;

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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.desofsi.solportech.R;
import co.desofsi.solportech.activities.ListCategoriesActivity;
import co.desofsi.solportech.routes.Routes;
import co.desofsi.solportech.models.Delivery;
import co.desofsi.solportech.models.Order;

public class MapsRouteActivityOrder extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ArrayList<Delivery> lis_deliveriman;
    private ArrayList<Delivery> real_time_lis_deliveriman;
    private Order order;
    private SharedPreferences sharedPreferences;
    private ImageButton btn_back;
    private ArrayList<MarkerOptions> rutas;
    private ArrayList<MarkerOptions> real_time_rutas;
    LocationManager localizar;
    Location llocaliza;
    JSONObject object2;
    SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationClient;
    private int REQUEST_CODE = 1;

    ///DETALLE MAPA
    ImageView imgmarker;
    private BottomSheetBehavior mBottomSheetBehavior1;
    private RoundedImageView image;
    private TextView txt_name, txt_description, txt_address, txt_phone;


    private double latitude_now = 0;
    private double longitude_now = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_order);
        init();
        //getPositionUser();
        getFuseLocationUser();

        //getCompanies();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    public void init() {
        object2 = new JSONObject();
        order = (Order) getIntent().getExtras().getSerializable("order");
        sharedPreferences = MapsRouteActivityOrder.this.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
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
        lis_deliveriman = new ArrayList<>();
        rutas = new ArrayList<>();
        real_time_rutas = new ArrayList<>();

        String url = Routes.DELIVERYMAN;
        System.out.println(url);
        mMap.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            object2 = object;
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("deliveryman"));

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject type_object = array.getJSONObject(i);

                                    Delivery delivery = new Delivery();
                                    delivery.setId(type_object.getInt("id"));
                                    delivery.setName(type_object.getString("name"));
                                    delivery.setLast_name(type_object.getString("last_name"));
                                    delivery.setPhone(type_object.getString("phone"));
                                    delivery.setVehicle_plate(type_object.getString("vehicle_plate"));
                                    delivery.setVehicle_description(type_object.getString("vehicle_description"));
                                    delivery.setUrl_vehicle(type_object.getString("url_vehicle"));
                                    delivery.setStatus(type_object.getString("status"));
                                    delivery.setStatus_order(type_object.getString("status_order"));
                                    delivery.setLongitude(type_object.getString("longitude"));
                                    delivery.setLatitud(type_object.getString("latitude"));
                                    delivery.setType_vehicle(type_object.getString("type_vehicle"));

                                    lis_deliveriman.add(delivery);

                                    double latitude = Double.parseDouble(delivery.getLatitud());
                                    double longitude = Double.parseDouble(delivery.getLongitude());
                                    LatLng coordenadas = new LatLng(latitude, longitude);
                                    MarkerOptions markerOptions = new MarkerOptions().icon(bitmapCustomer(getApplicationContext(), R.drawable.track))
                                            .position(coordenadas).title(delivery.getName() + " " + delivery.getLast_name());
                                    rutas.add(markerOptions);

                                }
                                for (MarkerOptions ruta : rutas) {
                                    mMap.addMarker(ruta);

                                }
                                double latitude = latitude_now;
                                double longitude = longitude_now;
                                try {
                                    latitude = Double.parseDouble(order.getLatitude_company());
                                    longitude = Double.parseDouble(order.getLongitude_company());
                                    Toast.makeText(MapsRouteActivityOrder.this, "Entro", Toast.LENGTH_LONG).show();

                                } catch (Exception e) {
                                    Toast.makeText(MapsRouteActivityOrder.this, "no entro", Toast.LENGTH_LONG).show();

                                }
                                LatLng positionUser = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions()
                                        .icon(bitmapCustomer(getApplicationContext(), R.drawable.store))
                                        .anchor(0.0f, 1.0f)
                                        .position(positionUser)
                                        .title(order.getName_company()));
                                //mMap.addMarker(new MarkerOptions().position(positionUser).title("Mi posición"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17));
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
        RequestQueue requestQueue = Volley.newRequestQueue(MapsRouteActivityOrder.this);
        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);


    }

    public void getFuseLocationUser() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsRouteActivityOrder.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsRouteActivityOrder.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            //Toast.makeText(MapsRouteActivityOrder.this, "Lat: " + location.getLatitude() + " Long: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                            latitude_now = location.getLatitude();
                            longitude_now = location.getLongitude();
                        }
                    }
                });

    }

    public void getPositionUser() {

        ActivityCompat.requestPermissions(MapsRouteActivityOrder.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (ActivityCompat.checkSelfPermission(MapsRouteActivityOrder.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            Toast.makeText(MapsRouteActivityOrder.this, "no hay permiso", Toast.LENGTH_LONG).show();
        else {
            localizar = (LocationManager) MapsRouteActivityOrder.this.getSystemService(Context.LOCATION_SERVICE);
            llocaliza = localizar.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (llocaliza != null) {
                //  txt_lon.setText(String.valueOf(llocaliza.getLongitude()));
                // txt_lati.setText(String.valueOf(llocaliza.getLatitude()));
                //Toast.makeText(MapsActivity.this, " ubicacion  " + llocaliza.getLatitude() + " , " + llocaliza.getLongitude(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MapsRouteActivityOrder.this, "no hay ubicacion", Toast.LENGTH_LONG).show();
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
        lis_deliveriman = new ArrayList<>();
        rutas = new ArrayList<>();
        String url = Routes.COMPANIES + "/" + order.getId();
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

                                    Delivery delivery = new Delivery();
                                    delivery.setId(type_object.getInt("id"));
                                    delivery.setName(type_object.getString("name"));
                                    delivery.setLast_name(type_object.getString("last_name"));
                                    delivery.setPhone(type_object.getString("phone"));
                                    delivery.setVehicle_plate(type_object.getString("vehicle_plate"));
                                    delivery.setVehicle_description(type_object.getString("vehicle_description"));
                                    delivery.setUrl_vehicle(type_object.getString("url_vehicle"));
                                    delivery.setStatus(type_object.getString("status"));
                                    delivery.setStatus_order(type_object.getString("status_order"));
                                    delivery.setLongitude(type_object.getString("longitude"));
                                    delivery.setLatitud(type_object.getString("latitud"));
                                    delivery.setType_vehicle(type_object.getString("type_vehicle"));

                                    lis_deliveriman.add(delivery);

                                    lis_deliveriman.add(delivery);

                                    double latitude = Double.parseDouble(delivery.getLatitud());
                                    double longitude = Double.parseDouble(delivery.getLongitude());
                                    LatLng coordenadas = new LatLng(latitude, longitude);
                                    MarkerOptions markerOptions = new MarkerOptions().position(coordenadas).title(delivery.getName() + " " + delivery.getLast_name());
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
        RequestQueue requestQueue = Volley.newRequestQueue(MapsRouteActivityOrder.this);
        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        System.out.println("ARRAY = >>> \n" + marker.getTitle());
        String name_delivery = "";
        int id = 0;
        String description_delivery = "";
        String phone_delivery = "";
        String type_vehicle = "";
        String url_image = "";
        Delivery deliveryselect = new Delivery();
        for (Delivery delivery_for : lis_deliveriman) {
            String names = delivery_for.getName() + " " + delivery_for.getLast_name();
            if (names.equals(marker.getTitle())) {
                id = delivery_for.getId();
                name_delivery = names;
                description_delivery = delivery_for.getVehicle_description();
                phone_delivery = delivery_for.getPhone();
                type_vehicle = delivery_for.getType_vehicle();
                url_image = delivery_for.getUrl_vehicle();
                deliveryselect = delivery_for;
            }
        }
        if (id != 0) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MapsRouteActivityOrder.this, R.style.BottomSheetDialogTheme);
            View buttomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_detail_delivery_map,
                    (LinearLayout) findViewById(R.id.liner_detail_map));
            txt_name = buttomSheetView.findViewById(R.id.bottom_sheet_name);
            txt_description = buttomSheetView.findViewById(R.id.bottom_sheet_description);
            txt_address = buttomSheetView.findViewById(R.id.bottom_sheet_address);
            txt_phone = buttomSheetView.findViewById(R.id.bottom_sheet_phone);
            image = buttomSheetView.findViewById(R.id.bottom_sheet_image);

            ///CARGAR DATOS
            txt_name.setText(name_delivery);
            txt_description.setText(description_delivery);
            txt_phone.setText(phone_delivery);
            txt_address.setText(type_vehicle);
            Picasso.get().load(Routes.URL + url_image).into(image);


            final Delivery finaldelivery_selected = deliveryselect;
            buttomSheetView.findViewById(R.id.bottom_sheet_btn_shop).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();
                    Intent intent = new Intent(MapsRouteActivityOrder.this, ListCategoriesActivity.class);
                    intent.putExtra("company_selected", finaldelivery_selected);
                    startActivity(intent);
                }
            });
            bottomSheetDialog.setContentView(buttomSheetView);
            bottomSheetDialog.show();
        }

        return false;
    }
}