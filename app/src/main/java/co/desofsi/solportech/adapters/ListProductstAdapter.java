package co.desofsi.solportech.adapters;

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
import co.desofsi.solportech.R;
import co.desofsi.solportech.activities.HomeActivity;
import co.desofsi.solportech.activities.ShowProductActivity;
import co.desofsi.solportech.models.Product;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_home_products, parent, false);
        return new ListCategoriesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCategoriesHolder holder, final int position) {

        final Product product = list.get(position);

        System.out.println("===>>>== "+product.getUrl_image());
        Picasso.get().load(product.getUrl_image()).into(holder.imageHP);
        holder.txtNameHP.setText(product.getName());
//        holder.txt_description.setText(product.getDescription());
        holder.txtPriceHP.setText("$ "+product.getSale_price());
        holder.btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(((HomeActivity)context), ShowProductActivity.class);
                intent.putExtra("product_selected",product);
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });
        //holder.txtPriceHP.setText(product.getSale_price());


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

        private TextView txtNameHP, txt_description, txtPriceHP;
        private ImageView imageHP;
        private ImageButton btnAddCart;

        private CardView cardView;

        public ListCategoriesHolder(@NonNull View itemView) {
            super(itemView);
            txtNameHP = itemView.findViewById(R.id.txtNameHP);
           // txt_description = itemView.findViewById(R.id.item_list_products_description_id);
            txtPriceHP = itemView.findViewById(R.id.txtPriceHP);
            btnAddCart = itemView.findViewById(R.id.btnAddCart);
            imageHP = (ImageView) itemView.findViewById(R.id.imageHP);
            cardView = itemView.findViewById(R.id.cardViewHP);
        }


    }
}
