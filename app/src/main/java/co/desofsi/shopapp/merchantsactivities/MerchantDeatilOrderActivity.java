package co.desofsi.shopapp.merchantsactivities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.desofsi.shopapp.R;
import co.desofsi.shopapp.adapters.ReviewListProductstAdapter;
import co.desofsi.shopapp.models.Company;
import co.desofsi.shopapp.routes.Routes;
import co.desofsi.shopapp.maps.MapsActivityOrder;
import co.desofsi.shopapp.models.DateClass;
import co.desofsi.shopapp.models.DetailOrder;
import co.desofsi.shopapp.models.Order;

public class MerchantDeatilOrderActivity extends AppCompatActivity {
    private ArrayList<DetailOrder> lis_products;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;

    private ImageButton btn_home, btn_download;
    private TextView txt_order_number,  txt_order_data,  txt_order_total ;
    private Button btn_delivery, btn_deactivate;
    private ScrollView scrollView;
    private DateClass dateClass;
    private Order order;
    private Company company;

    private static final int PERMISSION_STORAGE_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_deatil_order);
        try {
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //  window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.md_blue_800));
                //getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            order = new Order();
            order = (Order) getIntent().getExtras().getSerializable("order");
            company = order.getCompany();
            init();
            eventsButtons();
            loadReviewOrder();
            getOrdersDetail();
            if (order.getStatus().equals("anulado")) {
                btn_delivery.setVisibility(View.GONE);
                btn_deactivate.setVisibility(View.GONE);
            }
            if (order.getStatus().equals("pendiente")) {
                btn_deactivate.setVisibility(View.VISIBLE);
            }else{
                btn_deactivate.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Toast.makeText(MerchantDeatilOrderActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();


        }
    }

    public void init() {
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        dateClass = new DateClass();
        txt_order_number = findViewById(R.id.merchant_detail_order_txt_order_number);

        txt_order_data = findViewById(R.id.merchant_detail_order_txt_date);

        txt_order_total = findViewById(R.id.merchant_detail_order_txt_total);
        btn_home = findViewById(R.id.merchant_detail_order_btn_back);
        btn_download = findViewById(R.id.merchant_detail_order_btn_download);
        btn_delivery = findViewById(R.id.merchant_detail_order_btn_map);
        btn_deactivate = findViewById(R.id.merchant_detail_order_btn_deactivate);
        scrollView = findViewById(R.id.merchant_detail_order_scroll);
        recyclerView = findViewById(R.id.merchant_detail_order_recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MerchantDeatilOrderActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void loadReviewOrder() {

        txt_order_total.setText("$ " + order.getTotal());

        txt_order_data.setText(dateClass.dateFormatHuman(order.getDate()));
        txt_order_number.setText(order.getOrder_number());

        scrollView.pageScroll(View.FOCUS_UP);

    }

    public void eventsButtons() {
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_STORAGE_CODE);

                    } else {
                        startDownLoad();
                    }


                } else {
                    startDownLoad();
                }
            }
        });
        btn_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MerchantDeatilOrderActivity.this, MapsActivityOrder.class);
                intent.putExtra("order", order);
                startActivity(intent);

            }
        });
        btn_deactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MerchantDeatilOrderActivity.this);
                builder.setMessage("¿Está seguro de anular la orden")
                        .setCancelable(false)
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deactivateOrder();
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

    public void deactivateOrder() {
        String url = Routes.ORDER_DEACTIVATE + "/" + order.getId();
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                Intent intent = new Intent(MerchantDeatilOrderActivity.this, CompanyOrdersActivity.class);
                                intent.putExtra("company", company);
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
        RequestQueue requestQueue = Volley.newRequestQueue(MerchantDeatilOrderActivity.this);
        requestQueue.add(stringRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void downloadPdf() {
        String url = Routes.URL + order.getUrl_order();
        System.out.println(url);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String tempTitle = order.getOrder_number();
        request.setTitle(tempTitle);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_STORAGE_CODE);

            } else {
                startDownLoad();
            }
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


        } else {
            startDownLoad();
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, tempTitle + ".pdf");
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        request.setMimeType("application/pdf");
        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        downloadManager.enqueue(request);
    }

    public void startDownLoad() {
        String url = Routes.URL + order.getUrl_order();
        String tempTitle = order.getOrder_number();
        System.out.println(url);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        /*request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Descargar");
        request.setDescription("Descargando comprobante de orden");
        request.allowScanningByMediaScanner();
*/
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle("Descargar") //Download Manager Title
                .setDescription("Descargando comprobante de orden")
                .setMimeType("application/pdf")
                .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        tempTitle + ".pdf"
                );

        downloadManager.enqueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startDownLoad();
                } else {
                    Toast.makeText(MerchantDeatilOrderActivity.this, "Habilite el permiso de almacenamiento", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    private void getOrdersDetail() {
        lis_products = new ArrayList<>();
        String url = Routes.ORDER_DETAIL + "/" + order.getId();
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("details"));
                                System.out.println("ARRAY \n" + array + "  =>>>");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject type_object = array.getJSONObject(i);
                                    DetailOrder detail = new DetailOrder();
                                    detail.setId(type_object.getInt("id"));
                                    detail.setId_order( order.getId());
                                    detail.setId_product(type_object.getInt("articulo_id"));
                                    detail.setCant(type_object.getInt("cantidad"));
                                    detail.setProduct_name(type_object.getString("articulo"));
                                    detail.setProduct_desc(type_object.getString("descripcion"));
                                    detail.setPrice_unit(type_object.getString("precio_venta"));
                                    detail.setPrice_total(type_object.getString("precio"));
                                    lis_products.add(detail);
                                    System.out.println("lista \n" + type_object + "  =>>>");
                                }

                                ReviewListProductstAdapter orderListAdapter = new ReviewListProductstAdapter(MerchantDeatilOrderActivity.this, lis_products);
                                recyclerView.setAdapter(orderListAdapter);

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
        RequestQueue requestQueue = Volley.newRequestQueue(MerchantDeatilOrderActivity.this);
        requestQueue.add(stringRequest);
    }
}