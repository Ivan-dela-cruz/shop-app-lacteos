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
import co.desofsi.shopapp.adaptersdelivery.OrderListDeliveryAdapter;
import co.desofsi.shopapp.models.OrderRequestDelivery;
import co.desofsi.shopapp.routes.Routes;

public class RequestDeliveryFragment extends Fragment {
    private View view;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;
    private ArrayList<OrderRequestDelivery> lis_orders;

    public RequestDeliveryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_delivery_request, container, false);
        init();
        getOrders();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        refreshLayout = view.findViewById(R.id.delivery_fragment_swipe);
        recyclerView = view.findViewById(R.id.delivery_fragment_recycler);
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
        System.out.println(Routes.GET_REQUEST_DELIVERY);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Routes.GET_REQUEST_DELIVERY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("orders"));

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject type_object = array.getJSONObject(i);
                                    OrderRequestDelivery order_request = new OrderRequestDelivery();
                                    order_request.setId(type_object.getInt("id"));
                                    order_request.setId_delivery(type_object.getInt("id_delivery"));
                                    order_request.setId_order(type_object.getInt("id_order"));
                                    order_request.setId_company(type_object.getInt("id_company"));

                                    order_request.setName_customer(type_object.getString("name_customer"));
                                    order_request.setName_company(type_object.getString("name_company"));
                                    order_request.setCompany_address(type_object.getString("company_address"));
                                    order_request.setOrder_number(type_object.getString("order_number"));
                                    order_request.setTotal(type_object.getString("total"));
                                    order_request.setStatus(type_object.getString("status"));
                                    order_request.setStatus_request(type_object.getString("status_request"));
                                    order_request.setDate(type_object.getString("datetime"));
                                    order_request.setUrl_order(type_object.getString("url_order"));
                                    order_request.setLongitude_company(type_object.getString("longitude_com"));
                                    order_request.setLatitude_company(type_object.getString("latitude_com"));
                                    order_request.setLongitude_order(type_object.getString("longitude_order"));
                                    order_request.setLatitude_order(type_object.getString("latitude_order"));

                                    lis_orders.add(order_request);
                                }
                                OrderListDeliveryAdapter orderListAdapter = new OrderListDeliveryAdapter(getContext(), lis_orders);
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
