package co.desofsi.solportech.adaptersmerchant;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import co.desofsi.solportech.R;
import co.desofsi.solportech.activities.HomeActivity;
import co.desofsi.solportech.merchantsactivities.MerchantDeatilOrderActivity;
import co.desofsi.solportech.models.DateClass;
import co.desofsi.solportech.models.Order;
import co.desofsi.solportech.routes.Routes;

public class OrderListMerchantAdapter extends RecyclerView.Adapter<OrderListMerchantAdapter.TypeCompanyHolder> {

    private Context context;
    private ArrayList<Order> list;


    public OrderListMerchantAdapter(Context context, ArrayList<Order> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public TypeCompanyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_merchant_fragment, parent, false);
        return new TypeCompanyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeCompanyHolder holder, final int position) {

        final Order order = list.get(position);

        DateClass dateClass = new DateClass();

        holder.txt_name.setText(order.getName_customer());
        holder.txt_address.setText("Orden #"+order.getOrder_number());
        holder.text_date.setText(dateClass.time(order.getDate())  + dateClass.dateFormatHuman(order.getDate()));
        holder.text_total.setText(" $ " + order.getTotal());
        holder.text_status.setText(order.getStatus());

        switch (order.getStatus()){
            case "anulado":
                holder.imageView.setImageResource(R.drawable.ic_baseline_close_24);
            break;
            case "pendiente":
                holder.imageView.setImageResource(R.drawable.ic_baseline_store_mall_directory_24);
                break;
            case "Confirmado":
                holder.imageView.setImageResource(R.drawable.ic_baseline_confirmation_number_24);
                break;
            case "entregado":
                holder.imageView.setImageResource(R.drawable.ic_baseline_check_circle_24);
                break;
            case "no entregado":
                holder.imageView.setImageResource(R.drawable.ic_baseline_local_taxi_24);
                break;
        }
        holder.btn_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Routes.ORDER_ID = order.getId();
                Routes.ORDER_STATUS = order.getStatus();
                Intent intent = new Intent(((HomeActivity) context), MerchantDeatilOrderActivity.class);
                intent.putExtra("order", order);
                intent.putExtra("position", position);
                context.startActivity(intent);
             //   Toast.makeText(context,""+Routes.ORDER_ID, Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class TypeCompanyHolder extends RecyclerView.ViewHolder {

        private TextView txt_name, text_status, text_total, text_date,txt_address;

        private ImageView imageView;
        private Button btn_options;
        private CardView cardView;

        public TypeCompanyHolder(@NonNull View itemView) {
            super(itemView);

            txt_name = itemView.findViewById(R.id.merchant_item_txt_name_customer);
            txt_address = itemView.findViewById(R.id.merchant_item_txt_address);
            text_status = itemView.findViewById(R.id.merchant_item_txt_status);
            text_total = itemView.findViewById(R.id.merchant_item_txt_total);
            text_date = itemView.findViewById(R.id.merchant_item_txt_date);

            imageView = (ImageView) itemView.findViewById(R.id.merchant_item_img);
            btn_options = itemView.findViewById(R.id.merchant_item_button_detail);
            cardView = itemView.findViewById(R.id.merchant_item_card_view);


        }


    }
}