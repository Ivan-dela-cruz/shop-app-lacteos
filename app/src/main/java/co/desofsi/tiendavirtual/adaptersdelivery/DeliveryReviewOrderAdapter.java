package co.desofsi.tiendavirtual.adaptersdelivery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import co.desofsi.tiendavirtual.R;
import co.desofsi.tiendavirtual.models.DetailOrder;

public class DeliveryReviewOrderAdapter extends RecyclerView.Adapter<DeliveryReviewOrderAdapter.ListCategoriesHolder> {

    private Context context;
    private ArrayList<DetailOrder> list;


    public DeliveryReviewOrderAdapter(Context context, ArrayList<DetailOrder> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public ListCategoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_order_list_products_recycler, parent, false);
        return new ListCategoriesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListCategoriesHolder holder, final int position) {

        final DetailOrder product = list.get(position);

        holder.txt_name.setText(product.getProduct_name());
        holder.txt_description.setText(product.getProduct_desc());
        holder.txt_price_total.setText("$ " + product.getPrice_total());
        holder.txt_price_unit.setText("$ "+product.getPrice_unit());
        holder.txt_cant.setText("" + product.getCant());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ListCategoriesHolder extends RecyclerView.ViewHolder {

        private TextView txt_name, txt_description, txt_price_total,txt_price_unit, txt_cant;


        public ListCategoriesHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.item_review_order_list_products_name_id);
            txt_description = itemView.findViewById(R.id.item_revie_order_list_products_description_id);
            txt_price_unit = itemView.findViewById(R.id.item_revie_order_list_products_price_unit_id);
            txt_price_total = itemView.findViewById(R.id.item_revie_order_list_products_price_total_id);
            txt_cant = itemView.findViewById(R.id.item_review_order_list_products_text_cant);

        }


    }
}
