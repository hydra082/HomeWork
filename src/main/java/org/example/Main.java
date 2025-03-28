package org.example;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Random;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(User.class);
    public static void main(String[] args) {
        Configuration config = new Configuration()
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Game.class)
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.hbm2ddl.auto", "create")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.format_sql", "true")
                .setProperty("hibernate.cache.use_second_level_cache", "true")
                .setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory")
                .setProperty("hibernate.cache.use_query_cache", "true")
                .setProperty("hibernate.generate_statistics", "true");


        SessionFactory sessionFactory = config.buildSessionFactory();
        Statistics stats = sessionFactory.getStatistics();
        stats.setStatisticsEnabled(true);

        try (Session session = sessionFactory.openSession()) {
                User user = new User();
                user.setName("John");
                user.addGameList(new Game("one"));
                user.addGameList(new Game("one1"));
                user.addGameList(new Game("one2"));
                User user1 = new User();
                user.setName("John1");
                user.addGameList(new Game("two"));
                user.addGameList(new Game("two1"));
                user.addGameList(new Game("two2"));
                User user2 = new User();
                user.setName("John2");
                user.addGameList(new Game("three"));
                user.addGameList(new Game("three1"));
                user.addGameList(new Game("three2"));

                session.save(user);
                session.save(user1);
                session.save(user2);

            //Демонстрация проблемы N+1
            System.out.println("-----------------------Problem N+1--------------------");

            List<User> userList = session.createQuery("SELECT a FROM User a ").list();

            System.out.println("-----------------------JOIN FETCH--------------------");
            List<User> userQueryJoinFetch = session.createQuery("SELECT a FROM User a JOIN FETCH a.gameList").list();

            System.out.println("-----------------------BatchSize--------------------");
            Query<User> userQueryBatchSize = session.createQuery("SELECT a FROM User a");
            List<User> userListBatchSize = userQueryBatchSize.list();


System.out.println("-----------------------Entity Graph--------------------");

System.out.println("-----------------------FetchMode--------------------");




        }
        sessionFactory.close();
    }
}