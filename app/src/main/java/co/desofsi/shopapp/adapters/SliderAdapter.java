package co.desofsi.shopapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import co.desofsi.shopapp.models.Slider;
import co.desofsi.shopapp.R;
import co.desofsi.shopapp.routes.Routes;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderHolder>{

    private Context context;
    private ArrayList<Slider> list;

    public SliderAdapter(Context context, ArrayList<Slider> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SliderAdapter.SliderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_slider,parent,false);
        return new SliderAdapter.SliderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderAdapter.SliderHolder holder, final int position) {

        final Slider slider = list.get(position);

        // Picasso.get().load(Constant.URL+"img/users/"+specialty.getDoctor().getUrl_image()).into(holder.image_doctor);

        System.out.println(slider.getUrlImage());
        Picasso.get().load(slider.getUrlImage()).into(holder.imageSlider); //descomentar en produccion
        //Picasso.get().load(estate.getUrl_image()).into(holder.imageSlider);
        // Picasso.get().load("https://i.imgur.com/tGbaZCY.jpg").into(holder.imageSlider);
      //  holder.txt_name_estate.setText(estate.getName());
       // holder.txt_addres_estate.setText(estate.getAddress());

        //   holder.cardView.setCardBackgroundColor(Color.parseColor(String.valueOf(R.color.colorRed)));
        holder.cardViewSlider.setRadius(40);

      /*  holder.cardViewSlider.setOnClickListener(new View.OnClickListener() {
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


    class SliderHolder extends RecyclerView.ViewHolder{

       // private TextView txt_name_estate,txt_addres_estate;
        private ImageView imageSlider;
        private CardView cardViewSlider;


        public SliderHolder(@NonNull View itemView) {
            super(itemView);
           // txt_name_estate = itemView.findViewById(R.id.recycler_estate_name);
           // txt_addres_estate= itemView.findViewById(R.id.recycler_estate_address);
            imageSlider = itemView.findViewById(R.id.imageSlider);
            cardViewSlider = itemView.findViewById(R.id.cardViewSlider);
        }
    }

}