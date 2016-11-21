package com.simair.android.androidutils.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 테이블 기본 클래스
 * 
 * @author bong
 * 
 */
public class TABLE<T> {

	private static final String TAG = TABLE.class.getSimpleName();

	private static final String JAVA_INT = "int";
	private static final String JAVA_SHORT = "short";
	private static final String JAVA_LONG = "long";
	private static final String JAVA_FLOAT = "float";
	private static final String JAVA_DOUBLE = "double";
	private static final String JAVA_STRING = "String";

	private static final String SQLITE_INT = "INTEGER";
	private static final String SQLITE_SHORT = "SHORT INTEGER";
	private static final String SQLITE_LONG = "LONG";
	private static final String SQLITE_FLOAT = "FLOAT";
	private static final String SQLITE_DOUBLE = "DOUBLE";
	private static final String SQLITE_STRING = "TEXT";

	private static final int SQLITE_IDX_INT = 1;
	private static final int SQLITE_IDX_SHORT = 2;
	private static final int SQLITE_IDX_LONG = 3;
	private static final int SQLITE_IDX_FLOAT = 4;
	private static final int SQLITE_IDX_DOUBLE = 5;
	private static final int SQLITE_IDX_STRING = 6;

	private static final int SELECT = 1;
	private static final int UPDATE = 2;
	private static final int INSERT = 3;
	private static final int DELETE = 4;

	private DATABASE db = null;
	private SQLiteDatabase sqlite = null;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface FieldAttribute {
		/**
		 * 필드 옵션
		 * 
		 * @return
		 */
		String option() default "";

		/**
		 * 필드 insert 여부 ex) AUTOINCREMENT
		 * 
		 * @return
		 */
		boolean ignore() default false;

		/**
		 * 필드 null 허용여부
		 * 
		 * @return
		 */
		boolean allowNULL() default true;

		/**
		 * 필드 적용 여부
		 * 
		 * @return
		 */
		boolean apply() default true;
	}

	public TABLE(DATABASE db, Boolean arg0) {
		// Log.d(TAG, "constructure : TABLE(DATABASE db) + " + arg0);
		if (arg0) {
			connect(db);
		}
	}

	public TABLE() {
		// Log.d(TAG, "constructure : TABLE()");
	}

