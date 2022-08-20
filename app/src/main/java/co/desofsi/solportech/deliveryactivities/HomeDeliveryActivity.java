package co.desofsi.solportech.deliveryactivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import co.desofsi.solportech.R;
import co.desofsi.solportech.fragments.AccountFragment;
import co.desofsi.solportech.fragments.HomeFragment;
import co.desofsi.solportech.fragments.MerchantHomeFragment;
import co.desofsi.solportech.fragments.OrderFragment;
import co.desofsi.solportech.fragments.RequestDeliveryFragment;

public class HomeDeliveryActivity extends AppCompatActivity implements LocationListener{
    private FragmentManager fragmentManager;
    private final static int ID_HOME = 1;
    private final static int ID_EXPLORE = 2;
    private final static int ID_MESSAGE = 3;
    private final static int ID_NOTIFICATION = 4;
    private final static int ID_ACCOUNT = 5;
    LocationManager locationManager;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_delivery);
        fragmentManager = getSupportFragmentManager();
        getPositionUser();

        final TextView tvSelected = findViewById(R.id.tv_selected);
        tvSelected.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf"));

        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);

        //bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_home_black_24dp));
        //bottomNavigation.add(new MeowBottomNavigation.Model(ID_EXPLORE, R.drawable.ic_baseline_shopping_cart_24));
        //bottomNavigation.add(new MeowBottomNavigation.Model(ID_MESSAGE, R.drawable.ic_baseline_directions_bike_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTIFICATION, R.drawable.ic_notifications_black_24dp));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT, R.drawable.ic_account_circle_black_24dp));

        bottomNavigation.setCount(ID_NOTIFICATION, "115");




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

        bottomNavigation.setCount(ID_NOTIFICATION, "115");

        bottomNavigation.show(ID_HOME, true);


    }


    public void getPositionUser() {

        if (ActivityCompat.checkSelfPermission(HomeDeliveryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeDeliveryActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeDeliveryActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(HomeDeliveryActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeDeliveryActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(HomeDeliveryActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }


            return;
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);

        locationManager.requestLocationUpdates(provider, 20000L, 1f, (LocationListener) this);


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
                }
                return;
            }


        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(HomeDeliveryActivity.this, "lt" + location.getLatitude() + "  LONG" + location.getLongitude(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}