package co.desofsi.tiendavirtual.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import co.desofsi.tiendavirtual.R;
import co.desofsi.tiendavirtual.adapters.ListCompanyAdapter;
import co.desofsi.tiendavirtual.routes.Routes;
import co.desofsi.tiendavirtual.maps.MapsActivity;
import co.desofsi.tiendavirtual.models.Company;
import co.desofsi.tiendavirtual.models.TypeCompany;

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

public class ListComapiesActivity extends AppCompatActivity {

    private int position = 0, id = 0;
    private TypeCompany typeCompany_selected;
    private ImageView imageView;
    private TextView list_companies_text_baner;
    private TextView text_descrip;
    private RecyclerView recyclerView;
    private ArrayList<Company> lis_companies;
    private SwipeRefreshLayout refreshLayout;
    private SharedPreferences sharedPreferences;
    private ImageButton btn_back, btn_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_comapies);
        init();
    }

    public void init() {
        typeCompany_selected = (TypeCompany) getIntent().getExtras().getSerializable("type_company_selected");
        position = (int) getIntent().getIntExtra("id", 0);
        imageView = findViewById(R.id.list_companies_image_baner);
        text_descrip = findViewById(R.id.list_companies_text_description);
        list_companies_text_baner = findViewById(R.id.list_companies_text_baner);
        btn_back = findViewById(R.id.list_companies_btn_back);
        btn_map = findViewById(R.id.list_companies_btn_map);

        Picasso.get().load(Routes.URL + typeCompany_selected.getUrl_image()).into(imageView);
        list_companies_text_baner.setText(typeCompany_selected.getName());
        text_descrip.setText(typeCompany_selected.getDescription());

        sharedPreferences = ListComapiesActivity.this.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.list_companies_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(ListComapiesActivity.this, 2);
        //LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        // recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = findViewById(R.id.list_companies_refresh_swipe);


        getCompanies();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCompanies();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListComapiesActivity.this, MapsActivity.class);
                intent.putExtra("type_company_selected", typeCompany_selected);
                startActivity(intent);
            }
        });
    }

    public void getCompanies() {
        lis_companies = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        String url = Routes.COMPANIES + "/" + typeCompany_selected.getId();
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
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
                                ListCompanyAdapter listCompanyAdapter = new ListCompanyAdapter(ListComapiesActivity.this, lis_companies);
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
        RequestQueue requestQueue = Volley.newRequestQueue(ListComapiesActivity.this);
        requestQueue.add(stringRequest);
    }
}
