package co.desofsi.solportech.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import co.desofsi.solportech.R;
import co.desofsi.solportech.adapters.ProductAdapter;
import co.desofsi.solportech.adapters.SliderAdapter;
import co.desofsi.solportech.models.Product;
import co.desofsi.solportech.models.Slider;
import co.desofsi.solportech.routes.Routes;

public class HomeFragment extends Fragment  {
    private View view;
    private RecyclerView recyclerHomeSlider, recyclerHomeProducts;
    private ArrayList<Slider> list_sliders;
    private ArrayList<Product> list_products;
    private SwipeRefreshLayout refreshLayout;
  //  private TypeCompanyAdapter typeCompanyAdapter;
    private MaterialToolbar toolbar;
    private SharedPreferences sharedPreferences;
    JSONArray arraySliders, arrayProducts;
    private SliderAdapter sliderAdapter;
    private ProductAdapter productAdapter;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        return view;
    }

    public void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerHomeProducts = view.findViewById(R.id.recyclerHomeProducts);
        recyclerHomeSlider = view.findViewById(R.id.recyclerHomeSlider);
        recyclerHomeProducts.setHasFixedSize(true);
        recyclerHomeSlider.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
       //LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerHomeProducts.setLayoutManager(mLayoutManager);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerHomeSlider.setLayoutManager(horizontalLayoutManager);

        // recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.refreshHomeProducts);



        getLastProducts();
        getSliders();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLastProducts();
            }
        });

    }

    private void getSliders() {
        list_sliders = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Routes.SLIDERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                arraySliders = new JSONArray(object.getString("sliders"));

                                for (int i = 0; i < arraySliders.length(); i++) {
                                    JSONObject slider_object = arraySliders.getJSONObject(i);

                                    Slider slider_ = new Slider();
                                    slider_.setId(slider_object.getInt("id"));
                                    slider_.setTitle(slider_object.getString("title"));
                                    slider_.setDescription(slider_object.getString("description"));
                                    slider_.setUrlImage(slider_object.getString("image"));
                                    // System.out.println("objeto   : =>      " + specialty.getUrl_image() + " , " + specialty.getName() + "  , " + specialty.getDescription() + "  ,   " + specialty.getColor() + "  ,   " + specialty.getCreated_at() + "   ,  " + specialty.getStatus());
                                    list_sliders.add(slider_);

                                }
                                sliderAdapter = new SliderAdapter(getContext(), list_sliders);
                                recyclerHomeSlider.setAdapter(sliderAdapter);

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
    private void getLastProducts() {
        list_products = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Routes.PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                arrayProducts = new JSONArray(object.getString("productos"));

                                for (int i = 0; i < arrayProducts.length(); i++) {
                                    JSONObject product_object = arrayProducts.getJSONObject(i);

                                    Product product_ = new Product();
                                    product_.setId(product_object.getInt("id"));
                                    product_.setCategory_id(product_object.getInt("idcategoria"));
                                    product_.setCode(product_object.getString("codigo"));
                                    product_.setName(product_object.getString("nombre"));
                                    product_.setSale_price(product_object.getString("precio_venta"));
                                    product_.setStock(product_object.getInt("stock"));
                                    product_.setDescription(product_object.getString("descripcion"));
                                    product_.setUrl_image(product_object.getString("image"));
                                    // System.out.println("objeto   : =>      " + specialty.getUrl_image() + " , " + specialty.getName() + "  , " + specialty.getDescription() + "  ,   " + specialty.getColor() + "  ,   " + specialty.getCreated_at() + "   ,  " + specialty.getStatus());
                                    list_products.add(product_);

                                }
                                productAdapter = new ProductAdapter(getContext(), list_products);
                                recyclerHomeProducts.setAdapter(productAdapter);

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

   /* private void getSpecialties() {
        lis_companies = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        System.out.println(Routes.LOGIN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Routes.TYPE_COMPANES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("companies"));

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject type_object = array.getJSONObject(i);

                                    TypeCompany typeCompany = new TypeCompany();
                                    typeCompany.setId(type_object.getInt("id"));
                                    typeCompany.setName(type_object.getString("name"));
                                    typeCompany.setDescription(type_object.getString("description"));
                                    typeCompany.setUrl_image(type_object.getString("url_image"));
                                    lis_companies.add(typeCompany);

                                }
                                typeCompanyAdapter = new TypeCompanyAdapter(getContext(), lis_companies);
                                recyclerView.setAdapter(typeCompanyAdapter);

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
    }*/


}
