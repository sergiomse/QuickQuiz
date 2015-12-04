package com.sergiomse.quickquiz.activities;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sergiomse.quickquiz.Q2Application;
import com.sergiomse.quickquiz.R;
import com.sergiomse.quickquiz.database.QuickQuizDAO;
import com.sergiomse.quickquiz.filechooser.FileChooserActivity;
import com.sergiomse.quickquiz.model.Exercise;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExerciseActivity extends AppCompatActivity {

    private static final String TAG = ExerciseActivity.class.getName();

    private File folder;
    private Random random = new Random();

    private String packageId;
    private String questionId;
    private int questionPosition;
    private boolean hasNote;
    private String jsonState;

    private final static Pattern questionIdPattern = Pattern.compile("(?s)^.*\"id\"\\s*:\\s*(\\d+).*$");

    private Button btnSolve;
    private Button btnNext;
    private RelativeLayout webviewLayout;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quick Quiz");

        btnNext = (Button) findViewById(R.id.btnNext);
        btnSolve = (Button) findViewById(R.id.btnSolve);
        webviewLayout = (RelativeLayout) findViewById(R.id.webviewLayout);

        btnNext.setVisibility(View.GONE);
        btnSolve.setVisibility(View.GONE);

        webView = new WebView(this);
        webviewLayout.addView(webView);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(params);

        webView.getSettings().setJavaScriptEnabled(true);

        JavascriptInterface jsInterface = new JavascriptInterface(this);
        webView.addJavascriptInterface(jsInterface, "jsInterface");

        //TODO Disable when release
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String folderPath = intent.getStringExtra("folderPath");
            folder = new File(folderPath);
        } else {
            folder = new File( savedInstanceState.getString("folderName"));
        }

        packageId = ((Q2Application) getApplication()).getInstalledPackageId( folder );

        selectQuestion(webView, savedInstanceState);
//        receiveData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshNoteIcon();
    }

    private void selectQuestion(WebView webView, Bundle savedInstanceState) {
        String htmlType1 = prepareHtml(savedInstanceState);

        webView.loadDataWithBaseURL("file:///android_asset/", htmlType1, "text/html", "utf-8", null);
        btnSolve.setVisibility(View.VISIBLE);
    }

    private void createNewQuestion() {
        String htmlType1 = prepareHtml(null);

        WebView webView = new WebView(this);
        webviewLayout.addView(webView, 0);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(params);

        webView.getSettings().setJavaScriptEnabled(true);

        JavascriptInterface jsInterface = new JavascriptInterface(this);
        webView.addJavascriptInterface(jsInterface, "jsInterface");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                ExerciseActivity.this.webView.animate()
                        .translationX(-ExerciseActivity.this.webView.getWidth())
                        .setDuration(250)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                ExerciseActivity.this.webView = (WebView) ExerciseActivity.this.webviewLayout.getChildAt(0);
                                ExerciseActivity.this.webviewLayout.removeViewAt(1);

                                refreshNoteIcon();
                                btnSolve.setVisibility(View.VISIBLE);
                                btnNext.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
            }
        });

        webView.loadDataWithBaseURL("file:///android_asset/", htmlType1, "text/html", "utf-8", null);
    }

    private String prepareHtml(Bundle savedInstanceState) {
        //get all the questions files
        File questions[] = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && file.getName().trim().endsWith(".json");
            }
        });

        String jsonQuestion = "";
        if (savedInstanceState == null) {
            //get random question
            questionPosition = random.nextInt(questions.length);

            //read files file
            jsonQuestion = readTextFile(questions[questionPosition]);

            //TODO improve parsing json
            Matcher matcher = questionIdPattern.matcher(jsonQuestion);
            if (matcher.matches()) {
                questionId = matcher.group(1).trim();
            } else {
                Log.e(TAG, "Question hasn't a proper id: " + jsonQuestion);
            }

        } else {
            questionPosition = savedInstanceState.getInt("questionPosition");
            jsonQuestion = readTextFile(questions[questionPosition]);
            questionId = savedInstanceState.getString("questionId");
            jsonState = savedInstanceState.getString("jsonState");

        }

        String htmlType1 = readTextFileFromAssets("type1.html");

        //inject json into html
        if (savedInstanceState == null) {
            return htmlType1.replace("</head>", "<script>" + jsonQuestion + "</script></head>");
        } else {
            return htmlType1.replace("</head>", "<script>state = " + jsonState + "</script><script>" + jsonQuestion + "</script></head>");
        }
    }


    private String readTextFileFromAssets(String file) {
        return readTextFile(null, file);
    }

    private String readTextFile(File file) {
        return readTextFile(file, null);
    }

    private String readTextFile(File file, String fileInAssets) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;

        try {
            if( fileInAssets == null ) {
                br = new BufferedReader(new FileReader(file));
            } else {
                br = new BufferedReader(new InputStreamReader( getAssets().open( fileInAssets ) ));
            }

            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    public void onNext(View view) {
        btnNext.setEnabled(false);
        createNewQuestion();
    }


    private void refreshNoteIcon() {
        String note = QuickQuizDAO.getNote(this, packageId, questionId);
        hasNote = note != null;
        invalidateOptionsMenu();
    }

    public void onSolve(View view) {
        webView.loadUrl("javascript:showSolution(true);");
        btnSolve.setVisibility(View.GONE);
        btnNext.setEnabled(true);
        btnNext.setVisibility(View.VISIBLE);
    }

    public static class JavascriptInterface {

        private ExerciseActivity activity;

        public JavascriptInterface(ExerciseActivity activity) {
            this.activity = activity;
        }

        @android.webkit.JavascriptInterface
        public void setState(String jsonState) {
            activity.jsonState = jsonState;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.exercise_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if ( hasNote ) {
            menu.findItem(R.id.action_add_note).setIcon(R.drawable.img_pin_on);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_note:
                Intent intent = new Intent(this, NoteActivity.class);
                intent.putExtra("packageId", packageId);
                intent.putExtra("questionId", questionId);
                startActivity( intent );
                break;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("folderName", folder.getAbsolutePath());
        outState.putInt("questionPosition", questionPosition);
        outState.putString("questionId", questionId);
        outState.putString("jsonState", jsonState);

        super.onSaveInstanceState(outState);
    }
}
