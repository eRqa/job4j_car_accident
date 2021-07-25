package ru.job4j.accident.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.accident.model.Accident;
import ru.job4j.accident.model.AccidentType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class AccidentMem {

    private final Map<Integer, Accident> accidents = new HashMap<>();
    private final Map<Integer, AccidentType> types = new HashMap<>();
    private final AtomicInteger size = new AtomicInteger();

    public AccidentMem() {
        create(new Accident("Превышение скорости",
                "Превышение скорости на 20-40 км/ч", "Воронцовский бульвар 55"));
        create(new Accident("Выезд на выделенную полосу",
                "Выезд на полосу для общественного транспорта", "Невский проспект 27"));
        create(new Accident("Игнорирование знака",
                "Игнорирование знака движение по полосам", "Литейный проспект 6"));
        types.put(1, AccidentType.of(1, "Две машины"));
        types.put(2, AccidentType.of(2, "Машина и человек"));
        types.put(3, AccidentType.of(3, "Машина и велосипед"));
    }

    public List<Accident> getAccidents() {
        return new ArrayList<>(accidents.values());
    }

    public List<AccidentType> getAllTypes() {
        return new ArrayList<>(types.values());
    }

    public void create(Accident accident) {
        int id = size.incrementAndGet();
        accident.setId(id);
        accidents.put(id, accident);
    }

    public void edit(Accident accident) {
        accidents.put(accident.getId(), accident);
    }

    public void save(Accident accident) {
        accident.setType(findTypeById(accident.getType().getId()));
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
}
