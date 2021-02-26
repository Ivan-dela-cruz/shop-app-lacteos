package co.desofsi.shopapp.routes;

public class Routes {


    public static final String URL_MAP  = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    public static final String DESTINATION = "&destination=";
    public static final String  API_KEY = "&key=AIzaSyDibwJl271dqQbcVduSLr56yCct_RMX8_g";

    public static final String URL = "https://www.productoslacteossimonr.com/";
    public static final String HOME = URL + "api";
    public static final String LOGIN = HOME + "/login";
    public static final String LOGOUT = HOME + "/logout";
    public static final String GET_PROFILE = HOME + "/get-profile";
    public static final String UPDATE_PROFILE = HOME + "/save-profile-user";
    public static final String UPDATE_PASSWORD = HOME + "/change-password";
    public static final String REGISTER = HOME + "/register";
    public static final String SAVE_PROFILE = HOME + "/save-profile-user";
    public static final String TYPE_COMPANES = HOME + "/api-companies-type";
    public static final String COMPANIES = HOME + "/api-companies";
    public static final String CATEGORIES = HOME + "/categorias";
    public static final String PRODUCTS = HOME + "/productos";
    public static final String SEND_ORDER = HOME + "/store-shop";
    public static final String ORDERS = HOME + "/orders";
    public static final String ORDER_DETAIL = HOME + "/detalle";

    //RUTAS PARA LAS ORDENES



    public static final String ORDER_DOWNLOAD = HOME + "/api-download-order";
    public static final String ORDER_DEACTIVATE = HOME + "/api-deactivate-order";

    //RUTA PARA LOS REPARTIDORES
    public static final String UPDATED_POSITION = HOME + "/api-update-position";
    public static final String DELIVERYMAN = HOME + "/api-deliveriman";
    public static final String SEND_REQUEST_DELIVERY = HOME + "/api-send-request-delivery";
    public static final String GET_REQUEST_DELIVERY = HOME + "/api-get-request-delivery";
    public static final String GET_REQUEST_DELIVERY_CUSTOMER = HOME + "/api-get-request-delivery-customer";
    public static final String CONFIRM_DELIVERY = HOME + "/api-request-delivery-confirm";
    public static final String DECLINE_DELIVERY = HOME + "/api-request-delivery-decline";
    public static final String COMPLETE_DELIVERY = HOME + "/api-request-delivery-complete";

    //RUTA PAR LAS EMPRESA
    public static final String MY_COMPANIES = HOME + "/api-get-companies";
    public static final String ORDERS_COMPANY = HOME + "/api-orders-merchant";



    //TIMER  REFRESH MAP
    public static final int TIMER_MAP = 30000;
    public static int ORDER_ID = 0;

    //CODE
    public static final String SLIDERS = HOME + "/sliders";
    public static final String SEND_PAYMENT = HOME + "/send-payment";
    public static String ORDER_STATUS = "";
}
