package ru.job4j.accident.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import ru.job4j.accident.model.Accident;
import ru.job4j.accident.model.AccidentType;
import ru.job4j.accident.model.Rule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

@Repository
public class AccidentHibernate {
    private final SessionFactory sf;

    public AccidentHibernate(SessionFactory sf) {
        this.sf = sf;
    }

    public Accident save(Accident accident, String[] ids) {
        return tx(
                session -> {
                    accident.setRules(getRulesByIds(ids));
                    if (accident.getId() == 0) {
                        session.save(accident);
                    } else {
                        session.update(accident);
                    }
                    return accident;
                }
        );
    }

    private Set<Rule> getRulesByIds(String[] ids) {
        Set<Rule> rules = new HashSet<Rule>();
        int[] intRuleIds = Arrays.stream(ids).mapToInt(Integer::parseInt).toArray();
        Integer[] integerIds = IntStream.of(intRuleIds).boxed().toArray(Integer []::new);
        try (Session session = sf.openSession()) {
            var q = session.createQuery("FROM Rule WHERE id IN (:ids)");
            q.setParameterList("ids", integerIds);
            var rulesList = q.list();
            if (rulesList.size() > 0) {
                rules.addAll(rulesList);
            }
        }
        return rules;
    }

    public List<Accident> getAll() {
        try (Session session = sf.openSession()) {
            return session
                    .createQuery("from Accident", Accident.class)
                    .list();
        }
    }

    public List<AccidentType> getAllTypes() {
        try (Session session = sf.openSession()) {
            return session
                    .createQuery("from AccidentType", AccidentType.class)
                    .list();
        }
    }

    public List<Rule> getAllRules() {
        try (Session session = sf.openSession()) {
            return session
                    .createQuery("from Rule", Rule.class)
                    .list();
        }
    }

    public Accident findById(int id) {
        try (Session session = sf.openSession()) {
            Accident result = null;
            var q = session.createQuery("from Accident WHERE id = :id", Accident.class);
            q.setParameter("id", id);
            List<Accident> foundedAccidents = q.list();
            if (foundedAccidents.size() > 0) {
                result = foundedAccidents.get(0);
            }
            return result;
        }
    }

    private <T> T tx(final Function<Session, T> command) {
        final Session session = sf.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}