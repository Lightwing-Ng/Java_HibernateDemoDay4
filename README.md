## 第1章 Hibernate_day04 笔记

### 1.1 上次课的内容回顾

### 1.2 Hibernate的查询的方式

​	在 Hibernate 中提供了很多种的查询的方式。Hibernate 共提供了五种查询方式。

#### 1.2.1 Hibernate 的查询方式：`OID` 查询

OID 检索：Hibernate 根据对象的 `OID`（主键）进行检索

##### 1.2.1.1 使用 `get` 方法

```java
Customer customer = session.get(Customer.class, 1l);
```

##### 1.2.1.2 使用 `load` 方法

```java
Customer customer = session.load(Customer.class, 1l);
```

#### 1.2.2 Hibernate 的查询方式：对象导航检索

​	对象导航检索：Hibernate 根据一个已经查询到的对象，获得其关联的对象的一种查询方式。

```java
LinkMan linkMan = session.get(LinkMan.class, 1l);
Customer customer  = linkMan.getCustomer();
Customer customer = session.get(Customer.class, 2l);
Set<LinkMan> linkMans = customer.getLinkMans();
```
#### 1.2.3 Hibernate 的查询方式：HQL 检索（单表演示）

​	HQL 查询：Hibernate Query Language，Hibernate 的查询语言，是一种面向对象的方式的查询语言，语法类似 SQL。通过 session.createQuery()，用于接收一个 HQL 进行查询方式。

##### 1.2.3.1 初始化一些数据

```java
@Test
public void demo1() {
    Session session = HibernateUtils.getCurrentSession();
    Transaction tx = session.beginTransaction();
    // 创建一个客户
    Customer customer = new Customer();
    customer.setCust_name("Dominic Ortiz");
    
    for (int i = 1; i <= 10; i++) {
        LinkMan linkMan = new LinkMan();
        linkMan.setLkm_name("Bridget Davidson" + i);
        linkMan.setCustomer(customer);
        customer.getLinkMans().add(linkMan);
        session.save(linkMan);
    }
    
    session.save(customer);
    tx.commit();
}
```

##### 1.2.3.2 HQL 的简单查询

```java
@Test
public void demo2() {
    Session session = HibernateUtils.getCurrentSession();
    Transaction tx = session.beginTransaction();
    // 简单的查询
    Query query = session.createQuery("FROM Customer");
    List<Customer> list = query.list();
    // sql中支持*号的写法：select * from cst_customer; 但是在HQL中不支持*号的写法。

    for (Customer customer : list)
        System.out.println(customer);
        
    tx.commit();
}
```

##### 1.2.3.3 HQL 的别名查询

```java
@Test
public void demo3() {
    Session session = HibernateUtils.getCurrentSession();
    Transaction tx = session.beginTransaction();
    // 别名的查询
    Query query = session.createQuery("SELECT c FROM Customer c");
    List<Customer> list = query.list();
    
    for (Customer customer : list)
        System.out.println(customer);
        
    tx.commit();
}
```

##### 1.2.3.4 HQL 的排序查询

```java
@Test
public void demo4() {
    Session session = HibernateUtils.getCurrentSession();
    Transaction tx = session.beginTransaction();
    // 排序的查询
    // 默认情况
    // 设置降序排序 升序使用asc 降序使用desc
    List<Customer> list = session.createQuery("from Customer order by cust_id desc").list();
    for (Customer customer : list)
        System.out.println(customer);
        
    tx.commit();
}
```

##### 1.2.3.5 HQL 的条件查询

