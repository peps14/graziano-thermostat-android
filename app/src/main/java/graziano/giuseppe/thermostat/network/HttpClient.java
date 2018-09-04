package graziano.giuseppe.thermostat.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import graziano.giuseppe.thermostat.MainActivity;
import graziano.giuseppe.thermostat.R;
import graziano.giuseppe.thermostat.data.model.Thermostat;
import graziano.giuseppe.thermostat.network.request.BasicAuthRequest;
import graziano.giuseppe.thermostat.network.request.MeasurementListRequest;
import graziano.giuseppe.thermostat.network.request.SensorStatsRequest;
import graziano.giuseppe.thermostat.network.request.ThermostatRequest;
import graziano.giuseppe.thermostat.network.request.UserRequest;


public class HttpClient {

    protected static final String PROTOCOL_CHARSET = "utf-8";

    private static RequestQueue queue;
    private static Gson gson;

    private static String URL_SERVER;

    private static String PLACEHOLDER_ERROR = "${error}";

    private static String PLACEHOLDER_THERMOSTAT_ID = "${thermostat_id}";
    private static String PLACEHOLDER_SENSOR_ID = "${sensor_id}";
    private static String PLACEHOLDER_DATE_FROM = "${date_from}";
    private static String PLACEHOLDER_DATE_TO = "${date_to}";


    private static String ERROR_PARSING_MESSAGE;
    private static String ERROR_BAD_RESPONSE;

    private static String ENDPOINT_TEST_LOGIN = "/thermostat/test/login";
    private static String ENDPOINT_GET_USER = "/thermostat/user";
    private static String ENDPOINT_PUT_THERMOSTAT = "/thermostat/thermostat?thermostat_id=${thermostat_id}";
    private static String ENDPOINT_PUT_USER_THERMOSTAT_SELECT = "/thermostat/user/thermostat/select?thermostat_id=${thermostat_id}";
    private static String ENDPOINT_GET_MEASUREMENTS_LAST = "/thermostat/measurements/last?thermostat_id=${thermostat_id}";
    private static String ENDPOINT_GET_SENSOR_STATS = "/thermostat/measurements/stats?thermostat_id=${thermostat_id}&sensor_id=${sensor_id}";
    private static String ENDPOINT_GET_SENSOR_STATS_FROM = ENDPOINT_GET_SENSOR_STATS + "&date_from=${date_from}";
    private static String ENDPOINT_GET_SENSOR_STATS_TO = ENDPOINT_GET_SENSOR_STATS + "&date_to=${date_to}";
    private static String ENDPOINT_GET_SENSOR_STATS_FROM_TO = ENDPOINT_GET_SENSOR_STATS + "&date_from=${date_from}&date_to=${date_to}";


    private static String ENDPOINT_POST_FAMILY = "/family";

    public static void initialize(Context context){
        queue = Volley.newRequestQueue(context);
        gson = new Gson();
        URL_SERVER = context.getString(R.string.server_url);
        ERROR_PARSING_MESSAGE = context.getString(R.string.http_error_parse);
        ERROR_BAD_RESPONSE = context.getString(R.string.http_error_bad_response);
    }


    public static void getUserLogin(Response.Listener<BasicAuthRequest> listener, Response.ErrorListener errorListener){

        String url = URL_SERVER + ENDPOINT_GET_USER;
        UserRequest login = new UserRequest(Request.Method.GET, url, null, listener, errorListener ,MainActivity.user.getUsername(), MainActivity.user.getPassword());
        queue.add(login);
    }

    public static void putThermostat(Thermostat thermostat, Response.Listener<BasicAuthRequest> listener, Response.ErrorListener errorListener){
        String url = URL_SERVER + ENDPOINT_PUT_THERMOSTAT;
        url = url.replace(PLACEHOLDER_THERMOSTAT_ID, String.valueOf(thermostat.getId()));
        ThermostatRequest login = new ThermostatRequest(Request.Method.PUT, url, gson.toJson(thermostat), listener, errorListener ,MainActivity.user.getUsername(), MainActivity.user.getPassword());
        queue.add(login);
    }

    public static void putUserThermostat(Long id, Response.Listener<BasicAuthRequest> listener, Response.ErrorListener errorListener){
        String url = URL_SERVER + ENDPOINT_PUT_USER_THERMOSTAT_SELECT;
        url = url.replace(PLACEHOLDER_THERMOSTAT_ID, String.valueOf(id));
        UserRequest login = new UserRequest(Request.Method.PUT, url, null, listener, errorListener ,MainActivity.user.getUsername(), MainActivity.user.getPassword());
        queue.add(login);
    }

    public static void getMeasurementsLast(Long id, Response.Listener<BasicAuthRequest> listener, Response.ErrorListener errorListener){
        String url = URL_SERVER + ENDPOINT_GET_MEASUREMENTS_LAST;
        url = url.replace(PLACEHOLDER_THERMOSTAT_ID, String.valueOf(id));
        MeasurementListRequest measurementListRequest = new MeasurementListRequest(Request.Method.GET, url, null, listener, errorListener ,MainActivity.user.getUsername(), MainActivity.user.getPassword());
        queue.add(measurementListRequest);
    }

