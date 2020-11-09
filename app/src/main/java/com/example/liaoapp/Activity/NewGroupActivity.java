package com.example.liaoapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.framework.base.BaseBackActivity;
import com.example.liaoapp.R;

public class NewGroupActivity extends BaseBackActivity implements View.OnClickListener {
    private EditText etGroupId;
    private EditText etGroupName;
    private Button btnChose;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);


        etGroupId = findViewById(R.id.et_group_id);
        etGroupName = findViewById(R.id.et_group_name);
        btnChose = findViewById(R.id.btn_chose);

        btnChose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_chose:
                choseFriend();
                break;
        }
    }

    private void choseFriend() {
        String id = etGroupId.getText().toString().trim();
        if(TextUtils.isEmpty(id)){
            Toast.makeText(this, R.string.text_group_id_null, Toast.LENGTH_SHORT).show();
            return;
        }
        String name = etGroupName.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, R.string.text_group_name_null, Toast.LENGTH_SHORT).show();
            return;
        }


        Intent intent = new Intent(NewGroupActivity.this,ChoseFriendActivity.class);
        intent.putExtra("groupid",id);
        intent.putExtra("groupname",name);
        startActivity(intent);

    }
}
