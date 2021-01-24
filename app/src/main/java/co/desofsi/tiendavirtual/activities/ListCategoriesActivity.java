package co.desofsi.tiendavirtual.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import co.desofsi.tiendavirtual.R;
import co.desofsi.tiendavirtual.adapters.ListCategoriesAdapter;
import co.desofsi.tiendavirtual.adapters.ListProductstAdapter;
import co.desofsi.tiendavirtual.routes.Routes;
import co.desofsi.tiendavirtual.models.Category;
import co.desofsi.tiendavirtual.models.Company;
import co.desofsi.tiendavirtual.models.DateClass;
import co.desofsi.tiendavirtual.models.DetailOrder;
import co.desofsi.tiendavirtual.models.Order;
import co.desofsi.tiendavirtual.models.Product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListCategoriesActivity extends AppCompatActivity {

    private Company company;
    private ImageView image_baner;
    private TextView text_baner_name, text_baner_des;
    private ImageButton btn_back;
    private ImageButton btn_shop;
    private RecyclerView recyclerView_list_categories;
    private SharedPreferences sharedPreferences;
    private ArrayList<Category> lis_categories;

    ///ATRIBTUOS DE LOS PRODUCTOS
    public static ArrayList<Product> list_products;
    public static Product produc_category;
    public static RecyclerView recyclerView_list_products;
    public static SwipeRefreshLayout refreshLayout;
    public static Order order;
    public static DetailOrder detailOrder ;
    public static ArrayList<DetailOrder> list_detail;
    ///fechas
    private DateClass dateClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_categories);
        super.onResume();
        init();
    }

    private void init() {
        dateClass = new DateClass();
        order = new Order();
        detailOrder =  new DetailOrder();
        company = (Company) getIntent().getExtras().getSerializable("company_selected");

        sharedPreferences = ListCategoriesActivity.this.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        int id_user = sharedPreferences.getInt("id", 0);
        String name = sharedPreferences.getString("name","");
        order.setId_user(id_user);
        order.setId_company(company.getId());
        order.setName_company(company.getCompany_name());
        order.setName_customer(name);
        order.setDate(dateClass.dateToday());
        order.setDate_format(dateClass.dateTodayFormatServer());


        lis_categories = new ArrayList<>();
        list_detail = new ArrayList<>();

        image_baner = findViewById(R.id.list_categories_image_baner);
        text_baner_name = findViewById(R.id.list_categories_text_baner);
        text_baner_des = findViewById(R.id.list_categories_text_description);
        btn_back = findViewById(R.id.list_categories_btn_back);
        btn_shop = findViewById(R.id.list_categories_btn_shop);
          recyclerView_list_categories = findViewById(R.id.list_categories_lista_recycler);
        //recyclerView_list_categories.setHasFixedSize(true);
        //LinearLayoutManager mLayoutManager = new GridLayoutManager(ListCategoriesActivity.this, 2);
        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(ListCategoriesActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView_list_categories.setLayoutManager(horizontalLayoutManagaer);


        ///COMPONENTES DE LA LISTA DE PRODUCTOS
        recyclerView_list_products = findViewById(R.id.list_categories_products_recycler);
        //recyclerView_list_products.setHasFixedSize(true);
        //LinearLayoutManager mLayoutManager = new GridLayoutManager(ListCategoriesActivity.this, 2);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ListCategoriesActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView_list_products.setLayoutManager(mLayoutManager);
        // recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = findViewById(R.id.list_categories_products_refresh_swipe);


        ///COMPONENTES DEL ACTIVITY
        Picasso.get().load(Routes.URL + company.getUrl_merchant()).into(image_baner);
        text_baner_name.setText(company.getCompany_name());
        text_baner_des.setText(company.getCompany_description());


        ///CARGAR CATEGORIAS
        getCompanies();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListCategoriesActivity.this,DetailOrderActivity.class));
            }
        });

    }

    public void getCompanies() {
        lis_categories = new ArrayList<>();
        String url = Routes.CATEGORIES + "/" + company.getId();
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("categories"));

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject type_object = array.getJSONObject(i);

                                    Category category = new Category();
                                    category.setId(type_object.getInt("id"));
                                    category.setName(type_object.getString("name"));
                                    category.setDescription(type_object.getString("description"));
                                    category.setUrl_image(type_object.getString("url_image"));
                                    lis_categories.add(category);

                                }

                                ListCategoriesAdapter listCategoriesAdapter = new ListCategoriesAdapter(ListCategoriesActivity.this, lis_categories);
                                //RecyclerCategoriesAdapter listCategoriesAdapter = new RecyclerCategoriesAdapter(lis_categories, this);
                                recyclerView_list_categories.setAdapter(listCategoriesAdapter);


                            }

                        } catch (Exception e) {

                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


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


        RequestQueue requestQueue = Volley.newRequestQueue(ListCategoriesActivity.this);
        requestQueue.add(stringRequest);

    }

    public void getProducts(Category category) {
        list_products = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        String url = Routes.PRODUCTS + "/" + category.getId();
        // System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("products"));

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject type_object = array.getJSONObject(i);

                                    Product product = new Product();
                                    product.setId(type_object.getInt("id"));
                                    product.setName(type_object.getString("name"));
                                    product.setDescription(type_object.getString("description"));
                                    product.setSale_price(type_object.getString("sale_price"));
                                    product.setStock(type_object.getInt("stock"));
                                    product.setUrl_image(type_object.getString("url_image"));

                                    list_products.add(product);

                                }
                                ListProductstAdapter listProductstAdapter = new ListProductstAdapter(ListCategoriesActivity.this, list_products);
                                recyclerView_list_products.setAdapter(listProductstAdapter);

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
        RequestQueue requestQueue = Volley.newRequestQueue(ListCategoriesActivity.this);
        requestQueue.add(stringRequest);
    }


}