    public static void getSensorStats(Long id, Long sensorId, Response.Listener<BasicAuthRequest> listener, Response.ErrorListener errorListener){
        String url = URL_SERVER + ENDPOINT_GET_SENSOR_STATS;
        url = url.replace(PLACEHOLDER_THERMOSTAT_ID, String.valueOf(id));
        url = url.replace(PLACEHOLDER_SENSOR_ID, String.valueOf(sensorId));
        SensorStatsRequest sensorStatsRequest = new SensorStatsRequest(Request.Method.GET, url, null, listener, errorListener ,MainActivity.user.getUsername(), MainActivity.user.getPassword());
        queue.add(sensorStatsRequest);
    }

    public static void getSensorStatsFrom(Long id, Long sensorId, Long dateTimestampFrom, Response.Listener<BasicAuthRequest> listener, Response.ErrorListener errorListener){
        String url = URL_SERVER + ENDPOINT_GET_SENSOR_STATS_FROM;
        url = url.replace(PLACEHOLDER_THERMOSTAT_ID, String.valueOf(id));
        url = url.replace(PLACEHOLDER_SENSOR_ID, String.valueOf(sensorId));
        url = url.replace(PLACEHOLDER_DATE_FROM, String.valueOf(dateTimestampFrom));
        SensorStatsRequest sensorStatsRequest = new SensorStatsRequest(Request.Method.GET, url, null, listener, errorListener ,MainActivity.user.getUsername(), MainActivity.user.getPassword());
        queue.add(sensorStatsRequest);
    }

    public static void getSensorStatsTo(Long id, Long sensorId, Long dateTimestampTo, Response.Listener<BasicAuthRequest> listener, Response.ErrorListener errorListener){
        String url = URL_SERVER + ENDPOINT_GET_SENSOR_STATS_TO;
        url = url.replace(PLACEHOLDER_THERMOSTAT_ID, String.valueOf(id));
        url = url.replace(PLACEHOLDER_SENSOR_ID, String.valueOf(sensorId));
        url = url.replace(PLACEHOLDER_DATE_TO, String.valueOf(dateTimestampTo));
        SensorStatsRequest sensorStatsRequest = new SensorStatsRequest(Request.Method.GET, url, null, listener, errorListener ,MainActivity.user.getUsername(), MainActivity.user.getPassword());
        queue.add(sensorStatsRequest);
    }


    public static void getSensorStatsFromTo(Long id, Long sensorId, Long dateTimestampFrom, Long dateTimestampTo, Response.Listener<BasicAuthRequest> listener, Response.ErrorListener errorListener){
        String url = URL_SERVER + ENDPOINT_GET_SENSOR_STATS_FROM_TO;
        url = url.replace(PLACEHOLDER_THERMOSTAT_ID, String.valueOf(id));
        url = url.replace(PLACEHOLDER_SENSOR_ID, String.valueOf(sensorId));
        url = url.replace(PLACEHOLDER_DATE_FROM, String.valueOf(dateTimestampFrom));
        url = url.replace(PLACEHOLDER_DATE_TO, String.valueOf(dateTimestampTo));
        SensorStatsRequest sensorStatsRequest = new SensorStatsRequest(Request.Method.GET, url, null, listener, errorListener ,MainActivity.user.getUsername(), MainActivity.user.getPassword());
        queue.add(sensorStatsRequest);
    }



 /*  public static void getFamilyByName(String family_name, String password, Response.Listener<Family> listener, Response.ErrorListener errorListener){

        String url = URL_SERVER + ENDPOINT_GET_FAMILY;
        url = url.replace(PLACEHOLDER_FAMILY_NAME, family_name);
        url = url.replace(PLACEHOLDER_PASSWORD, password);

        FamilyRequest familyRequest = new FamilyRequest(Request.Method.GET, url, null, listener, errorListener ,USERNAME, PASSWORD);
        queue.add(familyRequest);
    }

    public static void createFamily(Family family, Response.Listener<Family> listener, Response.ErrorListener errorListener){

        String url = URL_SERVER + ENDPOINT_POST_FAMILY;
        FamilyRequest familyRequest = new FamilyRequest(Request.Method.POST, url, gson.toJson(family), listener, errorListener ,USERNAME, PASSWORD);
        queue.add(familyRequest);
    }
*/

    public static String getErrorMessage(VolleyError error){

        if(error != null && error.networkResponse != null && error.networkResponse.statusCode == 400) {

            String jsonString = null;
            try {
                jsonString = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers, PROTOCOL_CHARSET));
                JSONObject jsonObject = new JSONObject(jsonString);

                if(!jsonObject.isNull("error")){
                    return jsonObject.getString("error");
                }

            } catch (Exception e) {
                return ERROR_PARSING_MESSAGE;
            }
        }
        else if(error != null && error.networkResponse != null) {

            try {
                String jsonString = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers, PROTOCOL_CHARSET));
                return jsonString;
            } catch (UnsupportedEncodingException e) {
                return ERROR_PARSING_MESSAGE;
            }

        }

        return   ERROR_BAD_RESPONSE.replace(PLACEHOLDER_ERROR, error.toString());
    }

}