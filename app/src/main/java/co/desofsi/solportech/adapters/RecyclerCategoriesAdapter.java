package co.desofsi.solportech.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import co.desofsi.solportech.R;

import co.desofsi.solportech.models.Category;


public class RecyclerCategoriesAdapter extends RecyclerView.Adapter<ViewHolerCategory> {
    private ArrayList<Category> listDatos;
    private OnItemHomeClickListener myListener;



    public interface OnItemHomeClickListener {
        void onItemHomeClick(int position);
    }

    public void setOnItemClickListener(OnItemHomeClickListener listener) {
        myListener = listener;
    }

    public RecyclerCategoriesAdapter(ArrayList<Category> listDatos, OnItemHomeClickListener listener) {
        this.listDatos = listDatos;
        this.myListener = listener;
    }

    @NonNull
    @Override
    public ViewHolerCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_list_categories_recycler, parent, false);
        return new ViewHolerCategory(view, myListener);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolerCategory holder, int position) {
        final Category category = listDatos.get(position);
        holder.setName(category.getName());
        holder.setImageView_specialty(category.getUrl_image());
    }

    @Override
    public int getItemCount() {
        return listDatos.size();
    }


}

