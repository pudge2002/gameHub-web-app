package GameHub.views.pages;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import GameHub.model.Developer;
import GameHub.model.Game;
import GameHub.model.Publisher;
import GameHub.repository.DeveloperRepository;
import GameHub.repository.GameRepository;
import GameHub.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Route("dashboard")
@PageTitle("Дашборд")
public class DashboardView extends VerticalLayout {

    @Autowired
    private DeveloperRepository developerRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private GameRepository gameRepository;

    public DashboardView(DeveloperRepository developerRepository, PublisherRepository publisherRepository, GameRepository gameRepository) {
        this.developerRepository = developerRepository;
        this.publisherRepository = publisherRepository;
        this.gameRepository = gameRepository;

        add(createDeveloperChart());
        add(createPublisherChart());
        add(createGenreChart());
    }

    private Chart createDeveloperChart() {
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();
        List<Developer> developers = developerRepository.findAll();

        // Вывод информации о разработчиках в консоль
        developers.forEach(developer -> System.out.println(developer.getName()));

        Map<Integer, Long> developerGameCount = gameRepository.findAll().stream()
                .collect(Collectors.groupingBy(game -> game.getDeveloper_id().getId(), Collectors.counting()));

        // Вывод информации о количестве игр у разработчиков в консоль
        developerGameCount.forEach((developerId, count) -> System.out.println(developerId + ": " + count));

        for (Developer developer : developers) {
            Long gameCount = developerGameCount.getOrDefault(developer.getId(), 0L);
            dataSeries.add(new DataSeriesItem(developer.getName(), gameCount.doubleValue()));
        }

        chart.getConfiguration().setSeries(dataSeries);
        chart.getConfiguration().setTitle("Количество игр у разработчиков");

        return chart;
    }

    private Chart createPublisherChart() {
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();
        List<Publisher> publishers = publisherRepository.findAll();

        // Вывод информации об издателях в консоль
        publishers.forEach(publisher -> System.out.println(publisher.getName()));

        Map<Integer, Long> publisherGameCount = gameRepository.findAll().stream()
                .collect(Collectors.groupingBy(game -> game.getPublisher_id().getId(), Collectors.counting()));

        // Вывод информации о количестве игр у издателей в консоль
        publisherGameCount.forEach((publisherId, count) -> System.out.println(publisherId + ": " + count));

        for (Publisher publisher : publishers) {
            Long gameCount = publisherGameCount.getOrDefault(publisher.getId(), 0L);
            dataSeries.add(new DataSeriesItem(publisher.getName(), gameCount.doubleValue()));
        }

        chart.getConfiguration().setSeries(dataSeries);
        chart.getConfiguration().setTitle("Количество игр у издателей");

        return chart;
    }

    private Chart createGenreChart() {
        Chart chart = new Chart(ChartType.COLUMN);

        Configuration configuration = chart.getConfiguration();
        configuration.setTitle("Игры по жанрам");

        XAxis xAxis = new XAxis();
        xAxis.setType(AxisType.CATEGORY); // Устанавливаем тип оси X как категориальный
        configuration.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setTitle("Количество игр");
        configuration.addyAxis(yAxis);

        DataSeries dataSeries = new DataSeries();
        List<Game> games = gameRepository.findAll();

        // Вывод информации о играх в консоль
        System.out.println("Games:");
        games.forEach(game -> System.out.println(game.getName() + " - " + game.getGenre()));

        Map<String, Long> genreGameCount = games.stream()
                .collect(Collectors.groupingBy(Game::getGenre, Collectors.counting()));

        // Вывод информации о количестве игр по жанрам в консоль
        System.out.println("Genre Game Counts:");
        genreGameCount.forEach((genre, count) -> System.out.println(genre + ": " + count));

        // Установка категорий оси X
        xAxis.setCategories(genreGameCount.keySet().toArray(new String[0]));

        for (Map.Entry<String, Long> entry : genreGameCount.entrySet()) {
            dataSeries.add(new DataSeriesItem(entry.getKey(), entry.getValue().doubleValue()));
            System.out.println("Adding to chart: " + entry.getKey() + " with game count: " + entry.getValue());
        }

        configuration.setSeries(dataSeries);

        return chart;
    }

}
