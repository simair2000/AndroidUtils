package com.simair.android.androidutils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * 데이터베이스 기본 클래스, TABLE 클래스 연동
 * 
 * @author bong
 * 
 */
public abstract class DATABASE extends SQLiteOpenHelper {

	private static final String TAG = DATABASE.class.getSimpleName();
	@SuppressWarnings("rawtypes")
	private Map<String, TABLE> map = new HashMap<String, TABLE>();

	public static final int SQLITE_STATUS_CREATE = 1;
	public static final int SQLITE_STATUS_UPGRADE = 2;
	public static final int SQLITE_STATUS_NONE = 3;
	public static final int SQLITE_STATUS_OPEN = 4;

	private int status = SQLITE_STATUS_NONE;

	public DATABASE(Context context, String name, int version) {
		super(context, name, null, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// onEvent(true);
		Log.d(TAG, "onCreate");
		status = SQLITE_STATUS_CREATE;
	}

	/**
	 * 모든 테이블에 이벤트 전달
	 * 
	 * @param f
	 *            true : 생성, false : 업그레이드
	 */
	@SuppressWarnings("unused")
	private void onEvent(boolean f) {
		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			try {
				String tn = keys.next();
				if (tn != null) {
					@SuppressWarnings("rawtypes")
					TABLE table = map.get(tn);
					if (table != null) {
						if (f) {
							table.onCreate();
						} else {
							table.onUpgrade();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// onEvent(false);
		Log.d(TAG, "onUpgrade");
		if (oldVersion < newVersion) {
			status = SQLITE_STATUS_UPGRADE;
		}
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
		Log.d(TAG, "onOpen");
		if (status == SQLITE_STATUS_NONE) {
			status = SQLITE_STATUS_OPEN;
		}
	}

	/**
	 * TABLE 로부터 호출되며 데이터베이스의 상태를 전달.
	 * 
	 * @param clazz
	 */
	@SuppressWarnings("rawtypes")
	public void connect(TABLE clazz) {

		if (clazz != null) {
			String tn = clazz.getClass().getSimpleName();
			if (map.get(tn) != null) {
				return;
			}

			map.put(tn, clazz);
			Log.d(TAG, "connect : " + tn + "(" + status + ")");
			switch (status) {
			case SQLITE_STATUS_CREATE: {
				clazz.onCreate();
				break;
			}
			case SQLITE_STATUS_UPGRADE: {
				clazz.onUpgrade();
				break;
			}
			case SQLITE_STATUS_NONE: {
				clazz.onConnect();
				break;
			}
			case SQLITE_STATUS_OPEN: {
				clazz.onOpen();
				break;
			}
			default:
				break;
			}

		}
	}

	/**
	 * 데이터베이스 저장된 테이블 제거
	 */
	public void release() {
		if (map != null) {
			map.clear();
			map = null;
		}
	}

	/**
	 * 테이블 레코드 삭제
	 */
	@SuppressWarnings("rawtypes")
	public void clearAll() {
		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			try {
				String tn = keys.next();
				if (tn != null) {
					TABLE table = map.get(tn);
					if (table != null) {
						table.delete();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
