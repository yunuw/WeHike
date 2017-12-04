package edu.uw.yw239.wehike.posts;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.uw.yw239.wehike.MainActivity;
import edu.uw.yw239.wehike.R;
import edu.uw.yw239.wehike.common.AccountInfo;

/**
 * Created by Nan on 12/3/17.
 */

public class CreateCommentActivity extends PostDetailActivity {
    private EditText commentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        commentText = (EditText) findViewById(R.id.commentEditText);

        commentText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    commentText.setHint("Say Something...");
                else
                    commentText.setHint("");
            }
        });
    }

    public void onResume(){
        commentText.setHint("Say Something...");;
        super.onResume();
    }

    protected void attemptCreateComment() {
        // Reset errors.
        commentText.setError(null);

        final String description = commentText.getText().toString().trim();

        View focusView = null;
        boolean cancel = false;

        if (description == null || description.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.warning_empty_description);
            builder.setPositiveButton(R.string.dialog_ok_button, null);
            builder.show();
            focusView = commentText;
            cancel = true;
        }

        createComment(description);
    }

    private void createComment(String description) {
        try {
            String userName = AccountInfo.getCurrentUserName();
            final Resources resources = this.getResources();
            final String backendPrefix = resources.getString(R.string.backend_prefix);

            // call API and register new user

        }
        catch (SecurityException ex) {
        }
        catch (Exception ex) {
            Toast.makeText(this, "Failed to create comment: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        };
    }


    final Button sendButton = (Button) findViewById(R.id.button_send_comment);
}
