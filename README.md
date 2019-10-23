DBUtils3  - database utility version 3
======================================

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.tamurashingo.dbutil3/dbutil3/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.tamurashingo.dbutil3/dbutil3)
[![Build Status](https://travis-ci.org/tamurashingo/dbutils3.svg?branch=master)](https://travis-ci.org/tamurashingo/dbutils3)
[![Coverage Status](https://coveralls.io/repos/tamurashingo/dbutils3/badge.svg?branch=master&service=github)](https://coveralls.io/github/tamurashingo/dbutils3?branch=master)
[![License: MIT](http://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

This is database utility.

following class, annotation are used.

- DBConnectionUtil
  - easy to use PreparedStatement!
  - auto convert from search result to bean!
  - auto convert from search result to java.util.Map!

- Column
  - annotation that connect from bean field to database column name.


Usage
-----
add to pom.xml

```xml
<dependency>
    <groupId>com.github.tamurashingo.dbutil3</groupId>
    <artifactId>dbutil3</artifactId>
    <version>0.2.1-SNAPSHOT</version>
</dependency>
```


How to use DBConnectionUtil
---------------------------
### instantiate DBConnectionUtil ###


```java
try (DBConnectionUtil conn = DBConnectionUtil(connection)) {
}
```


### precompile SQL (using PreparedStatement) ###

```java
conn.prepareWithParam("select * from table where id = :id");
conn.prepareWithParam("insert into table values(:id, :count, :text)");
```

or

```java
conn.prepare("select * from table where id = ?");
conn.prepare("insert into table values (?, ?, ?)");
```

### execute query ###

```java
Param p = new Param();
p.put("id", 3);
// Generate bean
List<XXXXBean> result = conn.executeQueryWithParam(XXXXBean.class, param);
System.out.println(result.get(0).getId());

Param p = new Param();
p.put("id", 3);
// Generate Map
List<Map<String, String>> result = conn.executeQueryWithParam(param);
System.out.println(result.get(0).get("id"));
```

or

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
Param p = new Param();
p.put("id", 3);
p.put("count", 4);
p.put("text", "data");
// result is the number of updated
int count = conn.executeUpdateWithparam(p);
```

or

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

### AutoBinding ###
We can use auto-binding feature.
This feature generates Database Column Name from Bean Property name. (camel-case to snake-case)
If property has column annotation, column value is used in priority.


```java
@AutoBiding
public class TestBean {
    private String testId;
    private String camelCaseValue;
    private String snake_case_value;
    @Column("original_string")
    private String testValue;
    ...
}
```

This code is equivalent to:

```java
public class TestBean {
    @Column("test_id")
    private String testId;
    @Column("camel_case_value")
    private String camelCaseValue;
    @Column("snake_case_value")
    private String snake_case_value;
    @Column("original_string")
    private String testValue;
    ...
}
```


Example with JDBI
-----------------
You can implement JDBI Mapper easily.

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
Copyright &copy; 2014-2016 tamura shingo
Licensed under the [MIT License][MIT].

[MIT]: http://www.opensource.org/licenses/mit-license.php

