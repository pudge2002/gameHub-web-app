package GameHub.views.pages;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import GameHub.model.Developer;
import GameHub.model.Game;
import GameHub.model.Publisher;
import GameHub.repository.DeveloperRepository;
import GameHub.repository.GameRepository;
import GameHub.repository.PublisherRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Route("games")
@PageTitle("Игры")
@SpringComponent
@UIScope
public class GamesView extends Composite<VerticalLayout> {

    private final GameRepository gameRepository;
    private final DeveloperRepository developerRepository;
    private final PublisherRepository publisherRepository;
    private HorizontalLayout cardsContainer;
    private TextField searchField;
    private Select<String> sortFilter;
    private Select<String> genreFilter;

    @Autowired
    public GamesView(GameRepository gameRepository, DeveloperRepository developerRepository, PublisherRepository publisherRepository) {
        this.gameRepository = gameRepository;
        this.developerRepository = developerRepository;
        this.publisherRepository = publisherRepository;
    }

    @PostConstruct
    public void init() {
        VerticalLayout layout = getContent();
        getContent().addClassName("game-view");
        H1 title = new H1("Игры");
        layout.add(title);

        HorizontalLayout filterLayout = new HorizontalLayout();
        searchField = new TextField();
        searchField.setPlaceholder("Поиск по названию");
        searchField.addClassName("filter-input");
        searchField.addValueChangeListener(e -> updateCards());

        sortFilter = new Select<>();
        sortFilter.setOverlayClassName("developer-view-select-1");
        sortFilter.addClassName("developer-view-select-1");
        sortFilter.setItems("По алфавиту (A-Z)", "По алфавиту (Z-A)", "По году (возрастание)", "По году (убывание)");
        sortFilter.setPlaceholder("Сортировка");
        sortFilter.addClassName("filter-input");
        sortFilter.addValueChangeListener(e -> updateCards());

        genreFilter = new Select<>();
        genreFilter.setOverlayClassName("developer-view-select-1");
        genreFilter.addClassName("developer-view-select-1");
        genreFilter.setItems("Все жанры", "Action", "Adventure", "RPG", "Strategy", "Simulation", "Sports");
        genreFilter.setPlaceholder("Жанр");
        genreFilter.addClassName("filter-input");
        genreFilter.addValueChangeListener(e -> updateCards());

        filterLayout.add(searchField, sortFilter, genreFilter);
        getContent().add(filterLayout);

        Button addGameButton = new Button("Добавить игру", event -> openGameDialog());
        addGameButton.addClassName("add-button");
        layout.add(addGameButton);

        cardsContainer = new HorizontalLayout();
        cardsContainer.setClassName("cards-container");
        layout.add(cardsContainer);

        updateCards();
    }

    private void updateCards() {
        cardsContainer.removeAll();

        List<Game> games = gameRepository.findAll();
        String searchText = searchField.getValue();
        String sortOption = sortFilter.getValue();
        String genreOption = genreFilter.getValue();

        if (genreOption == null) {
            genreOption = "Все жанры"; // Default value if genreOption is null
        }

        final String finalGenreOption = genreOption;
        final String finalSearchText = searchText;
        final String finalSortOption = sortOption;

        games = games.stream()
                .filter(game -> finalSearchText.isEmpty() || game.getName().toLowerCase().contains(finalSearchText.toLowerCase()))
                .filter(game -> finalGenreOption.equals("Все жанры") || game.getGenre().equals(finalGenreOption))
                .collect(Collectors.toList());

        if ("По алфавиту (A-Z)".equals(finalSortOption)) {
            games.sort(Comparator.comparing(Game::getName));
        } else if ("По алфавиту (Z-A)".equals(finalSortOption)) {
            games.sort(Comparator.comparing(Game::getName).reversed());
        } else if ("По году (возрастание)".equals(finalSortOption)) {
            games.sort(Comparator.comparingInt(Game::getYear));
        } else if ("По году (убывание)".equals(finalSortOption)) {
            games.sort(Comparator.comparingInt(Game::getYear).reversed());
        }

        for (Game game : games) {
            VerticalLayout card = createGameCard(game);
            cardsContainer.add(card);
        }
    }

