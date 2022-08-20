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
import co.desofsi.solportech.activities.ListComapiesActivity;
import co.desofsi.solportech.routes.Routes;
import co.desofsi.solportech.models.TypeCompany;

public class TypeCompanyAdapter extends RecyclerView.Adapter<TypeCompanyAdapter.TypeCompanyHolder> {

    private Context context;
    private ArrayList<TypeCompany> list;


    public TypeCompanyAdapter(Context context, ArrayList<TypeCompany> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public TypeCompanyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment__recycler_specialty, parent, false);
        return new TypeCompanyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeCompanyHolder holder, final int position) {

        final TypeCompany company = list.get(position);
        // Picasso.get().load(Constant.URL+"img/users/"+specialty.getDoctor().getUrl_image()).into(holder.image_doctor);
        System.out.println(Routes.URL + company.getUrl_image());
        Picasso.get().load(Routes.URL + company.getUrl_image()).into(holder.imageView_specialty);

        holder.txt_name_specialty.setText(company.getName());
        holder.btn_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =  new Intent(((HomeActivity)context), ListComapiesActivity.class);
                intent.putExtra("type_company_selected",company);
                intent.putExtra("position",position);
                context.startActivity(intent);

            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =  new Intent(((HomeActivity)context), ListComapiesActivity.class);
                intent.putExtra("type_company_selected",company);
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class TypeCompanyHolder extends RecyclerView.ViewHolder {

        private TextView txt_name_specialty, text_status_specialty;

        private ImageView imageView_specialty;
        private ImageButton btn_options;
        private CardView cardView;

        public TypeCompanyHolder(@NonNull View itemView) {
            super(itemView);

            txt_name_specialty = itemView.findViewById(R.id.recycler_specialty_name);
            text_status_specialty = itemView.findViewById(R.id.recycler_specialty_status);

            imageView_specialty = (ImageView) itemView.findViewById(R.id.recycler_specialty_image);
            btn_options = itemView.findViewById(R.id.recycler_btn_options);
            cardView = itemView.findViewById(R.id.recycler_card_view);



        }


    }
}
