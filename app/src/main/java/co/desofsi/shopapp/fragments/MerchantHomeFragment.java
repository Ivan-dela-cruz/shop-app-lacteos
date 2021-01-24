package co.desofsi.shopapp.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


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
import co.desofsi.shopapp.adaptersmerchant.OrderListMerchantAdapter;
import co.desofsi.shopapp.routes.Routes;
import co.desofsi.shopapp.models.Order;


public class MerchantHomeFragment extends Fragment {
    private View view;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;
    private ArrayList<Order> lis_orders;

    public MerchantHomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_merchant, container, false);
        init();
        getOrders();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        refreshLayout = view.findViewById(R.id.merchant_fragment_swipe);
        recyclerView = view.findViewById(R.id.merchant_fragment_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        //LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrders();
            }
        });
    }


    private void getOrders() {
        lis_orders = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        System.out.println(Routes.LOGIN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Routes.ORDERS,
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
                                    order.setLongitude_company(type_object.getString("longitude_com"));
                                    order.setLatitude_company(type_object.getString("latitude_com"));
                                    order.setLongitude_order(type_object.getString("longitude_order"));
                                    order.setLatitude_order(type_object.getString("latitude_order"));

                                    lis_orders.add(order);
                                }
                                OrderListMerchantAdapter orderListAdapter = new OrderListMerchantAdapter(getContext(), lis_orders);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}
