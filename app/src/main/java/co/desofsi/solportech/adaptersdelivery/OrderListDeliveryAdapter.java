package co.desofsi.solportech.adaptersdelivery;

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
import co.desofsi.solportech.deliveryactivities.DeliveryDetailActivity;
import co.desofsi.solportech.models.DateClass;
import co.desofsi.solportech.models.OrderRequestDelivery;

public class OrderListDeliveryAdapter extends RecyclerView.Adapter<OrderListDeliveryAdapter.TypeCompanyHolder> {

    private Context context;
    private ArrayList<OrderRequestDelivery> list;


    public OrderListDeliveryAdapter(Context context, ArrayList<OrderRequestDelivery> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public TypeCompanyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_delivery_fragment, parent, false);
        return new TypeCompanyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeCompanyHolder holder, final int position) {

        final OrderRequestDelivery order = list.get(position);

        DateClass dateClass = new DateClass();

        holder.txt_name.setText(order.getName_company());
        holder.txt_address.setText(order.getCompany_address());
        holder.txt_customer.setText("Para: " + order.getName_customer());
        holder.text_date.setText(dateClass.time(order.getDate()) + dateClass.dateFormatHuman(order.getDate()));
        holder.text_total.setText(" $ " + order.getTotal());
        holder.text_status.setText(order.getStatus_request());

        switch (order.getStatus()){
            case "anulado":
                holder.imageView.setImageResource(R.drawable.ic_baseline_close_24);
                break;
            case "pendiente":
                holder.imageView.setImageResource(R.drawable.ic_baseline_store_mall_directory_24);
                break;
            case "confirmado":
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
                Intent intent = new Intent(((HomeActivity) context), DeliveryDetailActivity.class);
                intent.putExtra("order", order);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class TypeCompanyHolder extends RecyclerView.ViewHolder {

        private TextView txt_name, text_status, text_total, text_date, txt_address, txt_customer;

        private ImageView imageView;
        private Button btn_options;
        private CardView cardView;

        public TypeCompanyHolder(@NonNull View itemView) {
            super(itemView);

            txt_name = itemView.findViewById(R.id.delivery_item_txt_name_customer);
            txt_address = itemView.findViewById(R.id.delivery_item_txt_address);
            txt_customer = itemView.findViewById(R.id.delivery_item_txt_customer);
            text_status = itemView.findViewById(R.id.delivery_item_txt_status);
            text_total = itemView.findViewById(R.id.delivery_item_txt_total);
            text_date = itemView.findViewById(R.id.delivery_item_txt_date);

            imageView = (ImageView) itemView.findViewById(R.id.delivery_item_img);
            btn_options = itemView.findViewById(R.id.delivery_item_button_detail);
            cardView = itemView.findViewById(R.id.delivery_item_card_view);


        }


    }
}
