package com.example.lab06.view;

import com.example.lab06.pojo.Wizard;
import com.example.lab06.pojo.Wizards;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Route("mainPage.it")
public class MainWizardView extends VerticalLayout {
    private int Index = 0;
    private Wizards wizards;
    private Button back, create, update, del, next;
    private TextField name, money;
    private RadioButtonGroup<String> sex;
    private Select<String> position, school, house;
    public MainWizardView() {
        wizards = new Wizards();
        name = new TextField();
        name.setPlaceholder("Fullname");

        sex = new RadioButtonGroup<>();
        sex.setLabel("Gender :");
        sex.setItems("Male", "Female");

        position = new Select<>();
        position.setItems("Student", "Teacher");
        position.setPlaceholder("Position");

        money = new TextField("Dollars");
        money.setPlaceholder("$");

        school = new Select<>();
        school.setItems("Hogwarts", "Beauxbatons", "Durmstrang");
        school.setPlaceholder("School");

        house = new Select<>();
        house.setItems("Gryffindor", "Ravenclaw", "Hufflepuff", "Slytherin");
        house.setPlaceholder("House");

        back = new Button("<<");
        create = new Button("Create");
        update = new Button("Update");
        del = new Button("Delete");
        next = new Button(">>");
        HorizontalLayout h = new HorizontalLayout();
        h.add(back, create, update, del, next);

        add(name, sex, position, money, school, house, h);
        this.refresh();

        back.addClickListener(e -> {
            Index = Math.max(Index-1, 0);
            this.updateAll();
        });

        create.addClickListener(e -> {
            String n = name.getValue();
            String s = sex.getValue().equals("Male") ? "m" : "f";
            String p = position.getValue().equals("Student") ? "student" : "teacher";
            int m = Integer.parseInt(money.getValue());
            String sc = school.getValue();
            String ho = house.getValue();

            Wizard w = new Wizard(null, s, n, sc, ho, p, m);
            WebClient.create()
                    .post()
                    .uri("http://localhost:8080/addWizard")
                    .body(Mono.just(w), Wizard.class)
                    .retrieve()
                    .bodyToMono(Wizard.class)
                    .block();
            Index = wizards.getWizards().size();
            updateAll();
            refresh();
        });

        update.addClickListener(e -> {
            String id = wizards.getWizards().get(Index).get_id();
            String n = name.getValue();
            String s = sex.getValue().equals("Male") ? "m" : "f";
            String p = position.getValue().equals("Student") ? "student" : "teacher";
            int m = Integer.parseInt(money.getValue());
            String sc = school.getValue();
            String ho = house.getValue();
            Wizard w = new Wizard(id, s, n, sc, ho, p, m);
            WebClient.create()
                    .post()
                    .uri("http://localhost:8080/updateWizard")
                    .body(Mono.just(w), Wizard.class)
                    .retrieve()
                    .bodyToMono(Wizard.class)
                    .block();
            refresh();
        });

        del.addClickListener(e -> {
            String id = wizards.getWizards().get(Index).get_id();
            WebClient.create()
                    .post()
                    .uri("http://localhost:8080/deleteWizard")
                    .body(Mono.just(wizards.getWizards().get(Index)), Wizard.class)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            Index = 0;
            refresh();
        });

        next.addClickListener(e -> {
            Index = Math.min(Index+1, wizards.getWizards().size()-1);
            updateAll();
        });
    }

    private void refresh() {
        List<Wizard> w = WebClient
                .create()
                .get()
                .uri("http://localhost:8080/wizards")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Wizard>>() {})
                .block();
        this.wizards.setWizards(w);
        updateAll();
    }
    private void updateAll() {
        back.setEnabled(!(Index == 0));
        name.setValue(wizards.getWizards().get(Index).getName());
        sex.setValue(wizards.getWizards().get(Index).getSex().equals("m") ? "Male" : "Female");
        position.setValue(wizards.getWizards().get(Index).getPosition().equals("student") ? "Student" : "Teacher");
        money.setValue(String.valueOf(wizards.getWizards().get(Index).getMoney()));
        school.setValue(wizards.getWizards().get(Index).getSchool());
        house.setValue(wizards.getWizards().get(Index).getHouse());
        next.setEnabled(!(Index == wizards.getWizards().size()-1));
    }
}
