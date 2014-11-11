DBUtils3  - database utility version 3
======================================

This is database utility.

following class, annotation are used.

- DBConnectionUtil
  - easy to use PreparedStatement!
  - auto convert from search result to bean!
  - auto convert from search result to java.util.Map!

- Column
  - annotation that connect from bean field to database column name.


How to use DBConnectionUtil
---------------------------
### instantiate DBConnectionUtil ###


```java
try (DBConnectionUtil conn = DBConnectionUtil(connection)) {
}
```


### precompile SQL (using PreparedStatement) ###

```java
conn.prepare("select * from table where id = ?");
conn.prepare("insert into table values (?, ?, ?)");
```


### execute query ###

```java
// Generate bean
List<XXXXBean> result = conn.executeQuery(XXXXBean.class, 3);
System.out.println(result.get(0).getId());

// Generate Map
List<Map<String, String>> result = conn.executeQuery(3);
System.out.println(result.get(0).get("id"));
```


### execute update ###

```java
// result is the number of updated
int count = conn.executeUpdate(3, 4, "data");
```

### transaction management ###

```java
conn.commit();
conn.rollback();
```



How to use Column annotation (definition of bean)
-------------------------------------------------
You can generate bean automatically when specify a database column name
to the bean field with Column annotation.
The bean class must have following required conventions.

- The class must have a public default constructor (with no arguments).
- The class properties must be accessible using get, set, and so on.

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


License
-------
Copyright &copy; 2014 tamura shingo
Licensed under the [MIT License][MIT].

[MIT]: http://www.opensource.org/licenses/mit-license.php
