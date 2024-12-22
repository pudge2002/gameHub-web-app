package GameHub.views.pages;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import GameHub.model.Developer;
import GameHub.repository.DeveloperRepository;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import GameHub.repository.DeveloperRepository;
import GameHub.repository.GameRepository;
import GameHub.repository.PublisherRepository;
import GameHub.model.Developer;
import GameHub.model.Game;
import GameHub.model.Publisher;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Route("developers")
@PageTitle("Разработчики")
@SpringComponent
@UIScope
public class DeveloperView extends Composite<VerticalLayout> {

    @Autowired
    private DeveloperRepository developerRepository;

    private Dialog addDialog;
    private Binder<Developer> binder;
    private Developer currentDeveloper;
    private TextField searchField;
    private Select<String> sortFilter;
    private HorizontalLayout cardsContainer;
    private TextField nameField;
    private TextField cityField;
    private TextField yearField;

    public DeveloperView() {
        getContent().add(new H1("Разработчики"));
        getContent().addClassName("developer-view");

        HorizontalLayout filterLayout = new HorizontalLayout();
        searchField = new TextField();
        searchField.setPlaceholder("Поиск по названию");
        searchField.addClassName("filter-input");
        searchField.addValueChangeListener(e -> updateCards());

        sortFilter = new Select<>();
        //<theme-editor-local-classname>
        sortFilter.setOverlayClassName("developer-view-select-1");
        //<theme-editor-local-classname>
        sortFilter.addClassName("developer-view-select-1");
        sortFilter.setItems("По алфавиту (A-Z)", "По алфавиту (Z-A)", "По году (возрастание)", "По году (убывание)");
        sortFilter.setPlaceholder("Сортировка");
        sortFilter.addClassName("filter-input");
        sortFilter.addValueChangeListener(e -> updateCards());

        filterLayout.add(searchField, sortFilter);
        getContent().add(filterLayout);

        Button addButton = new Button("Добавить разработчика");
        addButton.addClassName("add-button");
        addButton.addClickListener(event -> openAddDialog());
        getContent().add(addButton);

        cardsContainer = new HorizontalLayout();
        cardsContainer.setClassName("cards-container");
        getContent().add(cardsContainer);
        //Всплывающее окно
        addDialog = new Dialog();
        FormLayout formLayout = new FormLayout();
        formLayout.getStyle().set("background-color","black");
        formLayout.getStyle().set("padding","10px");
        formLayout.getStyle().set("border-radius","10px");
        nameField = new TextField();
        nameField.addClassName("developer-view-text-field-1");
        cityField = new TextField();
        //<theme-editor-local-classname>
        cityField.addClassName("developer-view-text-field-1");
        cityField.addClassName("developer-view-text-field-2");
        yearField = new TextField();
        yearField.addClassName("developer-view-text-field-3");

        formLayout.addFormItem(nameField, "Название разработчика:");
        formLayout.addFormItem(cityField, "Город:");
        formLayout.addFormItem(yearField, "Год основания:");

        Button saveButton = new Button("Сохранить");
        saveButton.addClassName("developer-view-button-1");
        saveButton.addClickListener(event -> saveDeveloper());

        formLayout.addFormItem(saveButton, "");
        addDialog.add(formLayout);

        binder = new Binder<>(Developer.class);
        binder.forField(nameField).bind(Developer::getName, Developer::setName);
        binder.forField(cityField).bind(Developer::getCity, Developer::setCity);

        yearField.setValueChangeMode(ValueChangeMode.ON_BLUR);
        yearField.addValueChangeListener(event -> {
            try {
                int year = Integer.parseInt(event.getValue());
                currentDeveloper.setYear(year);
            } catch (NumberFormatException e) {
                // Handle invalid input
            }
        });
    }

    @PostConstruct
    void init() {
        updateCards();
    }

    private void updateCards() {
        cardsContainer.removeAll();
        List<Developer> developers = developerRepository.findAll();
        String searchText = searchField.getValue();
        String sortOption = sortFilter.getValue();

        developers = developers.stream()
                .filter(developer -> searchText.isEmpty() || developer.getName().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

        if ("По алфавиту (A-Z)".equals(sortOption)) {
            developers.sort(Comparator.comparing(Developer::getName));
        } else if ("По алфавиту (Z-A)".equals(sortOption)) {
            developers.sort(Comparator.comparing(Developer::getName).reversed());
        } else if ("По году (возрастание)".equals(sortOption)) {
            developers.sort(Comparator.comparingInt(Developer::getYear));
        } else if ("По году (убывание)".equals(sortOption)) {
            developers.sort(Comparator.comparingInt(Developer::getYear).reversed());
        }

        for (Developer developer : developers) {
            VerticalLayout card = createDeveloperCard(developer);
            cardsContainer.add(card);
        }
    }

    private VerticalLayout createDeveloperCard(Developer developer) {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(false);
        card.setWidthFull();
        card.setMaxWidth("300px");
        card.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
        card.getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        card.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        card.getStyle().set("margin", "10px");
        card.add(new H1(developer.getName()));
        card.add(new Paragraph("Город: " + developer.getCity()));
        card.add(new Paragraph("Год основания: " + developer.getYear()));

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Button editButton = new Button("Редактировать", event -> openEditDialog(developer));
        Button deleteButton = new Button("Удалить", event -> deleteDeveloper(developer));
        deleteButton.addClassName("delete-button");
        buttonsLayout.add(editButton, deleteButton);
        card.add(buttonsLayout);
        card.addClassName("glass-effect2");
        return card;
    }

    private void openAddDialog() {
        currentDeveloper = new Developer();
        nameField.clear();
        cityField.clear();
        yearField.clear();
        binder.readBean(currentDeveloper);
        addDialog.open();
    }

    private void openEditDialog(Developer developer) {
        currentDeveloper = developer;
        binder.readBean(currentDeveloper);
        addDialog.open();
    }

    private void saveDeveloper() {
        try {
            binder.writeBean(currentDeveloper);
            developerRepository.save(currentDeveloper);
            addDialog.close();
            updateCards();
        } catch (ValidationException e) {
            // Handle validation errors
        }
    }

    private void deleteDeveloper(Developer developer) {
        developerRepository.delete(developer);
        updateCards();
    }
}
