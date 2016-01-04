package richardcook.perkaapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.RandomAccessFile;

public class SendApplication extends AsyncTask<String, String, String> {
    RequestQueue mRequestQueue;
    Context context;

    public SendApplication(Context context){
        //Create Volley request queue so that post request can be made
        this.context = context;
        mRequestQueue =  Volley.newRequestQueue(context);
    }

    @Override
    protected String doInBackground(String... params) {
        String url = (String) context.getResources().getText(R.string.serverUrl);
        JSONObject jsonBody = new JSONObject();
        String resumeBase64 = "";
        final Context context = this.context;

        String firstName = params[0];
        String lastName = params[1];
        String email = params[2];
        String github = params[3];
        String source = params[4];
        String resumePath = params[5];

        publishProgress("Preparing and submitting");

        //Read in PDF selected and encode in base64 string
        try {
            RandomAccessFile resumeFile = new RandomAccessFile(resumePath, "r");
            byte[] resumeByte = new byte[(int)resumeFile.length()];
            resumeFile.read(resumeByte);
            resumeBase64 = Base64.encodeToString(resumeByte, Base64.DEFAULT);
            resumeFile.close();
        } catch (IOException e) {
            publishProgress("Error reading resume file");
            e.printStackTrace();
            return "Error";
        }
        //Make sure resume was read in
        if(resumeBase64.equals("")) {
            publishProgress("Error reading resume file");
            return "Error";
        }

        try {
            jsonBody.put("first_name", firstName);
            jsonBody.put("last_name", lastName);
            jsonBody.put("email", email);
            jsonBody.put("position_id", (String) context.getResources().getText(R.string.position));
            jsonBody.put("explanation", "");
            jsonBody.put("projects", new JSONArray(github.split(",")));
            jsonBody.put("source", source);
            jsonBody.put("resume", resumeBase64);
        } catch (JSONException e) {
            publishProgress("Error creating JSONBody");
            e.printStackTrace();
            return "Error";
        }

        //Volley Post request and response/error handler
        JsonObjectRequest req = new JsonObjectRequest(url,jsonBody, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response", response.toString());
                Toast.makeText(context, "Submitted successfully", Toast.LENGTH_SHORT).show();
                VolleyLog.v("Response", response.toString());
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error sending application", Toast.LENGTH_SHORT).show();
                VolleyLog.e("Error:", error.getMessage());
            }
        });

        mRequestQueue.add(req);
        return "Success";
    }

    //Pop message of progress, runs on UIThread and therefore can use Toast
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Toast.makeText(this.context, values[0], Toast.LENGTH_LONG).show();
    }
}
