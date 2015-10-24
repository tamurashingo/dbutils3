DBUtils3  - database utility version 3
======================================

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.tamurashingo.dbutil3/dbutil3/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.tamurashingo.dbutil3/dbutil3)
[![Build Status](https://travis-ci.org/tamurashingo/dbutils3.svg?branch=master)](https://travis-ci.org/tamurashingo/dbutils3)
[![Coverage Status](https://coveralls.io/repos/tamurashingo/dbutils3/badge.svg?branch=master&service=github)](https://coveralls.io/github/tamurashingo/dbutils3?branch=master)
[![License: MIT](http://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

Javaでデータベースを操作する際によく使うユーティリティです。
主にしたの2つのクラス、アノテーションを使います。

- DBConnectionUtil:
  - PreparedStatementを使いやすく!
  - SELECT結果をBeanに自動変換!
  - SELECT結果をMapにセット!

- Column:
  - JavaBeansとデータベースのカラムをつなぐアノテーション


使い方
------
pom.xmlに追加します。

```xml
<dependency>
    <groupId>com.github.tamurashingo.dbutil3</groupId>
    <artifactId>dbutil3</artifactId>
    <version>0.2.0</version>
</dependency>
```


DBConnectionUtilの使い方
----------------------
### DBConnectionUtilのインスタンス化 ###
コンストラクタにConnectionを渡します。
 
```java
try (DBConnectionUtil conn = new DBConnectionUtil(connection)) {
}
```
 
### PreparedStatementのプリコンパイル ###
 
```java
conn.prepareWithParam("select * from table where id = :id");
conn.prepareWithParam("insert into table values(:id, :count, :text)");
```

または

```java
conn.prepare("select * from table where id = ?");
conn.prepare("insert into table values (?, ?, ?)");
```

`:id`のようにコロン+変数名でパラメータを指定することができます。
`:id`が複数個あった場合はすべて同じ値が指定されます。

`?`を使った指定も可能です。これは通常の`PreparedStatement`と同じ仕様です。


### 参照の実行 ###

```
Param p = new Param();
p.put("id", 3);
// ColumnアノテーションをBeanに付与しておく必要があります（後述） 
List<XXXXBean> result = conn.executeQueryWithParam(XXXXBean.class, param);
System.out.println(result.get(0).getId());

Param p = new Param();
p.put("id", 3);
// データベースのカラム名を直接指定しても取得できます
List<Map<String, String>> result = conn.executeQueryWithParam(param);
System.out.println(result.get(0).get("id"));
```

または

```java
// ColumnアノテーションをBeanに付与しておく必要があります（後述） 
List<XXXXBean> result = conn.executeQuery(XXXXBean.class, 3);
System.out.println(result.get(0).getId());

// データベースのカラム名を直接指定しても取得できます
List<Map<String, String>> result = conn.executeQuery(3);
System.out.println(result.get(0).get("id"));
```


### 更新の実行 ###

```java
Param p = new Param();
p.put("id", 3);
p.put("count", 4);
p.put("text", "data");
// 戻り値は更新件数
int count = conn.executeUpdateWithparam(p);
```

または

```java
// 戻り値は更新件数
int count = conn.executeUpdate(3, 4, "data");
```


### トランザクション制御 ###

```java
conn.commit();
conn.rollback();
```


Columnアノテーションの使い方（Bean定義編）
--------------------------------
ColumnアノテーションでDatabaseのカラム名を指定することで、SELECT結果から自動でBeanを生成します。
Beanを定義する際は、以下のJavaBean仕様に従う必要があります。

- publicで引数無しのコンストラクタが必要
  - コピーコンストラクタ等を定義したいときは、引数無しのコンストラクタもあわせて用意してください
- メソッドの命名規則にしたがっていること（getter/setter）

```java
public class XXXXBean {

    @Column("id")
    private String beanId;

    public XXXXBean() {
    }

    public XXXXBean(XXXXBean bean) {
        this.beanId = bean.beanId;
    }
    
    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }
    public String getBeanId() {
        return this.beanId;
    }
}
```


Example with JDBI
-----------------
BeanBuilderを使うことでJDBIのMapperを楽に定義することができます。

```java
public class UserBean implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column("id")
    private int id;
    @Column("username")
    private String username;
    @Column("password")
    private String password;

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return this.id;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return this.username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return this.password;
    }
}

public interface UserDAO {

    /**
     * get user-info with id.
     *
     * @param id id
     * @return user-info
     */
    @SingleValueResult(UserBean.class)
    @SqlQuery(
              " select "
            + "   id, "
            + "   username, "
            + "   password "
            + " from "
            + "   user "
            + " where "
            + "   id = :id "
    )
    @Mapper(UserJdbiMapper.class)
    public Optional<UserBean> getUserById(@Bind("id") String id);
}

public class UserJdbiMapper implements ResultSetMapper<UserBean> {
    private BeanBuilder builder = new BeanBuilder(UserBean.class);

    @Override
    public UserBean map(int inex, ResultSet rs, StatementContext ctx) throws SQLExcetion {
        try {
            UserBean bean = builder.build(rs);
            return bean;
        }
        catch (BeanBuilderException ex) {
            throw new SQLException(ex);
        }
    }
}

```


License
-------
Copyright &copy; 2014-2015 tamura shingo
Licensed under the [MIT License][MIT].

[MIT]: http://www.opensource.org/licenses/mit-license.php
