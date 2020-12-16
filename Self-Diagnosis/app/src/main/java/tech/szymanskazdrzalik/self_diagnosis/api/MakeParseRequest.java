package tech.szymanskazdrzalik.self_diagnosis.api;

import android.content.Context;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import tech.szymanskazdrzalik.self_diagnosis.db.User;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

public class MakeParseRequest {

    private final ApiClass apiClass;
    private final ApiRequestQueue apiRequestQueue;
    private final String url;
    private Context context;
    private final Response.Listener<JSONObject> listener = response -> {
        try {
            JSONArray jsonArrayFromResponse = response.getJSONArray("mentions");
            JSONArray jsonArrayToRequest = new JSONArray();
            for (int i = 0; i < jsonArrayFromResponse.length(); i++) {
                JSONObject jsonObject = jsonArrayFromResponse.getJSONObject(i);
                if (jsonObject.getString("type").equals("symptom")) {
                    JSONObject clearJsonObject = new JSONObject();
                    clearJsonObject.put("id", jsonObject.getString("id"));
                    clearJsonObject.put("choice_id", jsonObject.getString("choice_id"));
                    jsonArrayToRequest.put(clearJsonObject);
                }
            }
            new MakeDiagnoseRequest(context, jsonArrayToRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    public MakeParseRequest(Context context, String text) {
        this.apiClass = ApiClass.getInstance(context);
        this.apiRequestQueue = ApiRequestQueue.getInstance(context);
        this.url = this.apiClass.getUrl() + "/parse";
        this.context = context;

        GlobalVariables globalVariables = GlobalVariables.getInstance();
        if (!globalVariables.getCurrentUser().isPresent()) {
            // TODO: 16.12.2020 Add user not found exception
            System.out.println("User not found");
        }
        User user = globalVariables.getCurrentUser().get();

        Map<String, String> headers = RequestUtil.getDefaultHeaders(context);

        JSONObject jsonObject = new JSONObject();
        try {
            RequestUtil.addUserDataToJsonObject(jsonObject);
            jsonObject.put("text", text);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.apiRequestQueue.addToRequestQueue(new JSONObjectRequestWithHeaders(1, this.url, headers, jsonObject, this.listener, System.out::println));
    }


}