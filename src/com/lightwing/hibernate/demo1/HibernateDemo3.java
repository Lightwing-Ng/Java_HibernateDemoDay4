package com.lightwing.hibernate.demo1;

import java.util.List;

import com.lightwing.hibernate.domain.Customer;
import com.lightwing.hibernate.utils.HibernateUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

/**
 * SQL查询
 *
 * @author Lightwing Ng
 */
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