```java
@Test
public void demo5() {
    Session session = HibernateUtils.getCurrentSession();
    Transaction tx = session.beginTransaction();
    // 条件的查询
    // 一、按位置绑定：根据参数的位置进行绑定。
    // 一个条件
    Query query = session.createQuery("from Customer where cust_name = ?"
                                     );
    query.setParameter(0, "李兵");
    List<Customer> list = query.list();
    // 多个条件
    Query query = session.createQuery(
                      "from Customer where cust_source = ? and cust_name like ?");
    query.setParameter(0, "小广告");
    query.setParameter(1, "李%");
    List<Customer> list = query.list();
    // 二、按名称绑定
    Query query = session.createQuery("from Customer where cust_source = :aaa and cust_name like :bbb");
    // 设置参数:
    query.setParameter("aaa", "朋友推荐");
    query.setParameter("bbb", "李%");
    List<Customer> list = query.list();
    
    for (Customer customer : list)
        System.out.println(customer);
        
    tx.commit();
}
```

##### 1.2.3.6 HQL 的投影查询

​	投影查询：查询对象的某个或某些属性。

```java
@Test
public void demo6() {
    Session session = HibernateUtils.getCurrentSession();
    Transaction tx = session.beginTransaction();
    // 投影查询
    // 查询多个属性，但是我想封装到对象中。
    List<Customer> list = session.createQuery("select new Customer(cust_name,cust_source) from Customer").list();
    
    for (Customer customer : list)
        System.out.println(customer); 
    tx.commit();
}
```

##### 1.2.3.7 HQL 的分页查询

```java
@Test
public void demo7() {
    Session session = HibernateUtils.getCurrentSession();
    Transaction tx = session.beginTransaction();
    // 分页查询
    Query query = session.createQuery("from LinkMan");
    query.setFirstResult(20);
    query.setMaxResults(10);
    List<LinkMan> list = query.list();
    
    for (LinkMan linkMan : list)
        System.out.println(linkMan);
        
    tx.commit();@Test
public void demo8() {
    Session session = HibernateUtils.getCurrentSession();
    Transaction tx = session.beginTransaction();
    // 聚合函数的使用：count(),max(),min(),avg(),sum()
    Object object = session.createQuery("SELECT COUNT(*) FROM Customer").uniqueResult();
    System.out.println(object);
    // 分组统计：
    List<Object[]> list = session.createQuery("SELECT cust_source, COUNT(*) FROM Customer " +
                          "GROUP BY" +
                          " cust_source")
                          .list();
                          
    for (Object[] objects : list)
        System.out.println(Arrays.toString(objects));
        
    tx.commit();
}
}
```

##### 1.2.3.8 HQL 的分组统计查询

```java
@Test
public void demo8() {
    Session session = HibernateUtils.getCurrentSession();
    Transaction tx = session.beginTransaction();
    // 聚合函数的使用：count(),max(),min(),avg(),sum()
    Object object = session.createQuery("SELECT COUNT(*) FROM Customer").uniqueResult();
    System.out.println(object);
    // 分组统计：
    List<Object[]> list = session.createQuery("SELECT cust_source, COUNT(*) FROM Customer GROUP BY cust_source").list();
                          
    for (Object[] objects : list)
        System.out.println(Arrays.toString(objects));
        
    tx.commit();
}
```

##### 1.2.3.9 HQL 的多表查询

###### 1. SQL 的多表查询

连接查询
	交叉连接：笛卡尔积

```sql
select * from A ,B;
```

​	内连接: inner join (inner 可以省略)
		隐式内连接：

```sql
select * from A,B where A.id = B.aid;
```

​		显式内连接：

```sql
select * from A inner join B on A.id = B.aid;
```

​	外连接:
		左外连接: left outer join(outer 可以省略)

```sql
select * from A left outer join B on A.id= B.aid;
```

​		右外连接: right outer join(outer 可以省略)

```sql
select * from A right outer join B on A.id = B.aid;
```

子查询

###### 2.HQL 的多表查询

连接查询
	交叉连接
	内连接
		显示内连接
		隐式内连接
		迫切内连接
	外连接
		左外连接
		右外连接
		迫切左外连接

```java
@Test
public void demo9() {
    Session session = HibernateUtils.getCurrentSession();
    Transaction tx = session.beginTransaction();
    List<Customer> list = session.createQuery("SELECT DISTINCT c FROM Customer c INNER JOIN FETCH c.linkMans").list();// 通知hibernate，将另一个对象的数据封装到该对象中
                          
    for (Customer customer : list)
        System.out.println(customer);
    tx.commit();
}
```

