DBUtils3  - database utility version 3
======================================

Javaでデータベースを操作する際によく使うユーティリティです。
主にしたの2つのクラス、アノテーションを使います。

+   DBConnectionUtil:
    PreparedStatementを使いやすく!
    SELECT結果をBeanに自動変換!
    SELECT結果をMapにセット!

+   Column:
    JavaBeansとデータベースのカラムをつなぐアノテーション


DBConnectionUtilの使い方
----------------------
### DBConnectionUtilのインスタンス化 ###
コンストラクタにConnectionを渡します。
 
    try (DBConnectionUtil conn = new DBConnectionUtil(connection)) {
    }
 
### PreparedStatementのプリコンパイル ###
 
    conn.prepare("select * from table where id = ?");
    conn.prepare("insert into table values (?, ?, ?)");

### 参照の実行 ###

    // ColumnアノテーションをBeanに付与しておく必要があります（後述） 
    List<XXXXBean> result = conn.executeQuery(XXXXBean.class, 3);
    System.out.println(result.get(0).getId());
    
    // データベースのカラム名を直接指定しても取得できます
    List<Map<String, String>> result = conn.executeQuery(3);
    System.out.println(result.get(0).get("id"));


### 更新の実行 ###

    // 戻り値は更新件数
    int count = conn.executeUpdate(3, 4, "data");


### トランザクション制御 ###

    conn.commit();
    conn.rollback();


Columnアノテーションの使い方（Bean定義編）
--------------------------------
ColumnアノテーションでDatabaseのカラム名を指定することで、SELECT結果から自動でBeanを生成します。
Beanを定義する際は、以下のJavaBean仕様に従う必要があります。

- publicで引数無しのコンストラクタが必要
  - コピーコンストラクタ等を定義したいときは、引数無しのコンストラクタもあわせて用意してください
- メソッドの命名規則にしたがっていること（getter/setter）

    public class XXXXBean {
    
        @Column("id")
        private String beanId;
        
        public void setBeanId(String beanId) {
            this.beanId = beanId;
        }
        public String getBeanId() {
            return this.beanId;
        }
    }


License
-------
Copyright &copy; 2014 tamura shingo
Licensed under the [MIT License][MIT].

[MIT]: http://www.opensource.org/licenses/mit-license.php
    
