package com.example.obdiibtsim;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddCustomPidActivity extends Activity {
	
	private Toast mToast = null;
	private Button addBtn;
	private Button cancelBtn;
	private EditText nameField;
	private EditText valueField;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_pid);
		
		addBtn = (Button) findViewById(R.id.addBtn);
		cancelBtn = (Button) findViewById(R.id.cancelBtn);
		nameField = (EditText)findViewById(R.id.pidNameField);
		valueField = (EditText)findViewById(R.id.pidValueField);
		
		nameField.setPrivateImeOptions("defaultInputmode=english;");
		valueField.setPrivateImeOptions("defaultInputmode=english;");
		
		addBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				addBtnProcess();
			}     	
		});	
		
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				cancelProcess();
			}        	
		});	
	}

	private void addBtnProcess() {
		String nameTxt = nameField.getText().toString();
		String valueTxt = valueField.getText().toString();
		
		//둘 다 있을 경우만 처리 가능
		if(thereIsNoEmptyField(nameTxt, valueTxt)) {
			Intent i = getIntent();
			i.putExtra("customPidName", nameTxt);
			i.putExtra("customPidValue", valueTxt);
			setResult(0, i);
			finish();
		}
		else {
			mToast = Toast.makeText(AddCustomPidActivity.this, "You need to fill the empty field(s)", Toast.LENGTH_SHORT);
			mToast.show();
		}
	}

	private boolean thereIsNoEmptyField(String nameTxt, String valueTxt) {
		return (!nameTxt.equals(""))&&(!valueTxt.equals(""));
	}   
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.custom_pid, menu);
		return true;
	}
	public boolean onKeyDown( int KeyCode, KeyEvent event )
	{
		if( event.getAction() == KeyEvent.ACTION_DOWN ){
			if( KeyCode == KeyEvent.KEYCODE_BACK ){
				//여기에 뒤로 버튼을 눌렀을때 해야할 행동을 지정한다
				cancelProcess();
				return true;
			}
		}
		return super.onKeyDown( KeyCode, event );
	}

	private void cancelProcess() {
		setResult(1);
		finish();
	}
}
