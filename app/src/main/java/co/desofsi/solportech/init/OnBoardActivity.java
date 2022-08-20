package co.desofsi.solportech.init;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import co.desofsi.solportech.R;
import co.desofsi.solportech.adapters.ViewPagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnBoardActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private Button btn_letf, btn_right;
    private ViewPagerAdapter viewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);
        init();
    }

    public void init() {
        viewPager = findViewById(R.id.view_pager);
        btn_letf = findViewById(R.id.btn_left);
        btn_right = findViewById(R.id.btn_right);
        dotsLayout = findViewById(R.id.dotsLayout);
        viewPagerAdapter = new ViewPagerAdapter(this);
        addDots(0);

        viewPager.addOnPageChangeListener(listener);
        viewPager.setAdapter(viewPagerAdapter);

        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_right.getText().toString().equals("Siguiente")) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                } else {
                    startActivity(new Intent(OnBoardActivity.this, AuthActivity.class));
                    finish();
                }
            }
        });
        btn_letf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+2);
            }
        });

    }

    public void addDots(int position) {
        dotsLayout.removeAllViews();
        dots = new TextView[3];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextColor(getResources().getColor(R.color.colorLightGrey));
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorGrey));
        }

    }

    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            if (position == 0) {
                btn_letf.setVisibility(View.VISIBLE);
                btn_letf.setEnabled(true);
                btn_right.setText("Siguiente");
            } else if (position == 1) {
                btn_letf.setVisibility(View.GONE);
                btn_letf.setEnabled(false);
                btn_right.setText("Siguiente");
            } else {
                btn_letf.setVisibility(View.GONE);
                btn_letf.setEnabled(false);
                btn_right.setText("Fin");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