#### 1.2.4 Hibernate 的查询方式：QBC 检索

​	QBC 查询：Query By Criteria，条件查询。是一种更加面向对象化的查询的方式。

##### 1.2.4.1 简单查询

```java
@Test
public void demo1() {
    Session session = HibernateUtils.getCurrentSession();
    Transaction tx = session.beginTransaction();
    // 获得Criteria的对象
    Criteria criteria = session.createCriteria(Customer.class);
    List<Customer> list = criteria.list();
    
    for (Customer customer : list)
        System.out.println(customer); 
    tx.commit();
}
```

##### 1.2.4.2 排序查询

```java
@Test
public void demo2() {
    Session session = HibernateUtils.getCurrentSession();
    Transaction tx = session.beginTransaction();
    // 排序查询
    Criteria criteria = session.createCriteria(Customer.class);
    criteria.addOrder(Order.desc("cust_id")); // 降序
    List<Customer> list = criteria.list();
    for (Customer customer : list)
        System.out.println(customer);
        
    tx.commit();
}
```

##### 1.2.4.3 分页查询

```java
@Test
public void demo3() {
    Session session = HibernateUtils.getCurrentSession();
    Transaction tx = session.beginTransaction();
    // 分页查询
    Criteria criteria = session.createCriteria(LinkMan.class);
    criteria.setFirstResult(10);
    criteria.setMaxResults(10);
    List<LinkMan> list = criteria.list();
    for (LinkMan linkMan : list)
        System.out.println(linkMan);
        
    tx.commit();
}
```

##### 1.2.4.4 条件查询

```java
@Test
public void demo4() {
    Session session = HibernateUtils.getCurrentSession();
    Transaction tx = session.beginTransaction();
    // 条件查询
    Criteria criteria = session.createCriteria(Customer.class);
    // 设置条件:
    /**
        =   eq
        >   gt
        >=  ge
        <   lt
        <=  le
        <>  ne
        like
        in
        and
        or
    */
    criteria.add(Restrictions.eq("cust_source", "小广告"));
    criteria.add(Restrictions.like("cust_name", "李%"));
    List<Customer> list = criteria.list();
    
    for (Customer customer : list)
        System.out.println(customer);
        
    tx.commit();
}
```

##### 1.2.4.5 统计查询

```java
@Test
public void demo5() {
    Session session = HibernateUtils.getCurrentSession();
    Transaction tx = session.beginTransaction();
    Criteria criteria = session.createCriteria(Customer.class);
    /**
        add: 普通的条件。where后面条件
        addOrder: 排序
        setProjection: 聚合函数和 group by having
    */
    criteria.setProjection(Projections.rowCount());
    Long num = (Long) criteria.uniqueResult();
    System.out.println(num);
    tx.commit();
}
```

##### 1.2.4.6 离线条件查询（SSH）`---DetachedCriteria`

```java
@Test
public void demo6() {
    DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Customer.class);
    detachedCriteria.add(Restrictions.like("cust_name", "李%"));
    Session session = HibernateUtils.getCurrentSession();
    Transaction transaction = session.beginTransaction();
    Criteria criteria = detachedCriteria.getExecutableCriteria(session);
    List<Customer> list = criteria.list();
    for (Customer customer : list)
        System.out.println(customer);
        
    transaction.commit();
}
```

#### 1.2.5 Hibernate的查询方式：SQL 检索

###### 1.2.5.1 SQL查询

​	SQL 查询：通过使用 SQL 语句进行查询

```java
public class HibernateDemo3 {
    @Test
    public void demo1() {
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();
        SQLQuery sqlQuery = session.createSQLQuery("SELECT * FROM cst_customer");
        sqlQuery.addEntity(Customer.class);
        List<Customer> list = sqlQuery.list();
        for (Customer customer : list) {
            System.out.println(customer);
        }
        tx.commit();
    }
}
```

