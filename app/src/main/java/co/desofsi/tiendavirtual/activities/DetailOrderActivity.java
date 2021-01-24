package co.desofsi.tiendavirtual.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import co.desofsi.tiendavirtual.R;
import co.desofsi.tiendavirtual.adapters.DetailListProductstAdapter;
import co.desofsi.tiendavirtual.routes.Routes;
import co.desofsi.tiendavirtual.models.DetailOrder;

public class DetailOrderActivity extends AppCompatActivity {

    public static TextView txt_total;
    private Button btn_confirm;
    public static RecyclerView recycler_items;
    private ImageButton btn_back;
    public static LinearLayout liner_btn;
    public static RelativeLayout relative_empty;
    private ProgressDialog dialog;
    private SharedPreferences userPref;
    private TextView txt_per;

    LocationManager locationManager;
    Location location;

    private static final int PERMISSION_LOCATION = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);
        try{
            init();
            loadDetail();
            getPositionUser();
            eventsButtons();
        }catch (Exception e)
        {
            Toast.makeText(DetailOrderActivity.this,"Error : "+e,Toast.LENGTH_SHORT).show();
        }

    }

    public void init() {
        userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        txt_total = findViewById(R.id.detail_order_txt_total);
        btn_confirm = findViewById(R.id.detail_order_btn_confirm);
        recycler_items = findViewById(R.id.detail_order_recycler_items);
        btn_back = findViewById(R.id.detail_order_btn_back);
        liner_btn = findViewById(R.id.detail_bottom);
        relative_empty = findViewById(R.id.empty_concept);
        txt_per = findViewById(R.id.detail_order_txt_permi);
        dialog = new ProgressDialog(DetailOrderActivity.this);
        dialog.setCancelable(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(DetailOrderActivity.this, LinearLayoutManager.VERTICAL, false);
        recycler_items.setLayoutManager(mLayoutManager);
        txt_total.setText("$ " + loadTotalPay());


        if (ListCategoriesActivity.list_detail.size() == 0) {
            liner_btn.setVisibility(View.GONE);
            relative_empty.setVisibility(View.VISIBLE);
        }


    }


    private void postOrder() {
        dialog.setMessage("Enviando");
        dialog.show();
        // final String id_user = String.valueOf(ListCategoriesActivity.order.getId_user());
        final String id_company = String.valueOf(ListCategoriesActivity.order.getId_company());
        final String total_order = ListCategoriesActivity.order.getTotal();
        final String longitude_order = ListCategoriesActivity.order.getLongitude_order();
        final String latitude_order = ListCategoriesActivity.order.getLatitude_order();

        final JSONArray array = new JSONArray();
        int i = 0;
        for (DetailOrder detailOrder : ListCategoriesActivity.list_detail) {
            JSONObject object = new JSONObject();
            try {
                object.put("id_product", detailOrder.getId_product());
                object.put("name", detailOrder.getProduct_name());
                object.put("description", detailOrder.getProduct_desc());
                object.put("quanty", detailOrder.getCant());
                object.put("total_price", detailOrder.getPrice_total());
                object.put("price_unit", detailOrder.getPrice_unit());

            } catch (Exception e) {

            }
            array.put(object);
            i++;
        }
        //System.out.println("ARRAY ENVIADO ===>> \n" + array.toString());
        //System.out.println("ENCABEZAADO ENVIADO ===>> \n" +" = "+id_company+" = "+total_order);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Routes.SEND_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {

                                startActivity(new Intent(DetailOrderActivity.this, ReviewOrderActivity.class));
                                finish();
                                Toast.makeText(DetailOrderActivity.this, "Enviado", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DetailOrderActivity.this, "Error al enviar el pedido", Toast.LENGTH_SHORT).show();

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
                map.put("id_company", id_company);
                map.put("total_order", total_order);
                map.put("longitude",longitude_order);
                map.put("latitude",latitude_order);
                map.put("detail_order", array.toString());
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(DetailOrderActivity.this);
        requestQueue.add(stringRequest);


    }

    public static double loadTotalPay() {
        double total = 0;
        DecimalFormat format = new DecimalFormat("#.00");// el numero de ceros despues del entero

        try {
            if (ListCategoriesActivity.list_detail.size() > 0) {
                for (DetailOrder detailOrder : ListCategoriesActivity.list_detail) {
                    total += Double.parseDouble(detailOrder.getPrice_total());
                }
            }
            ListCategoriesActivity.order.setTotal("" +  format.format(total));
            return total;
        } catch (Exception e) {
            return total;
        }


    }

    public void loadDetail() {
        DetailListProductstAdapter listProductstAdapter = new DetailListProductstAdapter(DetailOrderActivity.this, ListCategoriesActivity.list_detail);
        recycler_items.setAdapter(listProductstAdapter);
    }

    public void eventsButtons() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ListCategoriesActivity.order.getLatitude_order() !=null && ListCategoriesActivity.order.getLongitude_order() !=null)

                {
                    postOrder();
                }else{
                    Toast.makeText(DetailOrderActivity.this, "Tu ubicación no ha sido encontrada, revisa tu dispositivo", Toast.LENGTH_LONG).show();
                }
                // startActivity(new Intent(DetailOrderActivity.this, ReviewOrderActivity.class));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPositionUser() {

       // ActivityCompat.requestPermissions(DetailOrderActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (ActivityCompat.checkSelfPermission(DetailOrderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissions, PERMISSION_LOCATION);
        }

        else {
            locationManager = (LocationManager) DetailOrderActivity.this.getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                //  txt_lon.setText(String.valueOf(llocaliza.getLongitude()));
                // txt_lati.setText(String.valueOf(llocaliza.getLatitude()));
                String longitude_order = String.valueOf(location.getLongitude());
                String latitude_order = String.valueOf(location.getLatitude());
                ListCategoriesActivity.order.setLatitude_order(latitude_order);
                ListCategoriesActivity.order.setLongitude_order(longitude_order);
                btn_confirm.setVisibility(View.VISIBLE);
                txt_per.setVisibility(View.GONE);
               // Toast.makeText(DetailOrderActivity.this, " ubicacion  " + location.getLatitude() + " , " + location.getLongitude(), Toast.LENGTH_LONG).show();
            } else {
                btn_confirm.setVisibility(View.GONE);
                txt_per.setVisibility(View.VISIBLE);
                Toast.makeText(DetailOrderActivity.this, "Tu ubicación no ha sido encontrada", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(DetailOrderActivity.this, "Habilite el permiso de ubicación", Toast.LENGTH_LONG).show();

                }
            }
        }
    }
}