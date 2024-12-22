package GameHub.views.pages;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import GameHub.model.Game;
import GameHub.repository.GameRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Route("game-details")
@PageTitle("Детали игры")
@SpringComponent
@UIScope
public class GameDetailsView extends Composite<VerticalLayout> implements HasUrlParameter<String> {

    private final GameRepository gameRepository;
    private Game game;

    @Autowired
    public GameDetailsView(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        int gameId = Integer.parseInt(parameter);
        game = gameRepository.findById(gameId).orElse(null);
        if (game != null) {
            updateView();
        }
    }

    private void updateView() {
        VerticalLayout layout = getContent();
        layout.removeAll();

        // Основной контейнер с закругленными углами и центрированием
        Div mainContainer = new Div();
      mainContainer.getStyle().set("display", "flex");
        mainContainer.getStyle().set("flex-direction", "column");
        mainContainer.getStyle().set("justify-content", "center");
        mainContainer.getStyle().set("align-items", "center");
        mainContainer.getStyle().set("border-radius", "15px");
//        mainContainer.getStyle().set("overflow", "hidden");
        mainContainer.getStyle().set("margin-top","20%");
        mainContainer.getStyle().set("box-shadow", "0 4px 8px rgba(0, 0, 0, 0.1)");
        mainContainer.getStyle().set("background-color", "#ffffff");
        mainContainer.setWidth("80%"); // Устанавливаем ширину контейнера
        mainContainer.setMaxWidth("1200px");

        // Контейнер для изображения и текста
        Div contentContainer = new Div();

        contentContainer.getStyle().set("background-color", "#ffffff");
        contentContainer.getStyle().set("border-top", "1px solid #e0e0e0");
        contentContainer.getStyle().set("text-align", "center"); // Центрирование текста

        // Контейнер для изображения
        Div imageContainer = new Div();
        imageContainer.getStyle().set("position", "relative");
        imageContainer.setWidthFull(); // Устанавливаем ширину контейнера для изображения

        Image image = new Image(new StreamResource("image", () -> new ByteArrayInputStream(game.getImage_back())), "Image");
        image.setWidth("100%"); // Устанавливаем ширину изображения
        image.setHeight("600px"); // Устанавливаем высоту изображения
        image.getStyle().set("object-fit", "cover");
        image.getStyle().set("object-position", "top"); // Обрезаем нижнюю часть изображения
        imageContainer.add(image);

        // Контейнер для текста
        Div textContainer = new Div();
        textContainer.getStyle().set("position", "relative");
        textContainer.getStyle().set("top", "-50px"); // Текст немного наезжает на картинку
        textContainer.getStyle().set("width", "100%");
        textContainer.getStyle().set("background", "rgba(255, 255, 255, 1)"); // Полупрозрачный фон
        textContainer.getStyle().set("padding", "20px");
        textContainer.getStyle().set("box-sizing", "border-box");
        textContainer.getStyle().set("z-index", "1"); // Убедитесь, что текст отображается поверх изображения
        textContainer.getStyle().set("border-top-left-radius", "15px"); // Закругление верхнего левого угла
        textContainer.getStyle().set("border-top-right-radius", "15px"); // Закругление верхнего правого угла

        VerticalLayout textLayout = new VerticalLayout();
        textLayout.setPadding(false);
        textLayout.setSpacing(false);
        textLayout.getStyle().set("text-align", "center"); // Центрирование текста

        // Добавляем текст
        H1 title = new H1(game.getName());
        title.getStyle().set("color", "black"); // Черный цвет текста
        textLayout.add(title);

        textLayout.add(new Paragraph("Год: " + game.getYear()));
        textLayout.add(new Paragraph("Жанр: " + game.getGenre()));
        textLayout.add(new Paragraph("Разработчик: " + game.getDeveloper_id().getName()));
        textLayout.add(new Paragraph("Издатель: " + game.getPublisher_id().getName()));
        textLayout.getStyle().set("color", "black");

        // Добавим информацию из Wikipedia
        Paragraph wikiInfo = new Paragraph();
        wikiInfo.setText(parseGameInfo(game.getName()));
        wikiInfo.getStyle().set("color", "black"); // Черный цвет текста
        wikiInfo.getStyle().set("text-align", "left"); // Выравнивание текста по левому краю
        wikiInfo.setWidthFull(); // Ширина текста по всей ширине контейнера
        textLayout.add(wikiInfo);

        textContainer.add(textLayout);

        // Добавляем изображение и текст в контейнер
        contentContainer.add(imageContainer, textContainer);

        // Добавляем контейнеры в основной контейнер
        mainContainer.add(contentContainer);

        // Центрирование основного контейнера по экрану
        layout.setSizeFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(mainContainer);
    }


    private String parseGameInfo(String gameName) {
        String url = "https://ru.wikipedia.org/wiki/" + gameName;
        StringBuilder info = new StringBuilder();

        try {
            // Подключаемся к странице и получаем документ
            Document document = Jsoup.connect(url).get();

            // Извлечение названия игры
            String gameTitle = document.selectFirst("h1#firstHeading").text();
            info.append("Название игры: ").append(gameTitle).append("\n");

            // Извлечение первого абзаца описания игры
            Element firstParagraph = document.selectFirst("div.mw-parser-output > p");
            if (firstParagraph != null) {
                String gameDescription = firstParagraph.text();
                info.append("Описание игры: ").append(gameDescription).append("\n");
            }

            // Извлечение информации из инфобокса (если доступно)
            Element infobox = document.selectFirst("table.infobox");
            if (infobox != null) {
                Elements rows = infobox.select("tr");
                for (Element row : rows) {
                    Elements ths = row.select("th");
                    Elements tds = row.select("td");
                    if (ths.size() > 0 && tds.size() > 0) {
                        String key = ths.first().text();
                        String value = tds.first().text();
                        info.append(key).append(": ").append(value).append("\n");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            info.append("Информация о игре ").append(gameName).append(" не найдена.");
        }

        return info.toString();
    }
}
