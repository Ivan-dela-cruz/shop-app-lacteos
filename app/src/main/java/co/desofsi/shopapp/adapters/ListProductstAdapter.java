package co.desofsi.shopapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import co.desofsi.shopapp.R;
import co.desofsi.shopapp.activities.ListCategoriesActivity;
import co.desofsi.shopapp.activities.ShowProductActivity;
import co.desofsi.shopapp.routes.Routes;
import co.desofsi.shopapp.models.Product;

public class ListProductstAdapter extends RecyclerView.Adapter<ListProductstAdapter.ListCategoriesHolder> {

    private Context context;
    private ArrayList<Product> list;


    public ListProductstAdapter(Context context, ArrayList<Product> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public ListCategoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_products_recycler, parent, false);
        return new ListCategoriesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCategoriesHolder holder, final int position) {

        final Product product = list.get(position);

        System.out.println("===>>>== "+product.getUrl_image());
        Picasso.get().load(product.getUrl_image()).into(holder.imageView_specialty);
        holder.txt_name.setText(product.getName());
        holder.txt_description.setText(product.getDescription());
        holder.txt_price.setText("$ "+product.getSale_price());
        holder.btn_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(((ListCategoriesActivity)context), ShowProductActivity.class);
                intent.putExtra("product_selected",product);
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });
        //holder.txt_price.setText(product.getSale_price());


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

        private TextView txt_name, txt_description, txt_price;
        private ImageView imageView_specialty;
        private ImageButton btn_shop;

        private CardView cardView;

        public ListCategoriesHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.item_list_products_name_id);
            txt_description = itemView.findViewById(R.id.item_list_products_description_id);
            txt_price = itemView.findViewById(R.id.item_list_products_sale_price_id);
            btn_shop = itemView.findViewById(R.id.item_list_products_btn_shop);
            imageView_specialty = (ImageView) itemView.findViewById(R.id.item_list_products_image);
            cardView = itemView.findViewById(R.id.item_list_products_cardview_id);
        }


    }
}
