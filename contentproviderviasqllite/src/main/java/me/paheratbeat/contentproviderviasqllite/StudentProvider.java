package me.paheratbeat.contentproviderviasqllite;

/**
 * Created by PAHeartBeat on 15/02/17.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class StudentProvider extends ContentProvider {
	private static final String URL_SCHEME = "content://";
	private static final String PROVIDER_NAME = "me.paheratbeat.contentproviderviasqllite";
	private static final String URL = URL_SCHEME + PROVIDER_NAME + "/" + "students";
	private static final Uri CONTENT_URI = Uri.parse(URL);

	private static final String _ID = "_id";
	private static final String NAME = "name";
	private static final String GRADE = "grade";

	private static final int STUDENTS = 1;
	private static final int STUDENT_ID = 2;

	private static final String DATABASE_NAME = "Collage";
	private static final String TABLE_NAME = "Students";
	private static final int DATABASE_VERSION = 1;

	private static final String CREATE_DB_TABLE =
			"CREATE TABLE " + TABLE_NAME +
					" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
					" name TEXT NOT NULL, " +
					" grade TEXT NOT NULL);";
	private static final UriMatcher uriMatcher;
	private static HashMap<String, String> STUDENTS_PROJECTION_MAP;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "students", STUDENTS);
		uriMatcher.addURI(PROVIDER_NAME, "students/#", STUDENT_ID);
	}

	/**
	 * Database specific fields and constants
	 */

	private SQLiteDatabase db;

	@Override
	public boolean onCreate() {
		Context context = getContext();
		DatabaseHelper dbHelper = new DatabaseHelper(context);

		/**
		 * Create a write able database which will trigger its
		 * creation if it doesn't already exist.
		 */

		db = dbHelper.getWritableDatabase();
		return (db == null) ? false : true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		/**
		 * Add a new student record
		 */
		long rowID = db.insert(TABLE_NAME, "", values);

		/**
		 * If record is added successfully
		 */
		if (rowID > 0) {
			Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}

		throw new SQLException("Failed to add a record into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(TABLE_NAME);

		switch (uriMatcher.match(uri)) {
			case STUDENTS:
				qb.setProjectionMap(STUDENTS_PROJECTION_MAP);
				break;

			case STUDENT_ID:
				qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
				break;
			default:
		}


		if (sortOrder == null || sortOrder == "") {
			/**
			 * By default sort on student names
			 */
			sortOrder = NAME;
		}


		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		/**
		 * register to watch a content URI for changes
		 */
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		switch (uriMatcher.match(uri)) {
			case STUDENTS:
				count = db.delete(TABLE_NAME, selection, selectionArgs);
				break;

			case STUDENT_ID:
				String id = uri.getPathSegments().get(1);
				count = db.delete(TABLE_NAME, _ID + " = " + id +
								(!TextUtils.isEmpty(selection) ? " AND(" + selection + ')' : ""),
						selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = 0;
		switch (uriMatcher.match(uri)) {
			case STUDENTS:
				count = db.update(TABLE_NAME, values, selection, selectionArgs);
				break;

			case STUDENT_ID:
				count = db.update(TABLE_NAME, values,
						_ID + " = " + uri.getPathSegments().get(1) +
								(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
						selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
			/**
			 * Get all student records
			 */
			case STUDENTS:
				return "vnd.android.cursor.dir/vnd.example.students";
			/**
			 * Get a particular student
			 */
			case STUDENT_ID:
				return "vnd.android.cursor.item/vnd.example.students";
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	/**
	 * Helper class that actually creates and manages
	 * the provider's underlying data repository.
	 */

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_DB_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}
}
