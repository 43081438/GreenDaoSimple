# GreenDao3.0 简单使用
GreenDao3.0+配置和使用
一.GreenDao介绍

GreenDAO升级到了3.0+版本，不需要java项目了
该版本采用注解的方式通过编译生成Java数据对象和DAO对象，配置更简单。认识GreenDao之前必须知道ORM（Object Relation Mapping对象关系映射），其表现形式就是通过GreenDao将数据库和Bean对象关联起来。

二.GreenDao配置

1.在项目的build.gradle下配置插件依赖 classpath'org.greenrobot:greendao-gradle-plugin:3.2.1'


2.在moudle下的build.gradle文件中 申明插件以及配置greendao（可以不配置）
(1)apply plugin: 'org.greenrobot.greendao'
(2)配置GreenDao生成文件的路径
//greendao配置
greendao {
    /**GreenDao 实体类包所在文件夹*/
    targetGenDir 'src/main/java'

    /**版本号，升级时可配置*/
    schemaVersion 1

    /**GreenDao输出dao数据库操作类实体类所在文件夹*/
    daoPackage 'qzy.greendao.com.greendaosimple.dao'
}
 
(3)并在dependencies中添加compile
 compile 'org.greenrobot:greendao:3.2.0'

 
三.GreenDao使用

1.编写实体类:User类，使用greendao @Entity注解

注：自增长的主键ID类型必须用包装类Long，否则添加多个数据时候会报UNIQUE constraint failed
@Entity
public class User {
    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String age;
    private String address;
    private String sex;
    private String phone;
    @Property(nameInDb = "password")
    private String psw;
}
 GreenDAO 注解说明
1.实体@Entity注解

schema：告知GreenDao当前实体属于哪个schema
active：标记一个实体处于活动状态，活动实体有更新、删除和刷新方法
nameInDb：在数据中使用的别名，默认使用的是实体的类名
indexes：定义索引，可以跨越多个列
createInDb：标记创建数据库表

2.基础属性注解
@Id :主键 Long型，可以通过@Id(autoincrement = true)设置自增长
@Property：设置一个非默认关系映射所对应的列名，默认是的使用字段名 举例：@Property (nameInDb="name")
@NotNul：设置数据库表当前列不能为空
@Transient ：添加次标记之后不会生成数据库表的列
3.索引注解
@Index：使用@Index作为一个属性来创建一个索引，通过name设置索引别名，也可以通过unique给索引添加约束
@Unique：向数据库列添加了一个唯一的约束

4.关系注解
@ToOne：定义与另一个实体（一个实体对象）的关系
@ToMany：定义与多个实体对象的关系
2.编译项目

build之后greendao插件会为所有带有该注解的实体生成Dao文件，以及DaoManager与DaoSession，默认生成目录为build/generated/source ,如果我们在gradle脚本中配置了，则会生成在我们的配置目录。

不配置，则在默认目录(build/generated/source/greendao)下生成dao文件

2.1 扩展（装逼模式生成Dao文件）

这种装逼方式其实和上面Build项目最后实现的效果是一样的，只不过这一步采用自定义Task使用命令实现。
步骤就是在项目的build.gradle中不在指定greenDao的配置信息甚至连引入greendao插件也可以放到这里边，然后另写一个gradle文件来存放这些配置信息

之后在项目的build.gradle中引入该文件

最后我们就可以愉快执行gradle来生成文件了

 
3.数据库创建

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
        //创建数据库shop.db"
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "simple.db", null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        daoSession = daoMaster.newSession();
    }
    public static DaoSession getDaoInstant() {
        return daoSession;
    }
}
GreenDao已经将我们的数据库创建缩成几句话，代码会自动将Bean对象创建成表，不再是传统的手写SQL语句。这里的数据库创建只需要在Application中执行一次即可，这里对几个类进行解释
DevOpenHelper：创建SQLite数据库的SQLiteOpenHelper的具体实现
DaoMaster：GreenDao的顶级对象，作为数据库对象、用于创建表和删除表
DaoSession：管理所有的Dao对象，Dao对象中存在着增删改查等API
由于我们已经创建好了DaoSession和User的Bean对象，编译后会自动生成我们的UserDao对象，可通过DaoSession获得
UserDaodao = daoSession.getUserDao();
4.数据操作（增删改查）

（1）增
/**添加*/
public static boolean insertUser(User user) {
    BaseApplication.getDaoInstant().getUserDao().insert(user);
    return true;
}
（2）删
/**删除数据*/
public static void deleteByID(long id) {
    BaseApplication.getDaoInstant().getUserDao().deleteByKey(id);
}

/** 删除全部数据*/
public static void deleteAll() {
    BaseApplication.getDaoInstant().getUserDao().deleteAll();
}
（3）改
/**更新数据*/
public static void updateUser(User shop) {
    BaseApplication.getDaoInstant().getUserDao().update(shop);
}
（4）查
/** 查询全部数据*/
public static List<User> queryAll() {
    return BaseApplication.getDaoInstant().getUserDao().loadAll();
}

/**查询条件为用户名*/
public static List<User> queryUser(String userName) {
    return BaseApplication.getDaoInstant().getUserDao().queryBuilder()
            .where(UserDao.Properties.Name.eq(userName)).list();
}
效果很明显，GreenDao的封装更加短小精悍，语义明朗，下面对GreenDao中Dao对象其他API的介绍

增加单个数据
getUserDao().insert(shop);
getUserDao().insertOrReplace(shop);
增加多个数据
getUserDao().insertInTx(shopList);
getUserDao().insertOrReplaceInTx(shopList);
查询全部
List< Shop> list = getUserDao().loadAll(); List< Shop> list = getUserDao().queryBuilder().list();
查询附加单个条件
.where()
.whereOr()
查询附加多个条件
.where(, , ,)
.whereOr(, , ,)
查询附加排序
.orderDesc()
.orderAsc()
List <User> items = userDao.queryBuilder()
.where(Properties.FirstName.eq("Joe"))
.orderAsc(Properties.LastName)
.list();
查询限制当页个数
.limit()
查询总个数
.count()
修改单个数据
getUserDao().update(shop);
修改多个数据
getUserDao().updateInTx(shopList);
删除单个数据
getTABUserDao().delete(user);
删除多个数据
getUserDao().deleteInTx(userList);
删除数据ByKey
getTABUserDao().deleteByKey();
