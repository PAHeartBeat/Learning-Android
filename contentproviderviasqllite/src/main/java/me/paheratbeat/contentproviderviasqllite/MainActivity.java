package me.paheratbeat.contentproviderviasqllite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.content.ContentValues;

import android.database.Cursor;

import android.net.Uri;

import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import android.util.Log;

public class MainActivity extends AppCompatActivity {
	final String TAG="CPSL:MainActivity";
	EditText txtName;
	EditText txtGrade;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		bindControls();
	}

	public void bindControls() {
		txtName = (EditText) findViewById(R.id.txtName);
		txtGrade = (EditText) findViewById(R.id.txtGrade);
	}

	public void onClickAddName(View view) {
		Log.i(TAG,"Clicked on Add Name");
		// Add a new student record
		ContentValues values = new ContentValues();
		Log.i(TAG,"Creating Value");
		values.put("name", txtName.getText().toString());
		values.put("grade", txtGrade.getText().toString());

		Log.i(TAG,"Executing Insert Query");
		String URL = "content://me.paheratbeat.contentproviderviasqllite/students";
		Uri uri = getContentResolver().insert(Uri.parse(URL), values);

		Log.i(TAG,"Query Executed Successfully");
		Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
	}

	public void onClickRetrieveStudents(View view) {
		Log.i(TAG,"Clicked on Retrieve List");

		// Retrieve student records
		String URL = "content://me.paheratbeat.contentproviderviasqllite";

		Log.i(TAG,"Createing Uri for " + URL);
		Uri students = Uri.parse(URL);

		Log.i(TAG,"Executing Query");
		Cursor c = getContentResolver().query(students, null, null, null, "name");
		if (c == null) {
			Log.i(TAG,"No data found in cursor");
			Toast.makeText(this, "No Content Found", Toast.LENGTH_SHORT).show();
			return;
		}
		Log.i(TAG,"May be Cursor has some data");
		if (c.moveToFirst()) {
			do {
				Toast.makeText(this,
						c.getString(c.getColumnIndex("id")) +
								", " + c.getString(c.getColumnIndex("name")) +
								", " + c.getString(c.getColumnIndex("grade")),
						Toast.LENGTH_SHORT).show();
			} while (c.moveToNext());
		}
	}
}