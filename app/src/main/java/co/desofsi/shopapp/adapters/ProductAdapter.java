package co.desofsi.shopapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import co.desofsi.shopapp.R;
import co.desofsi.shopapp.models.Product;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {

    private Context context;
    private ArrayList<Product> list;

    public ProductAdapter(Context context, ArrayList<Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_home_products, parent, false);
        return new ProductAdapter.ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductHolder holder, final int position) {

        final Product product = list.get(position);

        // Picasso.get().load(Constant.URL+"img/users/"+specialty.getDoctor().getUrl_image()).into(holder.image_doctor);

        System.out.println(product.getUrl_image());
        Picasso.get().load(product.getUrl_image()).into(holder.imageProduct); //descomentar en produccion
        //Picasso.get().load(estate.getUrl_image()).into(holder.imageProduct);
        // Picasso.get().load("https://i.imgur.com/tGbaZCY.jpg").into(holder.imageProduct);
        //  holder.txt_name_estate.setText(estate.getName());
        // holder.txt_addres_estate.setText(estate.getAddress());
        holder.txtNameProduct.setText(product.getName());
     //   holder.txtDescription.setText(product.getDescription());
        holder.txtPrice.setText("$"+product.getSale_price());
        //   holder.cardView.setCardBackgroundColor(Color.parseColor(String.valueOf(R.color.colorRed)));
        holder.cardViewProduct.setRadius(40);

      /*  holder.cardViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent((HomeActivity)context, MenuEstatesActivity.class);
                context.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ProductHolder extends RecyclerView.ViewHolder {

        private TextView txtNameProduct, txtPrice, txtDescription;
        private ImageView imageProduct;
        private CardView cardViewProduct;
        private ImageButton btnAdd;


        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            txtNameProduct = itemView.findViewById(R.id.txtNameHP);
         //   txtDescription = itemView.findViewById(R.id.txtDescriptionHP);
            txtPrice = itemView.findViewById(R.id.txtPriceHP);
            imageProduct = itemView.findViewById(R.id.imageHP);
            cardViewProduct = itemView.findViewById(R.id.cardViewHP);
            btnAdd = itemView.findViewById(R.id.item_list_products_btn_shop);
        }
    }

}