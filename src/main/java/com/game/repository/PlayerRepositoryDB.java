package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
//        sessionFactory.;
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");
//        properties.put(Environment.SHOW_SQL, "true");
        properties.put(Environment.HBM2DDL_AUTO, "update");
        sessionFactory = new Configuration()
                .setProperties(properties)
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
//  NativeQuery, OFFSET, LIMIT
        try(Session session = sessionFactory.openSession()){
//            String skipRow = Integer.toString(pageNumber * pageSize);
//            String skipPageSize = Integer.toString(pageSize);
            NativeQuery<Player> playerNativeQuery = session.createNativeQuery("SELECT * FROM rpg.player ", Player.class);
//                    .setParameter("skipRow", skipRow)
//                    .setParameter("pageSize", skipPageSize);
            playerNativeQuery.setFirstResult(pageNumber*pageSize);
            playerNativeQuery.setMaxResults(pageSize);
           return  playerNativeQuery.list();
        }


    }

    @Override
    public int getAllCount() {
//        NamedQuery, через аннотации
        try (Session session = sessionFactory.openSession()){
            return  session.createNamedQuery("Player_GetAllCount", Player.class).list().size();

        }

    }

    @Override
    public Player save(Player player) {
//        commit
        try (Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.save(player);
            transaction.commit();
            return player;

        }

    }

    @Override
    public Player update(Player player) {
        // commit
        try (Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.merge(player);
            transaction.commit();
            return player;
        }



    }

    @Override
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()){
            Player player = session.find(Player.class, id);
            return Optional.ofNullable(player);
        }



    }

    @Override
    public void delete(Player player) {
        // commit

        try (Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.createQuery("delete  from Player where id = :id")
                    .setParameter("id", player.getId())
                    .executeUpdate();
            transaction.commit();

        }



    }

    @PreDestroy
    public void beforeStop() {
    sessionFactory.close();
    }
}