package com.runrunfast.homegym.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* SQliteOpenHelper是一个抽象类，来管理数据库的创建和版本的管理 */
public class DBOpenHelper extends SQLiteOpenHelper {
	// 数据库名
	private static final String DATABASE_NAME = "homegym.db";
	// 数据库版本
	private static final int DATABASE_VERSION = 1;

	public DBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String courseTable = CourseDao.getInstance().getCourseTableSqlString();
		db.execSQL(courseTable);
		
		String myCourseTable = MyCourseDao.getInstance().getCourseTableSqlStr();
		db.execSQL(myCourseTable);
		
		String actionTable = ActionDao.getInstance().getActionTableSqlStr();
		db.execSQL(actionTable);
//		
		String myFinishTable = MyFinishDao.getInstance().getFinishTableSqlStr();
		db.execSQL(myFinishTable);
		
		//如果表不存在就创建表
		String sql = "CREATE TABLE IF NOT EXISTS "
				+ "downloadlog(id integer primary key autoincrement,downpath varchar(100),"
				+ "threadid integer,downlength integer);";
		db.execSQL(sql);//执行语句
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS downloadlog;";
		db.execSQL(sql);
	}
}