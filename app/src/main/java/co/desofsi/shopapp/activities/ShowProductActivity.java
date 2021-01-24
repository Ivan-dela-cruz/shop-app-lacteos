package co.desofsi.shopapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import co.desofsi.shopapp.R;
import co.desofsi.shopapp.routes.Routes;
import co.desofsi.shopapp.models.Category;
import co.desofsi.shopapp.models.DetailOrder;
import co.desofsi.shopapp.models.Product;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ShowProductActivity extends AppCompatActivity {

    private Product product;
    private Category category;
    private SharedPreferences sharedPreferences;

    private ImageView imageView_product;
    private Button btn_addcar_shop, show_product_noty_items;
    private TextView text_name, text_description, textView_cantidad, text_price;
    private ImageButton btn_add_item, btn_min_item, show_product_btn_shop, show_product_btn_back;
    private int cant = 1;
    private DetailOrder detailOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);
        init();

    }

    private void init() {

        product = (Product) getIntent().getExtras().getSerializable("product_selected");
        imageView_product = findViewById(R.id.show_product_image);
        show_product_btn_shop = findViewById(R.id.show_product_btn_shop);
        show_product_btn_back = findViewById(R.id.show_product_btn_back);
        show_product_noty_items = findViewById(R.id.show_product_noty_items);
        btn_addcar_shop = findViewById(R.id.show_product_btn_add_car_shop);
        text_name = findViewById(R.id.show_product_name);
        text_description = findViewById(R.id.show_product_description);
        text_price = findViewById(R.id.show_product_price);
        textView_cantidad = findViewById(R.id.show_product_text_cantidad);
        btn_add_item = findViewById(R.id.show_product_btn_add);
        btn_min_item = findViewById(R.id.show_product_btn_min);

        //RENDERIZAR LOS DATOS

        Picasso.get().load(Routes.URL + product.getUrl_image()).into(imageView_product);
        text_name.setText(product.getName());
        text_description.setText(product.getDescription());
        text_price.setText("$ " + product.getSale_price());
        textView_cantidad.setText(String.valueOf(cant));
        show_product_noty_items.setText("" + ListCategoriesActivity.list_detail.size());


        eventsButtons();
    }

    private void eventsButtons() {
        btn_min_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cant = Integer.parseInt(textView_cantidad.getText().toString());
                    if (cant > 1) {
                        cant--;
                        textView_cantidad.setText(String.valueOf(cant));
                    }

                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        });
        btn_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cant = Integer.parseInt(textView_cantidad.getText().toString());
                    cant++;
                    textView_cantidad.setText(String.valueOf(cant));


                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        });
        btn_addcar_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    int cont  = 0;
                    double price = Double.parseDouble(product.getSale_price());
                    double total = price * cant;
                    detailOrder = new DetailOrder();
                    detailOrder.setId_product(product.getId());
                    detailOrder.setPrice_unit(product.getSale_price());
                    detailOrder.setUrl_image(product.getUrl_image());
                    detailOrder.setProduct_name(product.getName());
                    detailOrder.setProduct_desc(product.getDescription());
                    detailOrder.setCant(cant);
                    detailOrder.setPrice_total(String.valueOf(total));
                    for (DetailOrder detail: ListCategoriesActivity.list_detail) {
                        if(detail.getProduct_name().equals(detailOrder.getProduct_name())){
                            double new_price = Double.parseDouble(detail.getPrice_total());
                            int cant = detail.getCant();
                            double detail_price = Double.parseDouble(detailOrder.getPrice_total());
                            int detail_cant = detailOrder.getCant();
                            detail.setCant(cant+detail_cant);
                            detail.setPrice_total(String.valueOf(new_price+detail_price));
                            cont++;
                        }
                    }
                    if(cont==0){
                        ListCategoriesActivity.list_detail.add(detailOrder);
                        show_product_noty_items.setText("" + ListCategoriesActivity.list_detail.size());
                        Toast.makeText(ShowProductActivity.this, "Se agrego un producto al carrito", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ShowProductActivity.this, "Se actualizó un producto del carrito", Toast.LENGTH_SHORT).show();

                    }



                } catch (Exception e) {

                }


            }
        });

        show_product_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        show_product_btn_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowProductActivity.this, DetailOrderActivity.class));
            }
        });
    }
}
