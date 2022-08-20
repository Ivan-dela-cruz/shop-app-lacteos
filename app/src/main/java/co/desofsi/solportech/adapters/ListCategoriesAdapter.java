package co.desofsi.solportech.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import co.desofsi.solportech.R;
import co.desofsi.solportech.activities.HomeActivity;
import co.desofsi.solportech.activities.ListCategoriesActivity;
import co.desofsi.solportech.merchantsactivities.OrderFragmentMerchant;
import co.desofsi.solportech.routes.Routes;
import co.desofsi.solportech.models.Category;
import co.desofsi.solportech.models.Product;

public class ListCategoriesAdapter extends RecyclerView.Adapter<ListCategoriesAdapter.ListCategoriesHolder> {

    private Context context;
    private ArrayList<Category> list;
    private SharedPreferences sharedPreferences;

    public void getProducts(Category category) {
        ListCategoriesActivity.list_products = new ArrayList<>();
        OrderFragmentMerchant.refreshLayout.setRefreshing(true);
        String url = Routes.PRODUCTS + "/" + category.getId();
        // System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("productos"));

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject type_object = array.getJSONObject(i);

                                    Product product = new Product();
                                    product.setId(type_object.getInt("id"));
                                    product.setName(type_object.getString("nombre"));
                                    product.setDescription(type_object.getString("descripcion"));
                                    product.setSale_price(type_object.getString("precio_venta"));
                                    product.setStock(type_object.getInt("stock"));
                                    product.setUrl_image(type_object.getString("image"));

                                    System.out.println("PRODUCTO ENCOTRADO : " + product.getName());

                                    ListCategoriesActivity.list_products.add(product);

                                }
                                ListProductstAdapter listProductstAdapter = new ListProductstAdapter(((HomeActivity) context), ListCategoriesActivity.list_products);
                                OrderFragmentMerchant.recyclerView_list_products.setAdapter(listProductstAdapter);

                            }
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                        OrderFragmentMerchant.refreshLayout.setRefreshing(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OrderFragmentMerchant.refreshLayout.setRefreshing(false);
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
        RequestQueue requestQueue = Volley.newRequestQueue(((HomeActivity) context));
        requestQueue.add(stringRequest);
    }

    public ListCategoriesAdapter(Context context, ArrayList<Category> list) {

        this.context = context;
        this.list = list;
        sharedPreferences = ((HomeActivity) context).getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);


    }

    @NonNull
    @Override
    public ListCategoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_categories_recycler, parent, false);
        return new ListCategoriesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCategoriesHolder holder, final int position) {

        final Category categoy = list.get(position);
        if(position == 0){
            getProducts(categoy);
        }


        Picasso.get().load(Routes.URL + categoy.getUrl_image()).into(holder.imageView_specialty);
        // System.out.println(company.getName());
        holder.txt_name.setText(categoy.getName());

        String[] colors = {
                "FF8A65",//md_deep_orange_300
                "4DD0E1",//md_cyan_300
                "81C784",//md_green_300
                "9575CD",//md_deep_purple_300
                "E57373",//md_red_300
                "64B5F6",//md_blue_300
                "F06292",//md_pink_300
                "DCE775",//md_lime_300
                "BA68C8",//md_purple_300
                "7986CB",//md_indigo_300
        };
        Random rand = new Random();

        holder.cardView.setCardBackgroundColor(Color.parseColor("#"+colors[rand.nextInt(colors.length)]));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProducts(categoy);
            }
        });

        /*
         holder.btn_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("id encontrado"+company.getId());
                Intent intent =  new Intent(((HomeActivity)context), ListComapiesActivity.class);
                intent.putExtra("type_company_selected",company);
                intent.putExtra("position",position);
                context.startActivity(intent);

            }
        });
         */
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ListCategoriesHolder extends RecyclerView.ViewHolder {

        private TextView txt_name;
        private ImageView imageView_specialty;
        private CardView cardView;

        public ListCategoriesHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.item_list_categories_name_id);
            imageView_specialty = (ImageView) itemView.findViewById(R.id.item_list_categories_image);
            cardView = itemView.findViewById(R.id.item_list_categories_cardview_id);
        }


    }
}