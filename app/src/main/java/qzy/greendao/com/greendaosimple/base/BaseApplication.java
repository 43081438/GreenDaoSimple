package qzy.greendao.com.greendaosimple.base;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import qzy.greendao.com.greendaosimple.dao.DaoMaster;
import qzy.greendao.com.greendaosimple.dao.DaoSession;

/**
 * 作者：quzongyang
 *
 * 创建时间：2017/8/14
 *
 * 类描述：
 */

public class BaseApplication extends Application {

    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        //配置数据库
        setupDatabase();
    }

    /**
     * 配置数据库
     */
    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "shop.db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoInstant() {
        return daoSession;
    }
}
