package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityGraph;
import java.util.List;

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


        try (SessionFactory sessionFactory = config.buildSessionFactory()) {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                User user = new User();
                user.setName("John");
                user.addGameList(new Game("Game1"));
                user.addGameList(new Game("Game2"));
                user.addGameList(new Game("Game3"));

                User user1 = new User();
                user1.setName("Alice");
                user1.addGameList(new Game("Game4"));
                user1.addGameList(new Game("Game5"));

                session.persist(user);
                session.persist(user1);
                session.getTransaction().commit();
            }

            System.out.println("-----------------------Problem N+1--------------------");
            try (Session session = sessionFactory.openSession()) {
                List<User> userList = session.createQuery("SELECT u FROM User u", User.class).list();
                for (User u : userList) {
                    logger.info("User: " + u.getName() + ", Games: " + u.getGameList().size());
                }
            }

            System.out.println("-----------------------JOIN FETCH--------------------");
            try (Session session = sessionFactory.openSession()) {
                List<User> userList = session.createQuery(
                        "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.gameList", User.class
                ).list();
                for (User u : userList) {
                    logger.info("User: " + u.getName() + ", Games: " + u.getGameList().size());
                }
            }

            System.out.println("-----------------------BatchSize--------------------");
            try (Session session = sessionFactory.openSession()) {
                List<User> userList = session.createQuery("SELECT u FROM User u", User.class).list();
                for (User u : userList) {
                    logger.info("User: " + u.getName() + ", Games (Batch): " + u.getGamesListBatch().size());
                }
            }

            System.out.println("-----------------------Entity Graph--------------------");
            try (Session session = sessionFactory.openSession()) {
                EntityGraph<User> graph = session.createEntityGraph(User.class);
                graph.addAttributeNodes("gameList");
                List<User> userList = session.createQuery("SELECT u FROM User u", User.class)
                        .setHint("javax.persistence.fetchgraph", graph)
                        .list();
                for (User u : userList) {
                    logger.info("User: " + u.getName() + ", Games: " + u.getGameList().size());
                }
            }

            System.out.println("-----------------------FetchMode.SUBSELECT--------------------");
            try (Session session = sessionFactory.openSession()) {
                List<User> userList = session.createQuery("SELECT u FROM User u", User.class).list();
                for (User u : userList) {
                    logger.info("User: " + u.getName() + ", Games (Subselect): " + u.getGameListFetch().size());
                }
            }
        }
    }
}