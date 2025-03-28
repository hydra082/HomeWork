package org.example;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.Statistics;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Configuration config = new Configuration()
                .addAnnotatedClass(User.class)
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
            for (int i = 0; i < 10; i++) {
                User user = new User();
                user.setName("John" + i);
                //user.addGameList(new Game("AS" +1));
                session.save(user);
            }

            //Демонстрация проблемы N+1
            Query<User> userQuery = session.createQuery("SELECT a FROM User a", User.class);
            List<User> userList = userQuery.list();
            for (User user : userList) {
                System.out.println(user.getGameList().size());
            }

            //Решение с JOIN FETCH
            List<User> users = session.createQuery("SELECT h FROM User h JOIN FETCH h.gameList", User.class).getResultList();
            for (User user : users) {
                System.out.println(user.getGameList().size());
            }

            //Решение с BatchSize
            for (User user : userList) {
                System.out.println(user.getGamesListBatch().size());
            }
            //Решение с Entity Graph
            for (User user : userList) {
                System.out.println(user.getGameList().size());
            }
            //Решение FetchMode
            for (User user : userList)
            {
                System.out.println(user.getGameListFetch().size());
            }

        }
        sessionFactory.close();
    }
}