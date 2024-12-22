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
import GameHub.model.Publisher;
import GameHub.repository.PublisherRepository;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Route("publishers")
@PageTitle("Издатели")
@SpringComponent
@UIScope
public class PublisherView extends Composite<VerticalLayout> {

    @Autowired
    private PublisherRepository publisherRepository;

    private Dialog addDialog;
    private Binder<Publisher> binder;
    private Publisher currentPublisher;
    private TextField searchField;
    private Select<String> sortFilter;
    private HorizontalLayout cardsContainer;
    private TextField nameField;
    private TextField mainOfficeField;
    private TextField yearField;

    public PublisherView() {
        getContent().add(new H1("Издатели"));
        getContent().addClassName("publisher-view");

        HorizontalLayout filterLayout = new HorizontalLayout();
        searchField = new TextField();
        searchField.setPlaceholder("Поиск по названию");
        searchField.addClassName("filter-input");
        searchField.addValueChangeListener(e -> updateCards());

        sortFilter = new Select<>();
        sortFilter.setItems("По алфавиту (A-Z)", "По алфавиту (Z-A)", "По году (возрастание)", "По году (убывание)");
        sortFilter.setPlaceholder("Сортировка");
        sortFilter.addClassName("filter-input");
        sortFilter.addValueChangeListener(e -> updateCards());

        filterLayout.add(searchField, sortFilter);
        getContent().add(filterLayout);

        Button addButton = new Button("Добавить издателя");
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
        nameField.addClassName("publisher-view-text-field-1");
        mainOfficeField = new TextField();
        mainOfficeField.addClassName("publisher-view-text-field-2");
        yearField = new TextField();
        yearField.addClassName("publisher-view-text-field-1");
        yearField.addClassName("publisher-view-text-field-3");
        formLayout.addFormItem(nameField, "Название издателя:");
        formLayout.addFormItem(mainOfficeField, "Город главного офиса:");
        formLayout.addFormItem(yearField, "Год основания:");

        Button saveButton = new Button("Сохранить");
        saveButton.addClassName("publisher-view-button-1");
        saveButton.addClickListener(event -> savePublisher());

        formLayout.addFormItem(saveButton, "");
        addDialog.add(formLayout);

        binder = new Binder<>(Publisher.class);
        binder.forField(nameField).bind(Publisher::getName, Publisher::setName);
        binder.forField(mainOfficeField).bind(Publisher::getMainOffice, Publisher::setMainOffice);

        yearField.setValueChangeMode(ValueChangeMode.ON_BLUR);
        yearField.addValueChangeListener(event -> {
            try {
                int year = Integer.parseInt(event.getValue());
                currentPublisher.setYear(year);
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
        List<Publisher> publishers = publisherRepository.findAll();
        String searchText = searchField.getValue();
        String sortOption = sortFilter.getValue();

        publishers = publishers.stream()
                .filter(publisher -> searchText.isEmpty() || publisher.getName().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

        if ("По алфавиту (A-Z)".equals(sortOption)) {
            publishers.sort(Comparator.comparing(Publisher::getName));
        } else if ("По алфавиту (Z-A)".equals(sortOption)) {
            publishers.sort(Comparator.comparing(Publisher::getName).reversed());
        } else if ("По году (возрастание)".equals(sortOption)) {
            publishers.sort(Comparator.comparingInt(Publisher::getYear));
        } else if ("По году (убывание)".equals(sortOption)) {
            publishers.sort(Comparator.comparingInt(Publisher::getYear).reversed());
        }

        for (Publisher publisher : publishers) {
            VerticalLayout card = createPublisherCard(publisher);
            cardsContainer.add(card);
        }
    }

    private VerticalLayout createPublisherCard(Publisher publisher) {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(false);
        card.setWidthFull();
        card.setMaxWidth("300px");
        card.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
        card.getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        card.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        card.getStyle().set("margin", "10px");
        card.add(new H1(publisher.getName()));
        card.add(new Paragraph("Город главного офиса: " + publisher.getMainOffice()));
        card.add(new Paragraph("Год основания: " + publisher.getYear()));

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Button editButton = new Button("Редактировать", event -> openEditDialog(publisher));
        Button deleteButton = new Button("Удалить", event -> deletePublisher(publisher));
        deleteButton.addClassName("delete-button");
        buttonsLayout.add(editButton, deleteButton);
        card.add(buttonsLayout);
        card.addClassName("glass-effect2");
        return card;
    }

    private void openAddDialog() {
        currentPublisher = new Publisher();
        nameField.clear();
        mainOfficeField.clear();
        yearField.clear();
        binder.readBean(currentPublisher);
        addDialog.open();
    }

    private void openEditDialog(Publisher publisher) {
        currentPublisher = publisher;
        binder.readBean(currentPublisher);
        addDialog.open();
    }

    private void savePublisher() {
        try {
            binder.writeBean(currentPublisher);
            publisherRepository.save(currentPublisher);
            addDialog.close();
            updateCards();
        } catch (ValidationException e) {
            // Handle validation errors
        }
    }

    private void deletePublisher(Publisher publisher) {
        publisherRepository.delete(publisher);
        updateCards();
    }
}
