package arcadia.bahti.autoquiz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AddQuizActivity extends AppCompatActivity {

    private TextView message;
    private EditText urlText;
    private EditText topicName;
    private Button button;

    private boolean finished = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(AddQuizActivity.this, HomePage.class));
                    return true;
                case R.id.navigation_quizzes:
                    startActivity(new Intent(AddQuizActivity.this, MainActivity.class));
                    return true;
            }
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz2);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        message = (TextView) findViewById(R.id.textView2);
        message.setText("Enter the data");

        urlText = (EditText) findViewById(R.id.link);
        urlText.setHint("Enter a Quizlet URL");

        topicName = (EditText) findViewById(R.id.topicname);
        topicName.setHint("Enter a topic Name");

        findViewById(R.id.save).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                            message.setText("PLEASE WAIT");
                            new Test().execute();


                        break;
                }

                return false;
            }
        });
    }
    private class Test extends AsyncTask<Void,Void,Void> {

        ArrayList<String> lines;

        @Override
        protected Void doInBackground(Void... params){
            URL textUrl;

            try{
                System.out.println(urlText.getText().toString());
                String str = urlText.getText().toString();
                textUrl = new URL(str);
                lines = new ArrayList<>();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(textUrl.openStream()));
                String sb;
                while ((sb = bufferedReader.readLine()) != null){
                    lines.add(sb);
                }

                bufferedReader.close();

            }catch (Exception e){
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            try {
                String t = "";
                String total = "";
                String trigger = "<span class=\"TermText notranslate lang-en\">";

                String line = lines.get(19);
                String output = "";
                int i = 0;
                while (line.contains(trigger)) {

                    i++;

                    int l = line.indexOf(trigger);
                    String test = line.substring(l + trigger.length());
                    output += line.substring(l + trigger.length(), test.indexOf("<") + l + trigger.length());

                    if (i % 2 == 0){
                        output+="\n";
                    }else{
                        output+=":";
                    }

                    line = line.substring(line.indexOf(trigger) + trigger.length() + 10);
                }
                System.out.println(output);
                message.setText("Click Quizzes/Home");

                MainActivity.getSelf().addQuiz(output,topicName.getText().toString());
            }catch (Exception e){

            }
        }
    }
}
