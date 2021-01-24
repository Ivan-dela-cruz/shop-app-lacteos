package co.desofsi.tiendavirtual.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import co.desofsi.tiendavirtual.R;
import co.desofsi.tiendavirtual.routes.Routes;


public class ViewHolerCategory extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView txt_name;
    private ImageView imageView_specialty;
    private CardView cardView;

    RecyclerCategoriesAdapter.OnItemHomeClickListener mylistener;


    public ViewHolerCategory(View itemView, final RecyclerCategoriesAdapter.OnItemHomeClickListener listener) {
        super(itemView);
        txt_name = itemView.findViewById(R.id.item_list_categories_name_id);
        imageView_specialty = (ImageView) itemView.findViewById(R.id.item_list_categories_image);
        cardView = itemView.findViewById(R.id.item_list_categories_cardview_id);
        mylistener = listener;

        itemView.setOnClickListener(this);
    }


    public void setName(String valor) {
        txt_name.setText(valor);
    }
    public void setImageView_specialty(String valor) {
        Picasso.get().load(Routes.URL + valor).into(imageView_specialty);

    }



    @Override
    public void onClick(View v) {
        mylistener.onItemHomeClick(getAdapterPosition());
    }
}