package ru.job4j.accident.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.accident.model.Accident;
import ru.job4j.accident.repository.AccidentRepository;
import ru.job4j.accident.repository.AccidentTypeRepository;
import ru.job4j.accident.repository.RulesRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
public class AccidentControl {
    private final AccidentRepository accidents;
    private final AccidentTypeRepository accidentTypes;
    private final RulesRepository rules;

    public AccidentControl(AccidentRepository accidents,
                           AccidentTypeRepository accidentTypes,
                           RulesRepository rules) {
        this.accidents = accidents;
        this.accidentTypes = accidentTypes;
        this.rules = rules;
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("types", accidentTypes.findAll());
        model.addAttribute("rules", rules.findAll());
        return "accident/create";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam("id") int id, Model model) {
        model.addAttribute("types", accidentTypes.findAll());
        model.addAttribute("accident", accidents.findById(id).get());
        model.addAttribute("rules", rules.findAll());
        return "accident/edit";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Accident accident, HttpServletRequest req) {
        String[] ids = req.getParameterValues("rIds");
        if (ids != null) {
            var intIds = Arrays.stream(ids).mapToInt(Integer::parseInt).boxed().collect(Collectors.toSet());
            var foundedRules = rules.findAllById(intIds);
            foundedRules.forEach(accident::addRule);
        }
        accidents.save(accident);
        return "redirect:/";
    }

}