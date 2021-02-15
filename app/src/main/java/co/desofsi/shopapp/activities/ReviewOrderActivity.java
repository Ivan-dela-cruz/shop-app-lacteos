package co.desofsi.shopapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import co.desofsi.shopapp.R;
import co.desofsi.shopapp.adapters.ReviewListProductstAdapter;
import co.desofsi.shopapp.models.DateClass;
import co.desofsi.shopapp.models.DetailOrder;

public class ReviewOrderActivity extends AppCompatActivity {

    private ImageButton btn_home;
    private TextView txt_order_data, txt_order_total;
    private RecyclerView review_recycler;
    private Button btn_reset;
    private ScrollView scrollView;
    private DateClass dateClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_order);
        try {
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //  window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.md_blue_800));
                //getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        init();
        eventsButtons();
        loadReviewOrder();

    }

    public void init() {
        dateClass =  new DateClass();
        txt_order_data = findViewById(R.id.review_order_txt_date);
        txt_order_total = findViewById(R.id.review_order_txt_total);
        btn_home = findViewById(R.id.review_order_btn_back);
        btn_reset = findViewById(R.id.review_order_btn_home);
        scrollView = findViewById(R.id.review_order_scroll);
        review_recycler = findViewById(R.id.review_order_recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ReviewOrderActivity.this, LinearLayoutManager.VERTICAL, false);
        review_recycler.setLayoutManager(linearLayoutManager);

    }

    public void eventsButtons() {
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReviewOrderActivity.this, HomeActivity.class));
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReviewOrderActivity.this, HomeActivity.class));
            }
        });
    }

    public void loadReviewOrder() {
        ReviewListProductstAdapter reviewListProductstAdapter = new ReviewListProductstAdapter(ReviewOrderActivity.this, HomeActivity.list_detail);
        review_recycler.setAdapter(reviewListProductstAdapter);
        double total = 0;
        for (DetailOrder detailOrder : HomeActivity.list_detail) {
            total += Double.parseDouble(detailOrder.getPrice_total());
        }
        txt_order_total.setText("$ " + total);

        txt_order_data.setText(dateClass.dateFormatHuman(HomeActivity.order.getDate_format()));
        scrollView.pageScroll(View.FOCUS_UP);

    }

    @Override
    public void onBackPressed() {
        startActivity( new Intent(ReviewOrderActivity.this,HomeActivity.class));
        HomeActivity.list_detail.clear();
        finish();
    }
}