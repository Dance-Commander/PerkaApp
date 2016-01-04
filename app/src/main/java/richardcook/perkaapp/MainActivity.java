package richardcook.perkaapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.app.FragmentManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements SubmitDialog.SubmitDialogListener{

    EditText firstName;
    EditText lastName;
    EditText email;
    EditText github;
    EditText source;
    EditText resume;
    Button applyBtn;


    String resumePath;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        email = (EditText) findViewById(R.id.email);
        github = (EditText) findViewById(R.id.github);
        source = (EditText) findViewById(R.id.source);
        resume = (EditText) findViewById(R.id.resume);
        applyBtn = (Button) findViewById(R.id.apply_button);

    }

    public void resumeSelector(View v){
        FileChooser fileChooser = new FileChooser(MainActivity.this);
        fileChooser.setExtension(".pdf");
        fileChooser.setFileListener(new FileChooser.FileSelectedListener() {
            @Override
            public void fileSelected(final File file) {
                resume.setText(file.getName());
                resumePath =  file.getPath();
                resume.setError(null);
            }
        }).showDialog();
    }

    public void checkApplication(View v){
        List<EditText> requiredFields = new ArrayList<>();
        Boolean requiredField = false;

        requiredFields.add(firstName);
        requiredFields.add(lastName);
        requiredFields.add(email);
        requiredFields.add(resume);

        //Checks to make sure the required fields are filled in
        for(EditText editText: requiredFields) {
            if(editText.getText().toString().trim().equals("")){
                editText.setError("Required");
                requiredField = true;
            }
        }
        //Return if required fields are not filled
        if(requiredField){return;}

        FragmentManager fm = getFragmentManager();

        SubmitDialog dialog = new SubmitDialog();
        dialog.show(fm, "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save resumePath to state instance in case it is destroyed
        outState.putString("resumePath", resumePath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        resumePath = savedInstanceState.getString("resumePath");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        sendApplication();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    public void sendApplication(){
        SendApplication runner = new SendApplication(this);
        runner.execute(firstName.getText().toString(),lastName.getText().toString(),email.getText().toString(),github.getText().toString(),source.getText().toString(),resumePath);
    }
}
