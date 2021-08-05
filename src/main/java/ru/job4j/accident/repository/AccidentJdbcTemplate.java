package ru.job4j.accident.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.job4j.accident.model.Accident;
import ru.job4j.accident.model.AccidentType;
import ru.job4j.accident.model.Rule;

import java.sql.PreparedStatement;
import java.util.List;

//@Repository
public class AccidentJdbcTemplate {
    private final JdbcTemplate jdbc;

    public AccidentJdbcTemplate(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void save(Accident accident, String[] ids) {
        if (accident.getId() == 0) {
            create(accident, ids);
        } else {
            update(accident, ids);
        }
    }

    private void create(Accident accident, String[] ids) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into accident(name, text, address, type_id) values(?, ?, ?, ?)",
                    new String[]{"id"});
            ps.setString(1, accident.getName());
            ps.setString(2, accident.getText());
            ps.setString(3, accident.getAddress());
            ps.setInt(4, accident.getType().getId());
            return ps;
        }, keyHolder);
        accident.setId((int) keyHolder.getKey());
        for (String id : ids) {
            jdbc.update("insert into accident_rule (accident_id, rule_id) " +
                            "values (?, ?) ",
                    accident.getId(),
                    Integer.parseInt(id));
        }
    }

    private void update(Accident accident, String[] ids) {
        jdbc.update("update accident set name = ?, address = ?, " +
                        "text = ?, type_id = ? WHERE ID = ?",
                accident.getName(),
                accident.getAddress(),
                accident.getText(),
                accident.getType().getId(),
                accident.getId());
        jdbc.update("delete from accident_rule WHERE accident_id = ?",
                accident.getId());
        if (ids != null) {
            for (String id : ids) {
                jdbc.update("insert into accident_rule (accident_id, rule_id) " +
                                "values (?, ?) ",
                        accident.getId(),
                        Integer.parseInt(id));
            }
        }
    }

    public List<Accident> getAll() {
        return jdbc.query("SELECT " +
                        "acc.id," +
                        "acc.name," +
                        "acc.address," +
                        "acc.text," +
                        "acc.type_id as type_id," +
                        "types.name as type_name " +
                        "from accident as acc " +
                        "LEFT JOIN TYPE AS TYPES " +
                        "ON acc.type_id = TYPES.id ",
                (rs, row) -> {
                    Accident accident = new Accident();
                    accident.setId(rs.getInt("id"));
                    accident.setName(rs.getString("name"));
                    accident.setText(rs.getString("text"));
                    accident.setAddress(rs.getString("address"));
                    AccidentType accidentType = new AccidentType();
                    accidentType.setId(rs.getInt("id"));
                    accidentType.setName(rs.getString("type_name"));
                    accident.setType(accidentType);
                    setRulesToAccident(accident);
                    return accident;
                });
    }

    private void setRulesToAccident(Accident a) {
        List<Rule> rules = jdbc.query("SELECT " +
                        "rules.id," +
                        "rules.name " +
                        "FROM accident_rule as ar " +
                        "INNER JOIN rule as rules " +
                        "ON ar.rule_id = rules.id AND accident_id = ?",
                (resultSet, rowNum) -> {
                    Rule rule = new Rule();
                    rule.setId(resultSet.getInt("id"));
                    rule.setName(resultSet.getString("name"));
                    return rule;
                }, a.getId());
        rules.forEach(a::addRule);
    }

    public List<AccidentType> getAllTypes() {
        return jdbc.query("select id, name from type",
                (rs, row) -> {
                    AccidentType accidentType = new AccidentType();
                    accidentType.setId(rs.getInt("id"));
                    accidentType.setName(rs.getString("name"));
                    return accidentType;
                });
    }

    public List<Rule> getAllRules() {
        return jdbc.query("select id, name from rule",
                (rs, row) -> {
                    Rule rule = new Rule();
                    rule.setId(rs.getInt("id"));
                    rule.setName(rs.getString("name"));
                    return rule;
                });
    }

    public Accident findById(int id) {
        String q = "select acc.id, " +
                "acc.name, " +
                "acc.text, " +
                "acc.address, " +
                "type.id as type_id, " +
                "type.name as type_name " +
                "from accident as acc " +
                "left join type as type " +
                "on acc.type_id = type.id WHERE acc.id = ?";

        Accident accident = jdbc.queryForObject(q, (resultSet, rowNum) -> {
            Accident thisAcc = new Accident();
            thisAcc.setId(resultSet.getInt("id"));
            thisAcc.setName(resultSet.getString("name"));
            thisAcc.setText(resultSet.getString("text"));
            thisAcc.setAddress(resultSet.getString("address"));
            AccidentType accidentType = new AccidentType();
            accidentType.setId(resultSet.getInt("type_id"));
            accidentType.setName(resultSet.getString("type_name"));
            thisAcc.setType(accidentType);
            return thisAcc;
        }, id);

        return accident;
    }
}