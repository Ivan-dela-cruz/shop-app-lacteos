package co.desofsi.shopapp.merchantsactivities;

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
import co.desofsi.shopapp.adaptersmerchant.ListMyCompanyAdapter;
import co.desofsi.shopapp.models.Company;
import co.desofsi.shopapp.routes.Routes;


public class OrderFragmentMerchant extends Fragment {
    private View view;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;
    private ArrayList<Company> lis_companies;

    public OrderFragmentMerchant() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_merchant, container, false);
        init();

        getCompanies();


        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        refreshLayout = view.findViewById(R.id.order_fragment_swipe);
        recyclerView = view.findViewById(R.id.order_fragment_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        //LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCompanies();
            }
        });
    }


    public void getCompanies() {
        lis_companies = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Routes.MY_COMPANIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("companies"));

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject type_object = array.getJSONObject(i);

                                    Company company = new Company();
                                    company.setId(type_object.getInt("id"));
                                    company.setCompany_name(type_object.getString("company_name"));
                                    company.setCompany_address(type_object.getString("company_address"));
                                    company.setCompany_phone(type_object.getString("company_phone"));
                                    company.setCompany_description(type_object.getString("company_description"));
                                    company.setLatitude(type_object.getString("latitude"));
                                    company.setLongitude(type_object.getString("longitude"));
                                    company.setUrl_merchant(type_object.getString("url_merchant"));

                                    lis_companies.add(company);

                                }
                                ListMyCompanyAdapter listCompanyAdapter = new ListMyCompanyAdapter(getActivity(), lis_companies);
                                recyclerView.setAdapter(listCompanyAdapter);

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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}
