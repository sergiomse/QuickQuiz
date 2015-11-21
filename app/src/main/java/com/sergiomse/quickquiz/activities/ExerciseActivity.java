package com.sergiomse.quickquiz.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.sergiomse.quickquiz.R;
import com.sergiomse.quickquiz.model.Exercise;


public class ExerciseActivity extends AppCompatActivity {

    private String folderId;
    private String folderName;

    private Exercise exercise;
    private ProgressDialog progressDialog;
    private Button btnSolve;
    private Button btnNext;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Trng");

        btnNext = (Button) findViewById(R.id.btnNext);
        btnSolve = (Button) findViewById(R.id.btnSolve);

        btnNext.setVisibility(View.GONE);
        btnSolve.setVisibility(View.GONE);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        JavascriptInterface jsInterface = new JavascriptInterface(this);
        webView.addJavascriptInterface(jsInterface, "jsInterface");

        //TODO Disable when release
        WebView.setWebContentsDebuggingEnabled(true);

        Intent intent = getIntent();
        folderId = intent.getStringExtra("folderId");
        folderName = intent.getStringExtra("folderName");
//        receiveData();
    }

//    private void receiveData() {
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url = Configuration.getInstance().getBaseUrl() + "/folder/" + folderId + "/exercise";
//
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//            new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Gson gson = new GsonBuilder().create();
//                    exercise = gson.fromJson(response, Exercise.class);
//
//                    if (progressDialog.isShowing()) {
//                        progressDialog.dismiss();
//                    }
//
//                    drawHtml();
//                }
//            },
//
//            new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                    if (progressDialog.isShowing()) {
//                        progressDialog.dismiss();
//                    }
//                    Toast.makeText(ExerciseActivity.this, "Error " + error, Toast.LENGTH_LONG).show();
//                }
//            }
//        );
//
//        progressDialog = ProgressDialog.show(ExerciseActivity.this, "Loading", "Loading...");
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest);
//    }

    private void drawHtml() {
//        if(exercise != null) {
//            ExerciseHtmlComposer exerciseHtmlComposer = new ExerciseHtmlComposer(exercise);
//            webView.loadDataWithBaseURL("file:///android_asset/", exerciseHtmlComposer.toHtml(), "text/html", "utf-8", null);
//            btnSolve.setVisibility(View.VISIBLE);
//        } else {
//            Toast.makeText(this, "There is no data in folder " + folderName, Toast.LENGTH_LONG).show();
//        }
    }


    public void onNext(View view) {
//        receiveData();
        btnSolve.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.GONE);
    }

    public void onSolve(View view) {
        webView.loadUrl("javascript:showSolution();");
        btnSolve.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
    }

    public static class JavascriptInterface {

        private ExerciseActivity activity;

        public JavascriptInterface(ExerciseActivity activity) {
            this.activity = activity;
        }

        @android.webkit.JavascriptInterface
        public void check(boolean result) {

//            activity.btnSolve.post(new Runnable() {
//                @Override
//                public void run() {
//                    activity.buttonLayout.setVisibility(View.VISIBLE);
//                }
//            });
        }
    }
}
