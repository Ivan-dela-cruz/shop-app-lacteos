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
            R.drawable.delivery,
            R.drawable.catalogo,
            R.drawable.shop_women,
    };
    private String titles[] = {
            "Entregas a Domicilio",
            "Incrementas tu ventas",
            "Realiza tus pedidos"
    };
    private String descriptions[] = {
            "Recibe tus productos en la comodidad de tu hogar",
            "Promociona tus productos o servicios en la ciudad",
            "Pide tus productos a cuelquier tienda o restaurante de la ciudad"
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