    private VerticalLayout createGameCard(Game game) {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(false);
        card.setWidthFull();
        card.setMaxWidth("300px");
        card.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
        card.getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        card.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        card.getStyle().set("margin", "10px");
        card.getStyle().set("cursor", "pointer");

        Image image = new Image(new StreamResource("image", () -> new ByteArrayInputStream(game.getImage_title())), "Image");
        image.setWidth("100%");
        image.setHeight("150px");
        image.getStyle().set("object-fit", "cover");
        card.add(image);

        card.add(new Paragraph("Название: " + game.getName()));
        card.add(new Paragraph("Разработчик: " + game.getDeveloper_id().getName()));

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Button editButton = new Button("Редактировать", event -> openEditDialog(game));
        Button deleteButton = new Button("Удалить", event -> deleteGame(game));
        deleteButton.addClassName("delete-button");
        buttonsLayout.add(editButton, deleteButton);
        card.add(buttonsLayout);
        card.addClassName("glass-effect2");
        card.addClickListener(event -> {
            UI.getCurrent().navigate(GameDetailsView.class, Integer.toString(game.getId()));
        });

        return card;
    }

    private void openGameDialog() {
        Dialog dialog = new Dialog();
        dialog.addClassName("non-transparent-dialog");
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.getStyle().set("background-color","black");

        dialogLayout.getStyle().set("border-radius","10px");
        Binder<Game> binder = new Binder<>(Game.class);
        Game game = new Game();

        TextField nameField = new TextField("Название");
        TextField yearField = new TextField("Год");
        MemoryBuffer imageTitleBuffer = new MemoryBuffer();
        MemoryBuffer imageBackBuffer = new MemoryBuffer();
        Upload imageTitleUpload = new Upload(imageTitleBuffer);
        imageTitleUpload.setAcceptedFileTypes("image/jpeg", "image/png");
        imageTitleUpload.setUploadButton(new Button("Загрузить обложку"));
        Upload imageBackUpload = new Upload(imageBackBuffer);
        imageBackUpload.setAcceptedFileTypes("image/jpeg", "image/png");
        imageBackUpload.setUploadButton(new Button("Загрузить фон игры"));
        Select<String> genreField = new Select<>();
        genreField.setLabel("Жанр");
        genreField.setItems("Action", "Adventure", "RPG", "Strategy", "Simulation", "Sports");
        Select<Developer> developerSelect = new Select<>();
        Select<Publisher> publisherSelect = new Select<>();
        //<theme-editor-local-classname>
        publisherSelect.setOverlayClassName("games-view-select-1");
        //<theme-editor-local-classname>
        publisherSelect.addClassName("games-view-select-1");

        List<Developer> developers = developerRepository.findAll().stream()
                .sorted(Comparator.comparing(Developer::getName))
                .collect(Collectors.toList());
        List<Publisher> publishers = publisherRepository.findAll().stream()
                .sorted(Comparator.comparing(Publisher::getName))
                .collect(Collectors.toList());

        developerSelect.setLabel("Разработчик");
        developerSelect.setItems(developers);
        developerSelect.setItemLabelGenerator(Developer::getName);

        publisherSelect.setLabel("Издатель");
        publisherSelect.setItems(publishers);
        publisherSelect.setItemLabelGenerator(Publisher::getName);

        FormLayout formLayout = new FormLayout();
        formLayout.add(nameField, yearField, imageTitleUpload, imageBackUpload, genreField, developerSelect, publisherSelect);

        binder.forField(nameField).bind(Game::getName, Game::setName);
        binder.forField(yearField).withConverter(Integer::valueOf, String::valueOf).bind(Game::getYear, Game::setYear);
        binder.forField(genreField).bind(Game::getGenre, Game::setGenre);
        binder.forField(developerSelect).bind(Game::getDeveloper_id, Game::setDeveloper_id);
        binder.forField(publisherSelect).bind(Game::getPublisher_id, Game::setPublisher_id);

        imageTitleUpload.addSucceededListener(event -> {
            try (InputStream fis = imageTitleBuffer.getInputStream();
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                game.setImage_title(bos.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        imageBackUpload.addSucceededListener(event -> {
            try (InputStream fis = imageBackBuffer.getInputStream();
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                game.setImage_back(bos.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button saveButton = new Button("Сохранить", event -> {
            try {
                binder.writeBean(game);
                gameRepository.save(game);
                dialog.close();
                addNewGameCard(game);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton);
        dialogLayout.add(formLayout, buttonLayout);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void openEditDialog(Game game) {
        Dialog dialog = new Dialog();
        dialog.addClassName("non-transparent-dialog");
        VerticalLayout dialogLayout = new VerticalLayout();

        Binder<Game> binder = new Binder<>(Game.class);

        TextField nameField = new TextField("Название");
        TextField yearField = new TextField("Год");
        MemoryBuffer imageTitleBuffer = new MemoryBuffer();
        MemoryBuffer imageBackBuffer = new MemoryBuffer();
        Upload imageTitleUpload = new Upload(imageTitleBuffer);
        Upload imageBackUpload = new Upload(imageBackBuffer);
        Select<String> genreField = new Select<>();
        genreField.setLabel("Жанр");
        genreField.setItems("Action", "Adventure", "RPG", "Strategy", "Steals", "Survival", "Sandbox","Simulation", "Sports", "Horror" );
        Select<Developer> developerSelect = new Select<>();
        Select<Publisher> publisherSelect = new Select<>();

        List<Developer> developers = developerRepository.findAll().stream()
                .sorted(Comparator.comparing(Developer::getName))
                .collect(Collectors.toList());
        List<Publisher> publishers = publisherRepository.findAll().stream()
                .sorted(Comparator.comparing(Publisher::getName))
                .collect(Collectors.toList());

        developerSelect.setLabel("Разработчик");
        developerSelect.setItems(developers);
        developerSelect.setItemLabelGenerator(Developer::getName);

        publisherSelect.setLabel("Издатель");
        publisherSelect.setItems(publishers);
        publisherSelect.setItemLabelGenerator(Publisher::getName);

        FormLayout formLayout = new FormLayout();
        formLayout.add(nameField, yearField, imageTitleUpload, imageBackUpload, genreField, developerSelect, publisherSelect);

        binder.forField(nameField).bind(Game::getName, Game::setName);
        binder.forField(yearField).withConverter(Integer::valueOf, String::valueOf).bind(Game::getYear, Game::setYear);
        binder.forField(genreField).bind(Game::getGenre, Game::setGenre);
        binder.forField(developerSelect).bind(Game::getDeveloper_id, Game::setDeveloper_id);
        binder.forField(publisherSelect).bind(Game::getPublisher_id, Game::setPublisher_id);

        imageTitleUpload.addSucceededListener(event -> {
            try (InputStream fis = imageTitleBuffer.getInputStream();
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                game.setImage_title(bos.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        imageBackUpload.addSucceededListener(event -> {
            try (InputStream fis = imageBackBuffer.getInputStream();
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                game.setImage_back(bos.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binder.readBean(game);

        Button saveButton = new Button("Сохранить", event -> {
            try {
                binder.writeBean(game);
                gameRepository.save(game);
                dialog.close();
                updateCards();
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton);
        dialogLayout.add(formLayout, buttonLayout);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void deleteGame(Game game) {
        gameRepository.delete(game);
        updateCards();
    }

    private void addNewGameCard(Game game) {
        VerticalLayout card = createGameCard(game);
        cardsContainer.add(card);
    }
}
