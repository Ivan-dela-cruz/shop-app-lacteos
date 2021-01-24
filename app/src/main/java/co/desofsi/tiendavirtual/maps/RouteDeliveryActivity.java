package co.desofsi.tiendavirtual.maps;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.desofsi.tiendavirtual.R;
import co.desofsi.tiendavirtual.activities.DetailOrderActivity;
import co.desofsi.tiendavirtual.activities.HomeActivity;
import co.desofsi.tiendavirtual.activities.ListCategoriesActivity;
import co.desofsi.tiendavirtual.merchantsactivities.CompanyOrdersActivity;
import co.desofsi.tiendavirtual.merchantsactivities.MerchantDeatilOrderActivity;
import co.desofsi.tiendavirtual.models.Delivery;
import co.desofsi.tiendavirtual.models.OrderRequestDelivery;
import co.desofsi.tiendavirtual.routes.Routes;

public class RouteDeliveryActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private OrderRequestDelivery orderRequestDelivery;
    LocationManager locationManager;
    Location location;
    private ImageButton back;
    private double my_latitude, my_longitude;
    private Button btn_confirm, btn_send_not,btn_complete;

    private static final int PERMISSION_LOCATION = 1;
    private SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_delivery);
        orderRequestDelivery = (OrderRequestDelivery) getIntent().getExtras().getSerializable("order");
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        back = findViewById(R.id.map_route_btn_back);
        btn_confirm = findViewById(R.id.route_confirm);
        btn_send_not = findViewById(R.id.route_send);
        btn_complete = findViewById(R.id.route_complete);
        getPositionUser();
        // Toast.makeText(RouteDeliveryActivity.this, "" + orderRequestDelivery.getLatitude_company(), Toast.LENGTH_SHORT).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(orderRequestDelivery.getStatus_request().equals("aceptado")){
            btn_complete.setVisibility(View.VISIBLE);
            btn_send_not.setVisibility(View.GONE);
            btn_confirm.setVisibility(View.GONE);
        }
        if(orderRequestDelivery.getStatus_request().equals("entregado")){
            btn_complete.setVisibility(View.GONE);
            btn_send_not.setVisibility(View.GONE);
            btn_confirm.setVisibility(View.GONE);
        }

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RouteDeliveryActivity.this);
                builder.setMessage("¿Está seguro de aceptar la entrega de esta orden")
                        .setCancelable(false)
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                confirmDelivery();
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
        btn_send_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RouteDeliveryActivity.this);
                builder.setMessage("¿Está seguro de rechazar la orden")
                        .setCancelable(false)
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                declineDelivery();
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
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RouteDeliveryActivity.this);
                builder.setMessage("¿Está seguro en finalizar la entrega")
                        .setCancelable(false)
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                completeDelivery();
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

    }

    public void completeDelivery() {
        String url = Routes.COMPLETE_DELIVERY + "/" + orderRequestDelivery.getId();
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                Intent intent = new Intent(RouteDeliveryActivity.this, HomeActivity.class);
                                startActivity(intent);
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
        RequestQueue requestQueue = Volley.newRequestQueue(RouteDeliveryActivity.this);
        requestQueue.add(stringRequest);
    }

    public void confirmDelivery() {
        String url = Routes.CONFIRM_DELIVERY + "/" + orderRequestDelivery.getId();
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                Intent intent = new Intent(RouteDeliveryActivity.this, HomeActivity.class);
                                startActivity(intent);
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
        RequestQueue requestQueue = Volley.newRequestQueue(RouteDeliveryActivity.this);
        requestQueue.add(stringRequest);
    }
    public void declineDelivery() {
        String url = Routes.DECLINE_DELIVERY + "/" + orderRequestDelivery.getId();
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                Intent intent = new Intent(RouteDeliveryActivity.this, HomeActivity.class);
                                startActivity(intent);
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
        RequestQueue requestQueue = Volley.newRequestQueue(RouteDeliveryActivity.this);
        requestQueue.add(stringRequest);
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


        try {
            double origin_latitude = Double.parseDouble(orderRequestDelivery.getLatitude_company());
            double origin_longitude = Double.parseDouble(orderRequestDelivery.getLongitude_company());
            double destination_latitude = Double.parseDouble(orderRequestDelivery.getLatitude_order());
            double destination_longitude = Double.parseDouble(orderRequestDelivery.getLongitude_order());


            // Add a marker in Sydney and move the camera
            LatLng user = new LatLng(my_latitude, my_longitude);
            mMap.addMarker(new MarkerOptions()
                    .icon(bitmapCustomer(getApplicationContext(), R.drawable.locationred))
                    .anchor(0.0f, 1.0f)
                    .position(user)
                    .title("Mi posición"));

            // Add a marker in Sydney and move the camera
            LatLng company = new LatLng(origin_latitude, origin_longitude);
            mMap.addMarker(new MarkerOptions()
                    .icon(bitmapCustomer(getApplicationContext(), R.drawable.store))
                    .anchor(0.0f, 1.0f)
                    .position(company)
                    .title(orderRequestDelivery.getName_company()));
            LatLng positionUser = new LatLng(destination_latitude, destination_longitude);
            mMap.addMarker(new MarkerOptions()
                    .icon(bitmapCustomer(getApplicationContext(), R.drawable.locationuser))
                    .anchor(0.0f, 1.0f)
                    .position(positionUser)
                    .title(orderRequestDelivery.getName_customer()));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(company));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(origin_latitude, origin_longitude), 17));

            getPoint(origin_latitude, origin_longitude, destination_latitude, destination_longitude);

        } catch (Exception e) {

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

    public void getPoint(double origin_latitude, double origin_longitude, double destination_latitude, double destination_longitude) {
        String url = "" + Routes.URL_MAP + origin_latitude + ',' + origin_longitude + Routes.DESTINATION + destination_latitude + ',' + destination_longitude + Routes.API_KEY;


        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            System.out.println("RESPUESTA \n" + response);
                            graphicRoute(object);

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


        };
        RequestQueue requestQueue = Volley.newRequestQueue(RouteDeliveryActivity.this);
        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public void graphicRoute(JSONObject response) {
        JSONArray jRoute;
        JSONArray jLegs;
        JSONArray jSteps;

        try {
            jRoute = response.getJSONArray("routes");
            for (int i = 0; i < jRoute.length(); i++) {
                jLegs = ((JSONObject) (jRoute.get(i))).getJSONArray("legs");
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "" + ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        Log.i("end", "" + polyline);
                        List<LatLng> list = PolyUtil.decode(polyline);
                        mMap.addPolyline(new PolylineOptions().addAll(list).color(ContextCompat.getColor(RouteDeliveryActivity.this, R.color.colorOrange)).width(6));
                    }
                }

            }
        } catch (Exception e) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPositionUser() {

        // ActivityCompat.requestPermissions(DetailOrderActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (ActivityCompat.checkSelfPermission(RouteDeliveryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissions, PERMISSION_LOCATION);
        } else {
            locationManager = (LocationManager) RouteDeliveryActivity.this.getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                //  txt_lon.setText(String.valueOf(llocaliza.getLongitude()));
                // txt_lati.setText(String.valueOf(llocaliza.getLatitude()));
                my_latitude = location.getLongitude();
                my_longitude = location.getLatitude();

                // Toast.makeText(DetailOrderActivity.this, " ubicacion  " + location.getLatitude() + " , " + location.getLongitude(), Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(RouteDeliveryActivity.this, "Tu ubicación no ha sido encontrada", Toast.LENGTH_LONG).show();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getPositionUser();
                } else {
                    Toast.makeText(RouteDeliveryActivity.this, "Habilite el permiso de ubicación", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

}