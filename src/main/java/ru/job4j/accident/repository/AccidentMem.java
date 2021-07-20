package ru.job4j.accident.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.accident.model.Accident;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AccidentMem {

    private Map<Integer, Accident> accidents = new HashMap<>();

    public AccidentMem() {
        accidents.put(1, new Accident(1, "Превышение скорости",
                "Превышение скорости на 20-40 км/ч", "Воронцовский бульвар 55"));
        accidents.put(2, new Accident(2, "Выезд на выделенную полосу",
                "Выезд на полосу для общественного транспорта", "Невский проспект 27"));
        accidents.put(3, new Accident(3, "Игнорирование знака",
                "Игнорирование знака движение по полосам", "Литейный проспект 6"));
    }

    public List<Accident> getAccidents() {
        return new ArrayList<>(accidents.values());
    }

    public void create(Accident accident) {
        accident.setId(accidents.size() + 1);
        accidents.put(accident.getId(), accident);
    }

    public void edit(Accident accident) {
        accidents.put(accident.getId(), accident);
    }

    public void save(Accident accident) {
        if (accident.getId() == 0) {
            create(accident);
        } else {
            edit(accident);
        }
    }

    public Accident findById(int id) {
        return accidents.get(id);
    }
}
