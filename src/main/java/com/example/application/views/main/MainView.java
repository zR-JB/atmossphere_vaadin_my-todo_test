package com.example.application.views.main;

import com.example.application.entity.Person;
import com.example.application.services.PersonService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Main")
@Route(value = "")
public class MainView extends HorizontalLayout {

    private TextField name;
    private Button sayHello;



    private Grid<Person> Grid = new Grid<>(Person.class, true);
    private BeanValidationBinder<Person> binder;
    private Person person;
    private final PersonService service;

    @Autowired
    public MainView(PersonService service) {
        this.service = service;
        name = new TextField("Your name");
        sayHello = new Button("Say hello");
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);

        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);
        add(name, sayHello);
        Grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        add(Grid);
        Button refreshButton = new Button("REFRESH");
        refreshButton.addClickListener(event -> {
           Grid.getDataProvider().refreshAll();
            Grid.getLazyDataView().refreshAll();
            System.out.println("REFRESHING!");
        });
        add(refreshButton);



    }

}
