package co.desofsi.shopapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import co.desofsi.shopapp.R;
import co.desofsi.shopapp.deliveryactivities.RequestDeliveryCustomerFragment;
import co.desofsi.shopapp.fragments.AccountFragment;
import co.desofsi.shopapp.fragments.HomeFragment;
import co.desofsi.shopapp.fragments.RequestDeliveryFragment;
import co.desofsi.shopapp.fragments.OrderFragment;
import co.desofsi.shopapp.fragments.MerchantHomeFragment;
import co.desofsi.shopapp.merchantsactivities.OrderFragmentMerchant;
import co.desofsi.shopapp.models.DetailOrder;
import co.desofsi.shopapp.models.Order;
import co.desofsi.shopapp.models.Product;
import co.desofsi.shopapp.routes.Routes;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static co.desofsi.shopapp.routes.Routes.TIMER_MAP;

public class HomeActivity extends AppCompatActivity {

    private int REQUEST_CODE = 1;
    private FragmentManager fragmentManager;
    private final static int ID_HOME = 1;
    private final static int ID_EXPLORE = 2;
    private final static int ID_MESSAGE = 3;
    private final static int ID_NOTIFICATION = 4;
    private final static int ID_ACCOUNT = 5;
    private final static int ID_MERCHANT = 6;
    private final static int ID_CUSTOMER_DELIVERY = 7;
    LocationManager locationManager;
    Location location;
    SharedPreferences sharedPreferences;
    public static String role = "";


    private FusedLocationProviderClient fusedLocationClient;
    private CountDownTimer yourCountDownTimer;
    private double latitude_now = 0;
    private double longitude_now = 0;
    public static ArrayList<Product> list_products  =  new ArrayList<>();
    public static ArrayList<DetailOrder> list_detail =  new ArrayList<>();
    public static Order order =  new Order();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPreferences = HomeActivity.this.getSharedPreferences("user", Context.MODE_PRIVATE);
       // String role = sharedPreferences.getString("role", "");
        fragmentManager = getSupportFragmentManager();
        //getPositionUser();

        final TextView tvSelected = findViewById(R.id.tv_selected);
        tvSelected.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf"));
        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);


        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_home_black_24dp));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_EXPLORE, R.drawable.ic_orders));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_MERCHANT, R.drawable.ic_baseline_shopping_cart_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT, R.drawable.ic_account_circle_black_24dp));



        //  bottomNavigation.setCount(ID_NOTIFICATION, "115");


        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                // Toast.makeText(HomeActivity.this, "clicked item : " + item.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                //   Toast.makeText(HomeActivity.this, "showing item : " + item.getId(), Toast.LENGTH_SHORT).show();

                String name;
                switch (item.getId()) {
                    case ID_HOME:
                        name = "HOME";

                        fragmentManager.beginTransaction().replace(R.id.home_frame_container, new HomeFragment(), HomeFragment.class.getSimpleName()).commit();
                        break;
                    case ID_EXPLORE:
                        name = "EXPLORE";
                        fragmentManager.beginTransaction().replace(R.id.home_frame_container, new OrderFragment(), OrderFragment.class.getSimpleName()).commit();

                        break;
                    case ID_MESSAGE:
                        name = "MESSAGE";
                        fragmentManager.beginTransaction().replace(R.id.home_frame_container, new MerchantHomeFragment(), MerchantHomeFragment.class.getSimpleName()).commit();

                        break;
                    case ID_NOTIFICATION:
                        name = "NOTIFICATION";
                        fragmentManager.beginTransaction().replace(R.id.home_frame_container, new RequestDeliveryFragment(), RequestDeliveryFragment.class.getSimpleName()).commit();

                        break;
                    case ID_ACCOUNT:
                        name = "ACCOUNT";
                        fragmentManager.beginTransaction().replace(R.id.home_frame_container, new AccountFragment(), AccountFragment.class.getSimpleName()).commit();

                        break;


                    case ID_MERCHANT:
                        name = "MERCHANT";
                        fragmentManager.beginTransaction().replace(R.id.home_frame_container, new OrderFragmentMerchant(), OrderFragmentMerchant.class.getSimpleName()).commit();

                        break;


                    case ID_CUSTOMER_DELIVERY:
                        name = "DELIVERY";
                        fragmentManager.beginTransaction().replace(R.id.home_frame_container, new RequestDeliveryCustomerFragment(), RequestDeliveryCustomerFragment.class.getSimpleName()).commit();

                        break;


                    default:
                        name = "";
                }
                tvSelected.setText(getString(R.string.main_page_selected, name));
            }
        });

        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                //Toast.makeText(HomeActivity.this, "reselected item : " + item.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        //  bottomNavigation.setCount(ID_NOTIFICATION, "115");




        bottomNavigation.show(ID_HOME, true);




    }


    private void CountDownTimer() {

        if (yourCountDownTimer != null) {
            yourCountDownTimer.cancel();
        }
        yourCountDownTimer = new CountDownTimer(TIMER_MAP, TIMER_MAP) {

            @Override
            public void onTick(long l) {
                //  Log.e("Seconds : ", "" + l / 1000);

            }

            @Override
            public void onFinish() {
                // Toast.makeText(HomeActivity.this, "Puntos actualizados", Toast.LENGTH_SHORT).show();
                insertLocation();
            }
        };
        yourCountDownTimer.start();

    }

    public void insertLocation() {
        getFuseLocationUser();
        final String latitude_delivery = String.valueOf(latitude_now);
        final String longitude_delivery = String.valueOf(longitude_now);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Routes.UPDATED_POSITION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {

                                //   Toast.makeText(HomeActivity.this, "Actualizado", Toast.LENGTH_SHORT).show();
                                CountDownTimer();
                            }
                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(HomeActivity.this, "Error al guardar", Toast.LENGTH_LONG).show();
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("latitude", latitude_delivery);
                map.put("longitude", longitude_delivery);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
        requestQueue.add(stringRequest);
    }

    public void getFuseLocationUser() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(HomeActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this,
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
                            latitude_now = location.getLatitude();
                            longitude_now = location.getLongitude();
                            //  Toast.makeText(HomeActivity.this, "Lat: " + location.getLatitude() + " Long: " + location.getLongitude(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }


    public void getPositionUser() {

        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }


            return;
        } else {
            locationManager = (LocationManager) HomeActivity.this.getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                Toast.makeText(HomeActivity.this, " ubicacion  " + location.getLatitude() + " , " + location.getLongitude(), Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(HomeActivity.this, "Tu ubicación no ha sido encontrada", Toast.LENGTH_LONG).show();
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getPositionUser();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Toast.makeText(HomeActivity.this, "Habilita tu ubicación", Toast.LENGTH_LONG).show();
                }
                return;
            }


        }
    }


}
