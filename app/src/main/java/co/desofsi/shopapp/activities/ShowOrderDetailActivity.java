package co.desofsi.shopapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
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
import co.desofsi.shopapp.routes.Routes;
import co.desofsi.shopapp.models.DateClass;
import co.desofsi.shopapp.models.DetailOrder;
import co.desofsi.shopapp.models.Order;

public class ShowOrderDetailActivity extends AppCompatActivity {

    private ArrayList<DetailOrder> lis_products;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;

    private ImageButton btn_home;
    private TextView txt_order_number, txt_order_customer, txt_order_data, txt_order_company, txt_order_total, txt_status;
    private Button btn_reset;
    private ScrollView scrollView;
    private DateClass dateClass;
    private Order order;

    private static final int PERMISSION_STORAGE_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order_detail);
        try{
            order = new Order();
            order = (Order) getIntent().getExtras().getSerializable("order");
            init();
            eventsButtons();
            loadReviewOrder();
            getOrdersDetail();

        }catch (Exception e){
            Toast.makeText(ShowOrderDetailActivity.this, "Error: "+e, Toast.LENGTH_SHORT).show();


        }

    }

    public void init() {
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        dateClass = new DateClass();
        txt_order_number = findViewById(R.id.show_detail_order_txt_order_number);
        txt_order_customer = findViewById(R.id.show_detail_order_txt_customer);
        txt_order_data = findViewById(R.id.show_detail_order_txt_date);
        txt_order_company = findViewById(R.id.show_detail_order_txt_company);
        txt_order_total = findViewById(R.id.show_detail_order_txt_total);
        btn_home = findViewById(R.id.show_detail_order_btn_back);
        btn_reset = findViewById(R.id.show_detail_order_btn_home);
        scrollView = findViewById(R.id.show_detail_order_scroll);
        recyclerView = findViewById(R.id.show_detail_order_recycler);
        txt_status = findViewById(R.id.show_detail_order_txt_status);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ShowOrderDetailActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void loadReviewOrder() {

        txt_order_total.setText("$ " + order.getTotal());
        txt_order_company.setText(order.getName_company());
        txt_order_customer.setText(order.getName_customer());
        txt_order_data.setText(dateClass.dateFormatHuman(order.getDate()));
        txt_order_number.setText(order.getOrder_number());
        txt_status.setText(order.getStatus());
        scrollView.pageScroll(View.FOCUS_UP);

    }

    public void eventsButtons() {
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(ShowOrderDetailActivity.this, "Habilite el permiso de almacenamiento", Toast.LENGTH_SHORT).show();

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
                                          /*
                                            "id_order": 1,
                                            "id_product": 2,
                                            "quanty": 1,
                                            "product_name": "Mesa color azu",
                                            "product_desc": "mesa azul 4 sillas",
                                            "price_unit": 200,
                                            "price_total": 200,
                                            "created_at": "2020-07-29T04:51:30.000000Z",

                                           */
                                    DetailOrder detail = new DetailOrder();
                                    detail.setId(type_object.getInt("id"));
                                    detail.setId_order(type_object.getInt("id_order"));
                                    detail.setId_product(type_object.getInt("id_product"));
                                    detail.setCant(type_object.getInt("quanty"));
                                    detail.setProduct_name(type_object.getString("product_name"));
                                    detail.setProduct_desc(type_object.getString("product_desc"));
                                    detail.setPrice_unit(type_object.getString("price_unit"));
                                    detail.setPrice_total(type_object.getString("price_total"));
                                    lis_products.add(detail);
                                }
                                System.out.println("lista \n" + lis_products.size() + "  =>>>");
                                ReviewListProductstAdapter orderListAdapter = new ReviewListProductstAdapter(ShowOrderDetailActivity.this, lis_products);
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
        RequestQueue requestQueue = Volley.newRequestQueue(ShowOrderDetailActivity.this);
        requestQueue.add(stringRequest);
    }

    /* METODO PARA DESCARGAR PDF
    public void downloadPdf() {
        String url = Constant.URL + order.getUrl_order();
        System.out.println(url);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String tempTitle = order.getOrder_number();
        request.setTitle(tempTitle);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, tempTitle + ".pdf");
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        request.setMimeType("application/pdf");
        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        downloadManager.enqueue(request);
    }
     */
}

