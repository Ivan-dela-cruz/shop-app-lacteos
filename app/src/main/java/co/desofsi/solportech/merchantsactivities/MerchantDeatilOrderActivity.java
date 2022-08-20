package co.desofsi.solportech.merchantsactivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.desofsi.solportech.R;
import co.desofsi.solportech.adapters.ReviewListProductstAdapter;
import co.desofsi.solportech.models.Company;
import co.desofsi.solportech.routes.Routes;
import co.desofsi.solportech.models.DateClass;
import co.desofsi.solportech.models.DetailOrder;
import co.desofsi.solportech.models.Order;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MerchantDeatilOrderActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST = 103;
    private ArrayList<DetailOrder> lis_products;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;

    private ImageButton btn_home, btn_download;
    private TextView txt_order_number, txt_order_data, txt_order_total;
    private Button btnRegisterPayment, btn_deactivate;
    private ScrollView scrollView;
    private DateClass dateClass;
    private Order order;
    private Company company;
    ImageView camera_preview;
    Button btnSendPayment, btnCancelPayment;

    private static final int PERMISSION_STORAGE_CODE = 1000;
    int method = 0;
    String camera_file_path;

    private static final String CARPETA_PRINCIPAL = "DCIM/";//directorio principal
    private static final String CARPETA_IMAGEN = "APPSHOP";//carpeta donde se guardan las fotos
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;//ruta carpeta de directorios

    private String path;//almacena la ruta de la imagen
    File fileImage;
    Bitmap bitmap;
    private static final int COD_PHOTO = 20;
    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_deatil_order);
        try {
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //  window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
                //getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            order = new Order();
            order = (Order) getIntent().getExtras().getSerializable("order");
            company = order.getCompany();
            init();
            eventsButtons();
            loadReviewOrder();
            getOrdersDetail();
            if (order.getStatus().equals("anulado")) {
                btnRegisterPayment.setVisibility(View.GONE);
                btn_deactivate.setVisibility(View.GONE);
            }
            if (order.getStatus().equals("pendiente")) {
                btn_deactivate.setVisibility(View.VISIBLE);
            } else {
                btn_deactivate.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Toast.makeText(MerchantDeatilOrderActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();

        }
    }

    public void init() {
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        dateClass = new DateClass();
        txt_order_number = findViewById(R.id.merchant_detail_order_txt_order_number);

        txt_order_data = findViewById(R.id.merchant_detail_order_txt_date);

        txt_order_total = findViewById(R.id.merchant_detail_order_txt_total);
        btn_home = findViewById(R.id.merchant_detail_order_btn_back);
        btn_download = findViewById(R.id.merchant_detail_order_btn_download);
        btnRegisterPayment = findViewById(R.id.btnRegisterPayment);
        btn_deactivate = findViewById(R.id.merchant_detail_order_btn_deactivate);
        scrollView = findViewById(R.id.merchant_detail_order_scroll);
        recyclerView = findViewById(R.id.merchant_detail_order_recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MerchantDeatilOrderActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        if (!Routes.ORDER_STATUS.equals("Registrado")) {
            btnRegisterPayment.setVisibility(View.GONE);
            Toast.makeText(MerchantDeatilOrderActivity.this, "Pago registrado", Toast.LENGTH_LONG).show();
        }
    }

    public void loadReviewOrder() {

        txt_order_total.setText("$ " + order.getTotal());

        txt_order_data.setText(dateClass.dateFormatHuman(order.getDate()));
        txt_order_number.setText(order.getOrder_number());

        scrollView.pageScroll(View.FOCUS_UP);

    }

    public void eventsButtons() {
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_STORAGE_CODE);

                    } else {
                        startDownLoad();
                    }


                } else {
                    startDownLoad();
                }
            }
        });
        btnRegisterPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                method = 1;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    if (checkPermission(Manifest.permission.CAMERA)) {
                        //filePicker(1);
                        openCamera();
                    } else {
                        requestPermission(Manifest.permission.CAMERA);
                    }
                } else {
                    //  filePicker(1);
                }

                //    Toast.makeText(MerchantDeatilOrderActivity.this, "Registrar Pago", Toast.LENGTH_LONG).show();

            }
        });
        btn_deactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MerchantDeatilOrderActivity.this);
                builder.setMessage("¿Está seguro de anular la orden")
                        .setCancelable(false)
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deactivateOrder();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

    }

    public void deactivateOrder() {
        String url = Routes.ORDER_DEACTIVATE + "/" + order.getId();
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                Intent intent = new Intent(MerchantDeatilOrderActivity.this, CompanyOrdersActivity.class);
                                intent.putExtra("company", company);
                                startActivity(intent);
                            }
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                Map<String, String> map = new HashMap<String, String>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(MerchantDeatilOrderActivity.this);
        requestQueue.add(stringRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void downloadPdf() {
        String url = Routes.URL + order.getUrl_order();
        System.out.println(url);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String tempTitle = order.getOrder_number();
        request.setTitle(tempTitle);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_STORAGE_CODE);

            } else {
                startDownLoad();
            }
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


        } else {
            startDownLoad();
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, tempTitle + ".pdf");
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        request.setMimeType("application/pdf");
        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        downloadManager.enqueue(request);
    }

    public void startDownLoad() {
        String url = Routes.URL + order.getUrl_order();
        String tempTitle = order.getOrder_number();
        System.out.println(url);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        /*request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Descargar");
        request.setDescription("Descargando comprobante de orden");
        request.allowScanningByMediaScanner();
*/
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle("Descargar") //Download Manager Title
                .setDescription("Descargando comprobante de orden")
                .setMimeType("application/pdf")
                .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        tempTitle + ".pdf"
                );

        downloadManager.enqueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Toast.makeText(MerchantDeatilOrderActivity.this, "Permission Successfull", Toast.LENGTH_SHORT).show();
                    // filePicker(method);
                    openCamera();
                    startDownLoad();
                } else {
                    Toast.makeText(MerchantDeatilOrderActivity.this, "Habilite los permisos necesarios", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    private void getOrdersDetail() {
        lis_products = new ArrayList<>();
        String url = Routes.ORDER_DETAIL + "/" + order.getId();
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                JSONArray array = new JSONArray(object.getString("details"));
                                System.out.println("ARRAY \n" + array + "  =>>>");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject type_object = array.getJSONObject(i);
                                    DetailOrder detail = new DetailOrder();
                                    detail.setId(type_object.getInt("id"));
                                    detail.setId_order(order.getId());
                                    detail.setId_product(type_object.getInt("articulo_id"));
                                    detail.setCant(type_object.getInt("cantidad"));
                                    detail.setProduct_name(type_object.getString("articulo"));
                                    detail.setProduct_desc(type_object.getString("descripcion"));
                                    detail.setPrice_unit(type_object.getString("precio_venta"));
                                    detail.setPrice_total(type_object.getString("precio"));
                                    lis_products.add(detail);
                                    System.out.println("lista \n" + type_object + "  =>>>");
                                }

                                ReviewListProductstAdapter orderListAdapter = new ReviewListProductstAdapter(MerchantDeatilOrderActivity.this, lis_products);
                                recyclerView.setAdapter(orderListAdapter);

                            }
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                Map<String, String> map = new HashMap<String, String>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(MerchantDeatilOrderActivity.this);
        requestQueue.add(stringRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case COD_PHOTO:
                MediaScannerConnection.scanFile(MerchantDeatilOrderActivity.this, new String[]{path}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("Path", "" + path);
                            }
                        });

                bitmap = BitmapFactory.decodeFile(path);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
                dialogPaymentPicture(b);

                break;
        }
        bitmap = resizeImage(bitmap, 600, 800);
    }

    private void openCamera() {
        File miFile = new File(Environment.getExternalStorageDirectory(), DIRECTORIO_IMAGEN);
        boolean isCreated = miFile.exists();

        if (isCreated == false) {
            isCreated = miFile.mkdirs();
        }

        if (isCreated == true) {
            Long consecutive = System.currentTimeMillis() / 1000;
            String name = consecutive.toString() + ".jpg";

            path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO_IMAGEN
                    + File.separator + name;//indicamos la ruta de almacenamiento

            fileImage = new File(path);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImage));

            ////
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String authorities = this.getPackageName() + ".provider";
                Uri imageUri = FileProvider.getUriForFile(this, authorities, fileImage);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImage));
            }
            startActivityForResult(intent, COD_PHOTO);

            camera_file_path = fileImage.getPath();

        }
    }

    private Bitmap resizeImage(Bitmap bitmap, float newHeight, float newWidth) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > newHeight || height > newWidth) {
            float scaleWidth = newHeight / width;
            float scaleHeight = newWidth / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        } else {
            return bitmap;
        }
    }

    public void dialogPaymentPicture(Bitmap thumb) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MerchantDeatilOrderActivity.this);
        LayoutInflater inflater = (LayoutInflater) MerchantDeatilOrderActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_prev_payment, null);
        builder.setView(view);
        initDialog(view);
        // bitmap = resizeImage(thumb, 600, 800);
        camera_preview.setImageBitmap(thumb);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        // codeAnimal.setText(code_animal);
        btnSendPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storePayment();
                progress = new ProgressDialog(MerchantDeatilOrderActivity.this);
                progress.setMessage("Enviando...");
                progress.show();
                progress.setCanceledOnTouchOutside(false);
                dialog.dismiss();
            }

        });
        btnCancelPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  listTimeMilking.clear();
                dialog.dismiss();
            }
        });
    }

    public void initDialog(View view) {
        camera_preview = view.findViewById(R.id.camera_preview);
        btnSendPayment = view.findViewById(R.id.btnSendPayment);
        btnCancelPayment = view.findViewById(R.id.btnCancelPayment);
    }

    public void storePayment() {
        UploadTask uploadTask = new UploadTask();
        uploadTask.execute(new String[]{camera_file_path});
    }

    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(MerchantDeatilOrderActivity.this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MerchantDeatilOrderActivity.this, permission)) {
            Toast.makeText(MerchantDeatilOrderActivity.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MerchantDeatilOrderActivity.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        }
    }

    public class UploadTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                System.out.println("RRRRR"+s);
                JSONObject object = new JSONObject(s);
                if (object.getBoolean("success")) {
                    Toast.makeText(MerchantDeatilOrderActivity.this, "¡Pago registrado con exito!", Toast.LENGTH_SHORT).show();
                    btnRegisterPayment.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Toast.makeText(MerchantDeatilOrderActivity.this, "¡Error al registrar pago!", Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }


            progress.hide();
            // progressBar.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {

            File file1 = new File(strings[0]);
            String url = Routes.SEND_PAYMENT;
            String token = sharedPreferences.getString("token", "");

            String order_id = String.valueOf(Routes.ORDER_ID);

            try {
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("files1", file1.getName(), RequestBody.create(MediaType.parse("*/*"), file1))
                        .addFormDataPart("order_id", order_id)
                        .addFormDataPart("submit", "submit")
                        .build();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + token)
                        .post(requestBody)
                        .build();

                OkHttpClient okHttpClient = new OkHttpClient();
                //now progressbar not showing properly let's fixed it
                okhttp3.Response response = okHttpClient.newCall(request).execute();
                if (response != null && response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}