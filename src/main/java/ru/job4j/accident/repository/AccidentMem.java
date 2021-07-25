package ru.job4j.accident.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.accident.model.Accident;
import ru.job4j.accident.model.AccidentType;
import ru.job4j.accident.model.Rule;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class AccidentMem {

    private final Map<Integer, Accident> accidents = new HashMap<>();
    private final Map<Integer, AccidentType> types = new HashMap<>();
    private final Map<Integer, Rule> rules = new HashMap<>();
    private final AtomicInteger size = new AtomicInteger();

    public AccidentMem() {
        init();
    }

    public List<Accident> getAccidents() {
        return new ArrayList<>(accidents.values());
    }

    public List<AccidentType> getAllTypes() {
        return new ArrayList<>(types.values());
    }

    public List<Rule> getAllRules() {
        return new ArrayList<>(rules.values());
    }

    public void create(Accident accident) {
        int id = size.incrementAndGet();
        accident.setId(id);
        accidents.put(id, accident);
    }

    public void edit(Accident accident) {
        accidents.put(accident.getId(), accident);
    }

    public void save(Accident accident, String[] ids) {
        accident.setType(findTypeById(accident.getType().getId()));
        accident.setRules(findRulesByIds(ids));
        if (accident.getId() == 0) {
            create(accident);
        } else {
            edit(accident);
        }
    }

    public Accident findById(int id) {
        return accidents.get(id);
    }

    public AccidentType findTypeById(int id) {
        return types.get(id);
    }

    public Set<Rule> findRulesByIds(String[] ids) {
        Set<Rule> rsl = new HashSet<>();
        for (String id : ids) {
            int intId = Integer.parseInt(id);
            rsl.add(rules.get(intId));
        }
        return rsl;
    }

    private void init() {
        AccidentType t1 = AccidentType.of(1, "Две машины");
        AccidentType t2 = AccidentType.of(2, "Машина и человек");
        AccidentType t3 = AccidentType.of(3, "Машина и велосипед");
        types.put(1, t1);
        types.put(2, t2);
        types.put(3, t3);
        Rule r1 = Rule.of(1, "Статья 1");
        Rule r2 = Rule.of(2, "Статья 2");
        Rule r3 = Rule.of(3, "Статья 3");
        rules.put(1, r1);
        rules.put(2, r2);
        rules.put(3, r3);
        Accident acc1 = new Accident("Превышение скорости",
                "Превышение скорости на 20-40 км/ч", "Воронцовский бульвар 55");
        acc1.setType(t1);
        acc1.addRule(r1);
        acc1.addRule(r3);
        Accident acc2 = new Accident("Выезд на выделенную полосу",
                "Выезд на полосу для общественного транспорта", "Невский проспект 27");
        acc2.setType(t2);
        acc2.addRule(r2);
        Accident acc3 = new Accident("Игнорирование знака",
                "Игнорирование знака движение по полосам", "Литейный проспект 6");
        acc3.setType(t3);
        acc3.addRule(r1);
        acc3.addRule(r2);
        acc3.addRule(r3);
        create(acc1);
        create(acc2);
        create(acc3);
    }
}