### 1.3 Hibernate 的抓取策略（优化）

#### 1.3.1 延迟加载的概述

##### 1.3.1.1 什么是延迟加载

​	延迟加载：lazy（懒加载）。执行到该行代码的时候，不会发送语句去进行查询，在真正使用这个对象的属性的时候才会发送 SQL 语句进行查询。

##### 1.3.1.2 延迟加载的分类

###### 类级别的延迟加载

​	指的是通过 load 方法查询某个对象的时候，是否采用延迟。`session.load(Customer.class, 1l);`
	类级别延迟加载通过<class>上的lazy进行配置，如果让lazy失效
		将 lazy 设置为 false
		将持久化类使用 final 修饰
		`Hibernate. Initialize();`

###### 关联级别的延迟加载

​	指的是在查询到某个对象的时候，查询其关联的对象的时候，是否采用延迟加载。

```java
Customer customer = session.get(Customer.class, 1l);
customer.getLinkMans(); // 通过客户获得联系人的时候，联系人对象是否采用了延迟加载，称为是关联级别的延迟。
```

​	抓取策略往往会和关联级别的延迟加载一起使用，优化语句。

#### 1.3.2 抓取策略

##### 1.3.2.1 抓取策略的概述

###### 通过一个对象抓取到关联对象需要发送 SQL 语句，SQL 语句如何发送，发送成什么样格式通过策略进行配置。

- 通过 <set> 或者 <many-to-one> 上通过 fetch 属性进行设置
- fetch 和这些标签上的lazy如何设置优化发送的SQL语句

##### 1.3.2.2 <set>上的 fetch 和lazy

###### fetch：抓取策略，控制SQL语句格式

- select		默认值，发送普通的select语句，查询关联对象
- join		        发送一条迫切左外连接查询关联对象
- subselect        发送一条子查询查询其关联对象

###### lazy：延迟加载，控制查询关联对象的时候是否采用延迟

- true                 默认值，查询关联对象的时候，采用延迟加载
- false               查询关联对象的时候，不采用延迟加载
- extra               及其懒惰。

###### 在实际开发中，一般都采用默认值。如果有特殊的需求，可能需要配置join。

##### 1.3.2.3 <many-to-one>上的 fetch 和lazy

###### fetch	：抓取策略，控制SQL语句格式。

- select  	默认值，发送普通的 select 语句，查询关联对象。
- join         发送一条迫切左外连接。

###### lazy	：延迟加载，控制查询关联对象的时候是否采用延迟。

- proxy  	默认值，proxy具体的取值，取决于另一端的<class>上的lazy的值。
- false        查询关联对象，不采用延迟。
- no-proxy（不会使用）

###### 在实际开发中，一般都采用默认值。如果有特殊的需求，可能需要配置join。

#### 1.3.3 批量抓取

##### 1.3.3.1 什么是批量抓取

​	一批关联对象一起抓取，batch-size

##### 1.3.3.2 测试批量抓取

```java
public class HibernateDemo4 {
    @SuppressWarnings("unchecked")
    @Test
    /**
     * 获取客户的时候，批量抓取联系人
     * 在Customer.hbm.xml中set上配置batch-size
     */
    public void demo1() {
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();
        List<Customer> list = session.createQuery("FROM Customer").list();
        for (Customer customer : list) {
            System.out.println(customer.getCust_name());
            for (LinkMan linkMan : customer.getLinkMans())
                System.out.println(linkMan.getLkm_name());
        }
        tx.commit();
    }

    @SuppressWarnings("unchecked")
    @Test
    /**
     * 获取联系人的时候，批量抓取客户
     * * 在Customer.hbm.xml中<class>上配置
     */
    public void demo2() {
        Session session = HibernateUtils.getCurrentSession();
        Transaction tx = session.beginTransaction();
        List<LinkMan> list = session.createQuery("FROM LinkMan").list();
        for (LinkMan linkMan : list)
            System.out.println(linkMan.getLkm_name());
        tx.commit();
    }
}
```

