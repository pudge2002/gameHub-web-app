package GameHub.views.pages;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Home View")
public class HomeView extends Composite<VerticalLayout> {

    public HomeView() {
        getContent().addClassName("home-view");

        // Создаем горизонтальный контейнер для блоков
        HorizontalLayout blockContainer = new HorizontalLayout();
        blockContainer.setWidthFull();
        blockContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        blockContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        blockContainer.getStyle().set("flex-grow", "1"); // Разрешаем контейнеру расти

        // Создаем три блока
        Div block1 = createBlock("games", "https://4kwallpapers.com/images/wallpapers/cyberpunk-2077-pc-games-playstation-4-xbox-one-xbox-series-2048x2048-640.jpg", "Игры");
        Div block2 = createBlock("developers", "https://static0.gamerantimages.com/wordpress/wp-content/uploads/2020/02/rockstar-logo-teasers.jpg", "Разработчик");
        Div block3 = createBlock("publishers", "https://www.sony.com/en/brand/motionlogo/shared/img/thumb_video3.jpg", "Издатель");

        // Добавляем блоки в контейнер
        blockContainer.add(block1, block2, block3);

        // Добавляем контейнер с блоками в основной контент
        getContent().add(blockContainer);
    }

    private Div createBlock(String label, String imageSrc, String text) {
        Div block = new Div();
        block.addClassName("block");
        block.setWidth("200px");
        block.setHeight("200px");
        block.getStyle().set("position", "relative");
        block.getStyle().set("margin", "30px");
        block.getStyle().set("padding", "0px");
        block.getStyle().set("background-color", "#f0f0f0");
        block.getStyle().set("border", "1px solid #ccc");
        block.getStyle().set("border-radius", "5px");
        block.getStyle().set("cursor", "pointer");
        block.getStyle().set("overflow", "hidden"); // Добавляем это, чтобы избежать выхода за пределы

        Image image = new Image(imageSrc, label);
        image.setWidth("100%");
        image.setHeight("100%");
        image.getStyle().set("object-fit", "cover"); // Заливает изображение в блок
        image.getStyle().set("border-radius", "5px");
        block.add(image);

        Div textOverlay = new Div(new Paragraph(text));
        textOverlay.addClassName("glass-effect");
        block.add(textOverlay);
        textOverlay.setWidth("50%");
        block.addClickListener(event -> {
            // Обработчик события клика
            System.out.println("Нажат блок: " + label);
        });

        return block;
    }
}
