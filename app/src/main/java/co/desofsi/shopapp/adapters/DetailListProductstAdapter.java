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
import co.desofsi.shopapp.activities.DetailOrderActivity;
import co.desofsi.shopapp.activities.ListCategoriesActivity;
import co.desofsi.shopapp.routes.Routes;
import co.desofsi.shopapp.models.DetailOrder;

public class DetailListProductstAdapter extends RecyclerView.Adapter<DetailListProductstAdapter.ListCategoriesHolder> {

    private Context context;
    private ArrayList<DetailOrder> list;


    public DetailListProductstAdapter(Context context, ArrayList<DetailOrder> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public ListCategoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_products_list_products_recycler, parent, false);
        return new ListCategoriesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListCategoriesHolder holder, final int position) {

        final DetailOrder product = list.get(position);

        System.out.println("=>>>>>>>>>>>>>>>>>>>>>=>>>>>><<<<<<<<<<<<<<<"+product.getProduct_name());
        Picasso.get().load(product.getUrl_image()).into(holder.imageView_specialty);
        holder.txt_name.setText(product.getProduct_name());
        holder.txt_description.setText(product.getProduct_desc());
        holder.txt_price.setText("$ " + product.getPrice_total());
        holder.txt_cant.setText("" + product.getCant());
        holder.btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    int cant = product.getCant();
                    double price = Double.parseDouble(product.getPrice_unit());
                    cant++;
                    product.setCant(cant);
                    product.setPrice_total("" + (price * cant));
                    holder.txt_cant.setText(String.valueOf(cant));
                    holder.txt_price.setText(product.getPrice_total());
                    DetailOrderActivity.txt_total.setText("$ " + DetailOrderActivity.loadTotalPay());

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        holder.btn_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    int cant = product.getCant();
                    double price = Double.parseDouble(product.getPrice_unit());
                    if (cant > 1) {
                        cant--;
                        product.setCant(cant);
                        product.setPrice_total("" + (price * cant));
                        holder.txt_cant.setText(String.valueOf(cant));
                        holder.txt_price.setText(product.getPrice_total());
                        DetailOrderActivity.txt_total.setText("$ " + DetailOrderActivity.loadTotalPay());
                    }

                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        });
        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int new_position = holder.getAdapterPosition();
                ListCategoriesActivity.list_detail.remove(new_position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
                DetailOrderActivity.txt_total.setText("$ " + DetailOrderActivity.loadTotalPay());
                if (ListCategoriesActivity.list_detail.size() == 0) {
                    DetailOrderActivity.liner_btn.setVisibility(View.GONE);
                    DetailOrderActivity.relative_empty.setVisibility(View.VISIBLE);
                }

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

        private TextView txt_name, txt_description, txt_price, txt_cant;
        private ImageView imageView_specialty;
        private ImageButton btn_plus, btn_min, btn_remove;

        private CardView cardView;

        public ListCategoriesHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.item_detail_list_products_name_id);
            txt_description = itemView.findViewById(R.id.item_detail_list_products_description_id);
            txt_price = itemView.findViewById(R.id.item_detail_list_products_sale_price_id);
            txt_cant = itemView.findViewById(R.id.item_detail_show_product_text_cantidad);
            btn_remove = itemView.findViewById(R.id.item_detail_btn_list_products_delete);
            btn_min = itemView.findViewById(R.id.item_detail_show_product_btn_min);
            btn_plus = itemView.findViewById(R.id.item_detail_show_product_btn_add);
            imageView_specialty = (ImageView) itemView.findViewById(R.id.item_detail_list_products_image);
            cardView = itemView.findViewById(R.id.item_detail_list_products_cardview_id);
        }


    }
}