	/**
	 * 테이블 존재여부 반환
	 * 
	 * @return
	 */
	public boolean exist() {

		if (sqlite == null) {
			return false;
		}

		boolean result = false;
		Cursor cursor = null;
		try {
			cursor = sqlite.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + getTableName()
					+ "'", null);
			if (cursor != null) {
				if (cursor.getCount() > 0) {
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return result;
	}

	/**
	 * 변수 인덱스를 문자열 형태로 반환 (index -> sqlite variable type)
	 * 
	 * @param idx
	 * @return
	 */
	private String sys2db(int idx) {

		String vt = "";
		switch (idx) {
		case SQLITE_IDX_INT: {
			vt = SQLITE_INT;
			break;
		}
		case SQLITE_IDX_SHORT: {
			vt = SQLITE_SHORT;
			break;
		}
		case SQLITE_IDX_LONG: {
			vt = SQLITE_LONG;
			break;
		}
		case SQLITE_IDX_FLOAT: {
			vt = SQLITE_FLOAT;
			break;
		}
		case SQLITE_IDX_DOUBLE: {
			vt = SQLITE_DOUBLE;
			break;
		}
		default: {
			vt = SQLITE_STRING;
			break;
		}
		}
		return vt;
	}

	/**
	 * java 형태의 변수형을 sqlite 변수형의 index 로 반환
	 * 
	 * @param vt
	 * @return
	 */
	private int sys2idx(String vt) {

		int idx = 0;
		if (vt.contains(JAVA_INT)) {
			idx = SQLITE_IDX_INT;
		} else if (vt.contains(JAVA_SHORT)) {
			idx = SQLITE_IDX_SHORT;
		} else if (vt.contains(JAVA_LONG)) {
			idx = SQLITE_IDX_LONG;
		} else if (vt.contains(JAVA_FLOAT)) {
			idx = SQLITE_IDX_FLOAT;
		} else if (vt.contains(JAVA_DOUBLE)) {
			idx = SQLITE_IDX_DOUBLE;
		} else if (vt.contains(JAVA_SHORT)) {
			idx = SQLITE_IDX_SHORT;
		} else {
			idx = SQLITE_IDX_STRING;
		}

		return idx;
	}

	/**
	 * 테이블 생성 이벤트
	 */
	protected void onCreate() {
		create();
	}

	/**
	 * 테이블 생성
	 */
	public void create() {

		if (sqlite == null) {
			return;
		}

		if (exist()) {
			return;
		}

		String fs = "";
		FieldData fds[] = getFieldsAttribute(this);

		if (fds.length > 0) {

			for (int i = 0; i < fds.length; i++) {
				if (fs.length() > 0) {
					fs += ",";
				}

				fs += fds[i].name + " " + sys2db(fds[i].vti);
				if (fds[i].option.length() > 0) {
					fs += " " + fds[i].option;
				} else {
					if (!fds[i].allowNULL) {
						fs += " NOT NULL";
					}
				}

			} // end of for

			if (fs.length() > 0) {
				// if not exists
				String sql = "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" + fs + ");";
				Log.d(TAG, "execSQL : " + sql);

				try {
					sqlite.execSQL(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} // end of if
		}
	}

	/**
	 * 테이블 변경
	 */
	protected void onUpgrade() {

		if (sqlite == null) {
			return;
		}

		try {
			String sql = "DROP TABLE IF EXISTS " + getTableName();
			// Log.d(TAG, "execSQL : " + sql);
			sqlite.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.onCreate();

	}

	/**
	 * 변화없음
	 */
	protected void onConnect() {

	}

	/**
	 * DB 파일 Open
	 */
	protected void onOpen() {

	}

	/**
	 * DATABASE 객체에서 호출됨. database -> table 객체 저장.
	 * 
	 * @param db
	 */
	public void connect(DATABASE db) {
		this.db = db;
		sqlite = this.db.getWritableDatabase();
		if (!exist()) {
			create();
		}
		this.db.connect(this);
	}

	/**
	 * 테이블 클래스의 정보를 저장
	 * 
	 * @author bong
	 * 
	 */
	private static class FieldData {

		private int vti = 0;
		private String name = null;
		private Object value = null;

		private String option = "";
		private boolean ignore = false;
		private boolean allowNULL = true;
		private boolean apply = true;

		private Field object = null;
		private boolean pk = false;

		private FieldData(Field object, int vti, String name, Object value) {
			this.vti = vti;
			this.name = name;
			this.value = value;

			this.object = object;
		}

		private FieldData() {
		}
	}

	/**
	 * 클래스명 반환
	 * 
	 * @return
	 */
	protected String getTableName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * 클래스 변수 분석 및 옵션값 반환
	 * 
	 * @param clazz
	 * @return
	 */
	private FieldData[] getFieldsAttribute(TABLE<T> clazz) {

		FieldData fdz[] = null;

		Field[] fields = clazz.getClass().getFields();
		if (fields != null && fields.length > 0) {
			FieldData fds[] = null;
			int vf = fields.length; // valid field.
			fds = new FieldData[fields.length];
			int i;
			int c = fields.length;
			for (i = 0; i < c; i++) {
				try {
					String name = fields[i].getName();
					Object value = fields[i].get(clazz);
					fds[i] = new FieldData(fields[i], sys2idx(fields[i].getType().getName()), name, value);
					FieldAttribute fa = fields[i].getAnnotation(FieldAttribute.class);
					if (fa != null) {
						fds[i].option = fa.option();
						if (fds[i].option != null) {
							fds[i].pk = fds[i].option.contains("PRIMARY");
						}
						fds[i].ignore = fa.ignore();
						fds[i].allowNULL = fa.allowNULL();
						fds[i].apply = fa.apply();

						if(fields[i].isSynthetic()) {
							fds[i].apply = false;
						}

						if (!fds[i].apply) {
							vf--;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} // end of for

			fdz = new FieldData[vf];

			int j = 0;
			for (i = 0; i < c; i++) {
				if (fds[i].apply) {
					fdz[j++] = fds[i];
				}
			}
		}

		return fdz;
	}

	/**
	 * 레코드 수 반환
	 * 
	 * @return
	 */
	public int count() {

		if (sqlite == null) {
			return -1;
		}

		int c = -1;
		Cursor cursor = null;
		try {
			cursor = sqlite.rawQuery("SELECT COUNT(*) FROM " + getTableName(), null);
			if (cursor != null) {
				cursor.moveToFirst();
				c = cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return c;
	}

	/**
	 * 
	 * 레코드 수 반환
	 * 
	 * 
	 * 
	 * @param sql
	 * 
	 * @return
	 * 
	 */

	public int count(String sql) {

		if (sqlite == null) {
			return -1;
		}

		int c = -1;
		Cursor cursor = null;
		try {
			cursor = sqlite.rawQuery(sql, null);
			if (cursor != null) {
				cursor.moveToFirst();
				c = cursor.getInt(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return c;
	}

	// --> simair
	/**
	 * 레코드 insert 혹은 primary key 값이 동일한 record 일 경우 update 한다
	 * @return record count
	 */
	public int insertOrUpdate() {

		if (sqlite == null) {
			return -1;
		}

		String fields = "";
		String values = "";

		FieldData fds[] = getFieldsAttribute(this);

		if (fds.length > 0) {

			for (int i = 0; i < fds.length; i++) {
				if (!fds[i].ignore && fds[i].value != null) {
					if (fields.length() > 0) {
						fields += ",";
					}
					fields += fds[i].name;
					if (values.length() > 0) {
						values += ",";
					}
					if (fds[i].name == null) {
						return -1;
					}
					values += "'" + fds[i].value + "'";
				}
			} // end of for

			if (fields.length() > 0) {
				String sql = "INSERT OR REPLACE INTO " + getTableName() + " (" + fields + ") " + "VALUES" + "(" + values + ");";
				// Log.d(TAG, "execSQL : " + sql);
				try {
					sqlite.execSQL(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} // end of if
		}

		return count();
	}
	// <-- simair

	/**
	 * 레코드 삽입 (전체:ContentValues)
	 * 
	 * @return
	 */
	public int insertx() {

		if (sqlite == null) {
			return -1;
		}

		int count = 0;
		ContentValues cvs = new ContentValues();
		FieldData fds[] = getFieldsAttribute(this);
		if (fds.length > 0) {

			for (int i = 0; i < fds.length; i++) {
				if (!fds[i].ignore && fds[i].value != null) {
					cvs.put(fds[i].name, "" + fds[i].value);
				}
			} // end of for

			try {
				count = (int) sqlite.insert(getTableName(), null, cvs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return count;
	}

	/**
	 * 레코드 삽입 (전체,쿼리)
	 * 
	 * @return
	 */
	public int insert() {

		if (sqlite == null) {
			return -1;
		}

		String fields = "";
		String values = "";

		FieldData fds[] = getFieldsAttribute(this);

		if (fds.length > 0) {

			for (int i = 0; i < fds.length; i++) {
				if (!fds[i].ignore && fds[i].value != null) {

					if (fields.length() > 0) {
						fields += ",";
					}
					fields += fds[i].name;
					if (values.length() > 0) {
						values += ",";
					}

					if (fds[i].name == null) {
						return -1;
					}

					values += "'" + fds[i].value + "'";
				}

			} // end of for

			if (fields.length() > 0) {
				String sql = "INSERT INTO " + getTableName() + " (" + fields + ") " + "VALUES" + "(" + values + ");";
				// Log.d(TAG, "execSQL : " + sql);
				try {
					sqlite.execSQL(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} // end of if
		}

		return count();
	}

	public long insert(ArrayList<ContentValues> values) {
		long cnt = 0;
		if (sqlite == null) {
			return 0;
		}

		try {
			sqlite.beginTransaction();
			for (ContentValues cv : values) {
				cnt += sqlite.insert(getTableName(), null, cv);
			}
			sqlite.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sqlite.endTransaction();
		}

		return cnt;
	}

	/**
	 * 필드값 확인
	 * 
	 * @param fd
	 * @return
	 */
	private boolean invalid(FieldData fd) {

		boolean f = false;

		switch (fd.vti) {
		case SQLITE_IDX_INT: {
			if ((Integer) fd.value == -1) {
				f = true;
			}
			break;
		}
		case SQLITE_IDX_SHORT: {
			if ((Short) fd.value == -1) {
				f = true;
			}
			break;
		}
		case SQLITE_IDX_LONG: {
			if ((Long) fd.value == -1) {
				f = true;
			}
			break;
		}
		case SQLITE_IDX_FLOAT: {
			if ((Float) fd.value == -1) {
				f = true;
			}
			break;
		}
		case SQLITE_IDX_DOUBLE: {
			if ((Double) fd.value == -1) {
				f = true;
			}
			break;
		}
		default: {
			if (fd.value == null) {
				f = true;
			}
			break;
		}
		}

		return f;
	}

	/**
	 * 테이블의 URI 반환
	 * 
	 * @param context
	 * @return
	 */
	public Uri getTableUri(Context context) {

		String uri = null;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			if (info != null) {
				uri = "sqlite://";
				uri += info.packageName;
				uri += "/table/";
				uri += getTableName();
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (uri != null) ? Uri.parse(uri) : null;
	}

	/**
	 * 레코드 변경 (전체, 쿼리)
	 * 
	 * @return
	 */
	public boolean update() {

		if (sqlite == null) {
			return false;
		}

		String set = "";
		FieldData fds[] = getFieldsAttribute(this);

		if (fds.length > 0) {

			FieldData pk = null;
			for (int i = 0; i < fds.length; i++) {
				if (!fds[i].ignore) {

					if (!invalid(fds[i]) && !fds[i].pk) {
						if (set.length() > 0) {
							set += ",";
						}

						set += fds[i].name + "=" + "'" + fds[i].value + "'";
					}
				}

				if (fds[i].pk) {
					pk = fds[i];
				}

			} // end of for

			if (set.length() > 0) {
				String sql = "UPDATE " + getTableName() + " SET " + set;
				if (pk != null) {
					sql += " WHERE " + pk.name + "=" + "'" + pk.value + "'";

					try {
						return update(sql);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} // end of if
		}

		return false;
	}

	/**
	 * 레코드 변경 (조건부)
	 * 
	 * @param sql
	 * @return
	 */
	public boolean update(String sql) {

		if (sqlite == null) {
			return false;
		}

		// Log.d(TAG, "execSQL : " + sql);
		try {
			sqlite.execSQL(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 레코드 변경 (ContentValues)
	 * 
	 * @return
	 */
	public boolean updatex() {

		if (sqlite == null) {
			return false;
		}

		FieldData fds[] = getFieldsAttribute(this);

		if (fds.length > 0) {

			FieldData pk = null;
			ContentValues cvs = new ContentValues();

			for (int i = 0; i < fds.length; i++) {
				if (!fds[i].ignore) {
					// if (fds[i].value != null) {
					if (!invalid(fds[i])) {
						cvs.put(fds[i].name, "" + fds[i].value);
					}
				}

				if (fds[i].pk) {
					pk = fds[i];
				}

			} // end of for

			try {
				if (pk != null) {
					sqlite.update(getTableName(), cvs, pk.name + " = ?", new String[] { "" + pk.value });
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * 테이블 잠금
	 */
	public void lock() {
		if (sqlite != null) {
			try {
				Log.d(TAG, ">>>>>>>>>>>>>> beginTransaction");
				sqlite.beginTransaction();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 테이블 잠금 해제
	 */
	public void unlock() {
		if (sqlite != null) {
			try {
				Log.d(TAG, ">>>>>>>>>>>>>> endTransaction");
				sqlite.setTransactionSuccessful();
				sqlite.endTransaction();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 레코드 삭제
	 */
	public void delete() {
		if (sqlite != null) {
			try {
				// Log.d(TAG, "delete table : " + getTableName());
				sqlite.delete(getTableName(), null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean delete1() {
		if (sqlite == null) {
			return false;
		}

		try {
			String sql = sqlstr(DELETE);
			Log.d(TAG, "delete1 table : " + sql);
			sqlite.execSQL(sql);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public void delete(String where) {
		if (sqlite == null) {
			return;
		}

		try {
			sqlite.delete(getTableName(), where, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete(String where, String[] args) {
		if (sqlite == null) {
			return;
		}

		try {
			sqlite.delete(getTableName(), where, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean sqlupdate(String set, String columnName, List<Long> list) {

		String command = "UPDATE ";
		String sql = command + getTableName();
		sql += " SET " + set;
		String where = list2where(columnName, list);
		if (where != null && where.length() > 0) {
			sql += " WHERE " + where;
		}

		return update(sql);
	}

	/**
	 * select 를 제외한 쿼리에서 사용가능.
	 * 
	 * @param sql
	 */
	public void execute(String sql) {
		if (sqlite == null) {
			return;
		}
		try {
			sqlite.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<T> selectn() {
		String sql = sqlstr(SELECT);
		return select(sql);
	}

	/**
	 * 레코드 반환
	 * 
	 * @return 오브젝트
	 */
	public List<T> select() {
		return select("SELECT * FROM " + getTableName());
	}

	/**
	 * 커서 반환 (close 주의)
	 * 
	 * @return
	 */
	public Cursor cursor() {
		Cursor cursor = null;
		try {
			// String sql = "SELECT * FROM " + getTableName();
			String sql = sqlstr(SELECT);
			cursor = sqlite.rawQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}

	public Cursor cursor(String sql) {
		Cursor cursor = null;
		try {
			cursor = sqlite.rawQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}

	/**
	 * 테이블의 값을 유효하지 않은 상태로 변경
	 * 
	 * @return
	 */
	public T initialize() {
		return clear(-1);
	}

	/**
	 * 초기화
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T clear() {
		return clear(0);
	}

	/**
	 * 초기화
	 * 
	 * @param cv
	 *            초기화값.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T clear(int cv) {
		FieldData fds[] = getFieldsAttribute(this);
		if (fds.length > 0) {
			for (int i = 0; i < fds.length; i++) {
				try {
					Object value = null;
					switch (fds[i].vti) {
					case SQLITE_IDX_DOUBLE:
					case SQLITE_IDX_LONG:
					case SQLITE_IDX_FLOAT:
					case SQLITE_IDX_SHORT:
					case SQLITE_IDX_INT: {
						value = cv;
						break;
					}
					default: {
						value = null;
						break;
					}
					}

					fds[i].object.set(this, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} // end of for
		}

		return (T) this;
	}

	private String sqlwhere() {
		String sql = "";

		FieldData fds[] = getFieldsAttribute(this);
		if (fds.length > 0) {
			FieldData fd = null;
			for (int i = 0; i < fds.length; i++) {
				switch (fds[i].vti) {
				case SQLITE_IDX_DOUBLE: {
					if ((Double) fds[i].value != 0) {
						fd = fds[i];
					}
					break;
				}
				case SQLITE_IDX_LONG: {
					if ((Long) fds[i].value != 0) {
						fd = fds[i];
					}
					break;
				}
				case SQLITE_IDX_FLOAT: {
					if ((Float) fds[i].value != 0) {
						fd = fds[i];
					}
					break;
				}
				case SQLITE_IDX_INT: {
					if ((Integer) fds[i].value != 0) {
						fd = fds[i];
					}
					break;
				}
				case SQLITE_IDX_SHORT: {
					if ((Short) fds[i].value != 0) {
						fd = fds[i];
					}
					break;
				}
				default: {
					if (fds[i].value != null) {
						fd = fds[i];
					}
					break;
				}
				}

				if (fd != null) {
					// 2015.8.25
					// where 절에 조건이 여러개 들어가도록 수정해야 하는데, 바꾸니까 여기저기서 오류나서 일단 보류
					// if (sql.length() > 0) {
					// sql += " AND ";
					// }

					sql += fd.name + "=" + "'" + fd.value + "'";
					// fd = null;
					break;

				}
			}
		}
		return sql;
	}

	private String sqlstr(int idx) {

		String command = "";
		switch (idx) {
		case SELECT: {
			command = "SELECT * FROM ";
			break;
		}
		case DELETE: {
			command = "DELETE FROM ";
			break;
		}
		default:
			return "";
		}

		String sql = command + getTableName();
		String where = sqlwhere();
		if (where != null && where.length() > 0) {
			sql += " WHERE " + where;
		}
		return sql;
	}

	public List<T> sqlwhere(String columnName, List<Long> list) {

		String command = "SELECT * FROM ";
		String sql = command + getTableName();
		String where = list2where(columnName, list);
		if (where != null && where.length() > 0) {
			sql += " WHERE " + where;
		}

		return select(sql);
	}

	public List<T> sqlwhere(String where) {
		String command = "SELECT * FROM ";
		String sql = command + getTableName();
		if (where != null && where.length() > 0) {
			sql += " WHERE " + where;
		}

		return select(sql);
	}

	private String list2where(String columnName, List<Long> list) {
		String str = "";
		for (long iuid : list) {
			if (list.get(0) != iuid)
				str += " or ";
			str += String.format("%s=%d", columnName, iuid);
		}

		return str;
	}

	/**
	 * 레코드 반환 (자료구조 형태 1)
	 * 
	 * @return
	 */
	public Map<String, String> select1x() {

		Map<String, String> map = null;
		List<HashMap<String, String>> l = selectx(sqlstr(SELECT));
		if (l.size() > 0) {
			map = l.get(0);
		}
		return map;
	}

	/**
	 * 레코드 반환 (1)
	 * 
	 * @return
	 */
	public T select1() {

		T t = null;
		List<T> l = select(sqlstr(SELECT));
		if (l.size() > 0) {
			t = l.get(0);
		}
		return t;
	}

	/**
	 * 레코드 반환
	 * 
	 * @param sql
	 *            커스텀 쿼리
	 * @return
	 */
	public T select1(String sql) {

		T t = null;
		List<T> l = select(sql);
		if (l.size() > 0) {
			t = l.get(0);
		}
		return t;
	}

	/**
	 * 레코드 반환 (조건부)
	 * 
	 * @param sql
	 * @return 오브젝트
	 */
	@SuppressWarnings("unchecked")
	public List<T> select(String sql) {

		if (sqlite == null) {
			return null;
		}

		List<T> clazzs = new ArrayList<T>();
		Cursor cursor = null;
		try {

			// Log.d(TAG, "rawQuery : " + sql);
			cursor = sqlite.rawQuery(sql, null);
			if (cursor != null) {

				String columnNames[] = cursor.getColumnNames();
				int c = columnNames.length;

				int fdIdx[] = new int[c];
				FieldData fds[] = null;

				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

					// TABLE clazz = this.getClass().newInstance();
					TABLE<T> clazz = this.getClass().asSubclass(this.getClass()).getConstructor(Boolean.class)
							.newInstance(false);

					clazz.sqlite = sqlite;

					boolean isFirst = false;
					if (fds == null) {
						fds = getFieldsAttribute(clazz);
						isFirst = true;
					}
					if (fds == null || fds.length <= 0) {
						continue;
					}

					for (int i = 0; i < c; i++) {
						if (isFirst) {
							String column = columnNames[i];
							for (int j = 0; j < fds.length; j++) {
								if (column.equals(fds[j].name)) {
									fdIdx[i] = j;
									break;
								}
							}
						}

						Object value = null;
						switch (fds[fdIdx[i]].vti) {
						case SQLITE_IDX_INT: {
							value = cursor.getInt(i);
							break;
						}
						case SQLITE_IDX_SHORT: {
							value = cursor.getShort(i);
							break;
						}
						case SQLITE_IDX_FLOAT: {
							value = cursor.getFloat(i);
							break;
						}
						case SQLITE_IDX_LONG: {
							value = cursor.getLong(i);
							break;
						}
						case SQLITE_IDX_DOUBLE: {
							value = cursor.getDouble(i);
							break;
						}
						default: {
							value = cursor.getString(i);
							break;
						}
						}

						fds[fdIdx[i]].object.set(clazz, value);
					} // end of if (int i)

					clazzs.add((T) clazz);
				} // end of of (cursor)
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return clazzs;
	}

	/**
	 * 레코드 반환 (조건부)
	 * 
	 * @param sql
	 * @return 오브젝트
	 */
	@SuppressWarnings("unchecked")
	public Cursor selectToCursor(String sql) {
		if (sqlite == null) {
			return null;
		}

		return sqlite.rawQuery(sql, null);
	}

	/**
	 * 레코드 반환 (전체)
	 * 
	 * @return 자료구조 형태
	 */
	public List<HashMap<String, String>> selectx() {
		return selectx("SELECT * FROM " + getTableName());
	}

	/**
	 * 레코드 반환 (조건부)
	 * 
	 * @param sql
	 * @return 자료구조 형태
	 */
	public List<HashMap<String, String>> selectx(String sql) {

		if (sqlite == null) {
			return null;
		}

		List<HashMap<String, String>> lists = new ArrayList<HashMap<String, String>>();
		Cursor cursor = null;
		try {

			// Log.d(TAG, "rawQuery : " + sql);

			cursor = sqlite.rawQuery(sql, null);
			if (cursor != null) {
				String columnNames[] = cursor.getColumnNames();
				int c = columnNames.length;
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					HashMap<String, String> map = new HashMap<String, String>();
					for (int i = 0; i < c; i++) {
						String column = columnNames[i];
						String value = cursor.getString(i);
						map.put(column, value);
					} // end of for (column)

					lists.add(map);
				} // end of for (row)
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return lists;
	}

	/**
	 * 데이터베이스 사용 가능 여부
	 * 
	 * @return
	 */
	public boolean verify() {
		return (sqlite != null);
	}

	public String joinKey() {
		return "";
	}

	public List<T> join(TABLE<?> b) {

		String sql = "SELECT * FROM " + getTableName() + " a INNER JOIN " + b.getTableName() + " b ON a." + joinKey()
				+ "=b." + b.joinKey() + " WHERE a." + sqlwhere();

		return select(sql);
	}
}
