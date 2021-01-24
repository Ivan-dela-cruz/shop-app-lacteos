package co.desofsi.shopapp.merchantsactivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import co.desofsi.shopapp.R;
import co.desofsi.shopapp.activities.HomeActivity;
import co.desofsi.shopapp.adaptersmerchant.OrderListMerchantAdapter;
import co.desofsi.shopapp.models.Company;
import co.desofsi.shopapp.models.Order;
import co.desofsi.shopapp.routes.Routes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

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

public class CompanyOrdersActivity extends AppCompatActivity {

    private Company company;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;
    private ArrayList<Order> lis_orders;
    private ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_orders);
        company = (Company) getIntent().getExtras().getSerializable("company");

        init();

        getOrders();
    }


    private void init() {
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        refreshLayout = findViewById(R.id.order_fragment_swipe);
        recyclerView = findViewById(R.id.order_fragment_recycler);
        btn_back = findViewById(R.id.company_order_btn_back);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(CompanyOrdersActivity.this, 1);
        //LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrders();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CompanyOrdersActivity.this, HomeActivity.class));
            }
        });
    }


    private void getOrders() {
        lis_orders = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        String url = Routes.ORDERS_COMPANY + "/" + company.getId();
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("orders"));

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject type_object = array.getJSONObject(i);
                                    Order order = new Order();
                                    order.setId(type_object.getInt("id"));
                                    order.setId_customer(type_object.getInt("id_customer"));
                                    order.setId_user(type_object.getInt("id_user"));
                                    order.setId_company(type_object.getInt("id_company"));
                                    order.setName_customer(type_object.getString("name_customer"));
                                    order.setName_company(type_object.getString("name_company"));
                                    order.setOrder_number(type_object.getString("order_number"));
                                    order.setTotal(type_object.getString("total"));
                                    order.setStatus(type_object.getString("status"));
                                    order.setDate(type_object.getString("created_at"));
                                    order.setUrl_order(type_object.getString("url_order"));
                                    order.setCompany(company);
                                    lis_orders.add(order);
                                }
                                OrderListMerchantAdapter orderListAdapter = new OrderListMerchantAdapter(CompanyOrdersActivity.this, lis_orders);
                                recyclerView.setAdapter(orderListAdapter);

                            }
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                        refreshLayout.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        refreshLayout.setRefreshing(false);
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
        RequestQueue requestQueue = Volley.newRequestQueue(CompanyOrdersActivity.this);
        requestQueue.add(stringRequest);
    }
}
