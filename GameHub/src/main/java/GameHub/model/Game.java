package GameHub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "games") // Указываем имя таблицы, если хотим отличаться от имени класса
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "year")
    private int year;



    @Column(name = "image_title")
    @Lob // Для хранения больших объектов, таких как изображения
    private byte[] image_title;

    @Column(name = "image_back")
    @Lob // Для хранения больших объектов, таких как изображения
    private byte[] image_back;

    @Column(name = "genre")
    private String genre;
    @ManyToOne
    @JoinColumn(name = "developer_id")
    private Developer developer_id;
    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher_id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public byte[] getImage_title() {
        return image_title;
    }

    public void setImage_title(byte[] image_title) {
        this.image_title = image_title;
    }

    public byte[] getImage_back() {
        return image_back;
    }

    public void setImage_back(byte[] image_back) {
        this.image_back = image_back;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Developer getDeveloper_id() {
        return developer_id;
    }

    public void setDeveloper_id(Developer developer_id) {
        this.developer_id = developer_id;
    }

    public Publisher getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(Publisher publisher_id) {
        this.publisher_id = publisher_id;
    }

    public Game() {
    }




}
