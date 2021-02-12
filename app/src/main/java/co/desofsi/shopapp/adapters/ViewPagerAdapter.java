package co.desofsi.shopapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import co.desofsi.shopapp.R;


public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    public ViewPagerAdapter(Context context){
        this.context = context;
    }
    private int images[] = {
            R.drawable.vp_product1,
            R.drawable.vp_venta2,
            R.drawable.vp_ubicacion3,
    };
    private String titles[] = {
            "Venta de productos lácteos ",
            "Creación de productos",
            "Se ubica"
    };
    private String descriptions[] = {
            "En este aplicativo móvil se visualizará y se venderá la producción que realiza en la asociación de productores de leche “Simón Rodríguez”",
            "Producto de calidad e higiénicamente empacado y con todas las normas de bioseguridad",
            "Dirección: Cantón: Cotopaxi Parroquia: Mulaló, Barrio: San Agustín del callo"
    };

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view ==(LinearLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.view_pager,container,false);
        ImageView imageView = view.findViewById(R.id.img_view_pager);
        TextView title = view.findViewById(R.id.text_title_view_pager);
        TextView description = view.findViewById(R.id.text_description_view_pager);
        imageView.setImageResource(images[position]);
        title.setText(titles[position]);
        description.setText(descriptions[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
