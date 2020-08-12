package com.excilys.formation.java.cdb.persistence.daos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.excilys.formation.java.cdb.models.User;

@Repository
public class UserDAO {

    private static final String SQL_SELECT_ALL = "FROM User";

    private static final String SQL_SELECT_WITH_USERNAME =
            "FROM User user WHERE user.username = :username";

    private static Logger logger = LoggerFactory.getLogger(UserDAO.class);

    private SessionFactory sessionFactory;

    @Autowired
    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<User> getAll() {
        List<User> userList = new ArrayList<User>();
        try (Session session = sessionFactory.openSession()) {
            userList = session.createQuery(SQL_SELECT_ALL, User.class).list();
        } catch (HibernateException e) {
            logger.error("error when getting all users", e);
        }
        return userList;
    }

    public void create(User user) {
        if (user != null) {
            try (Session session = sessionFactory.openSession()) {
                session.save(user);
            }
        } else {
            logger.error("the user is null");
        }
        logger.info(user.toString());
    }

    public Optional<User> findByUsername(String username) {
        Optional<User> result = Optional.empty();
        if (username != null && !username.isEmpty()) {
            try (Session session = sessionFactory.openSession()) {
                Query<User> query = session
                        .createQuery(SQL_SELECT_WITH_USERNAME, User.class)
                        .setParameter("username", username);
                result = Optional.ofNullable(query.uniqueResult());
            }
        } else {
            logger.error("username is null");
        }
        return result;
    }
}
