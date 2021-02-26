package co.desofsi.shopapp.merchantsactivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.desofsi.shopapp.R;
import co.desofsi.shopapp.activities.DetailOrderActivity;
import co.desofsi.shopapp.activities.ListCategoriesActivity;
import co.desofsi.shopapp.adapters.ListCategoriesAdapter;
import co.desofsi.shopapp.adaptersmerchant.ListMyCompanyAdapter;
import co.desofsi.shopapp.models.Category;
import co.desofsi.shopapp.models.Company;
import co.desofsi.shopapp.models.DateClass;
import co.desofsi.shopapp.models.DetailOrder;
import co.desofsi.shopapp.models.Order;
import co.desofsi.shopapp.models.Product;
import co.desofsi.shopapp.routes.Routes;


public class OrderFragmentMerchant extends Fragment {
    private View view;

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
        dateClass = new DateClass();
        order = new Order();
        detailOrder =  new DetailOrder();


        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        int id_user = sharedPreferences.getInt("id", 0);
        String name = sharedPreferences.getString("name","");
        order.setId_user(id_user);
        order.setId_company(1);
        order.setName_company("SIMON RODRIGUEZ");
        order.setName_customer(name);
        order.setDate(dateClass.dateToday());
        order.setDate_format(dateClass.dateTodayFormatServer());


        lis_categories = new ArrayList<>();
        list_detail = new ArrayList<>();

        image_baner = view.findViewById(R.id.list_categories_image_baner);
        text_baner_name = view.findViewById(R.id.list_categories_text_baner);
        text_baner_des = view.findViewById(R.id.list_categories_text_description);
        btn_back = view.findViewById(R.id.list_categories_btn_back);
        btn_shop = view.findViewById(R.id.list_categories_btn_shop);
        recyclerView_list_categories = view.findViewById(R.id.list_categories_lista_recycler);
        //recyclerView_list_categories.setHasFixedSize(true);
        //LinearLayoutManager mLayoutManager = new GridLayoutManager(ListCategoriesActivity.this, 2);
        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_list_categories.setLayoutManager(horizontalLayoutManagaer);


        ///COMPONENTES DE LA LISTA DE PRODUCTOS
        recyclerView_list_products = view.findViewById(R.id.list_categories_products_recycler);
        //recyclerView_list_products.setHasFixedSize(true);
      //  LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView_list_products.setLayoutManager(mLayoutManager);
        // recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.list_categories_products_refresh_swipe);


        ///COMPONENTES DEL ACTIVITY
        //Picasso.get().load(Routes.URL + company.getUrl_merchant()).into(image_baner);
        text_baner_name.setText("SIMÓN RODIRGUEZ");
        text_baner_des.setText("Asociación de Productores de Leche");


        ///CARGAR CATEGORIAS
        getCompanies();


        btn_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), DetailOrderActivity.class));
            }
        });

    }

    public void getCompanies() {
        lis_categories = new ArrayList<>();
        String url = Routes.CATEGORIES;
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("categorias"));

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject type_object = array.getJSONObject(i);

                                    Category category = new Category();
                                    category.setId(type_object.getInt("id"));
                                    category.setName(type_object.getString("nombre"));
                                    category.setDescription(type_object.getString("descripcion"));
                                    lis_categories.add(category);

                                }

                                ListCategoriesAdapter listCategoriesAdapter = new ListCategoriesAdapter(getContext(), lis_categories);
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


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }
}
