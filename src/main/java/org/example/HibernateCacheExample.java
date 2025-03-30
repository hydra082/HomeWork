package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;

public class HibernateCacheExample implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(HibernateCacheExample.class);

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

        try (SessionFactory sessionFactory = config.buildSessionFactory()) {
            Statistics stats = sessionFactory.getStatistics();
            stats.setStatisticsEnabled(true);

            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                User user = new User();
                user.setName("John");
                session.persist(user);
                session.getTransaction().commit();
            }

            System.out.println("\nКэш 1 уровня:");
            try (Session session = sessionFactory.openSession()) {
                logger.info("Первый запрос к User с ID=1");
                User user1 = session.get(User.class, 1L);
                System.out.println("User: " + user1.getName());

                logger.info("Второй запрос к User с ID=1 (из кэша 1 уровня)");
                User user2 = session.get(User.class, 1L);
                System.out.println("User: " + user2.getName());
            }

            System.out.println("\nКэш 2 уровня:");
            try (Session session1 = sessionFactory.openSession()) {
                logger.info("Первая сессия: загрузка User с ID=1");
                User user1 = session1.get(User.class, 1L);
                System.out.println("User: " + user1.getName());
                System.out.println("Попадания в кэш: " + stats.getSecondLevelCacheHitCount());
                System.out.println("Промахи в кэш: " + stats.getSecondLevelCacheMissCount());
            }

            try (Session session2 = sessionFactory.openSession()) {
                logger.info("Вторая сессия: загрузка User с ID=1 (из кэша 2 уровня)");
                User user2 = session2.get(User.class, 1L);
                System.out.println("User: " + user2.getName());
                System.out.println("Попадания в кэш: " + stats.getSecondLevelCacheHitCount());
                System.out.println("Промахи в кэш: " + stats.getSecondLevelCacheMissCount());
            }

            System.out.println("\nСтатистика:");
            System.out.println("Кэш 2 уровня - попадания: " + stats.getSecondLevelCacheHitCount());
            System.out.println("Кэш 2 уровня - промахи: " + stats.getSecondLevelCacheMissCount());
        }
    }
}
