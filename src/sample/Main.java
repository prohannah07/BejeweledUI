package sample;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main extends Application {

    private static final int W = 8;
    private static final int H = 8;
    private static final int SIZE = 100;
    private static final int GEM_SIZE = 80;

    private Color[] colors = new Color[] {
            Color.RED, Color.ORANGE, Color.BLUE, Color.GREEN, Color.YELLOW, Color.GRAY, Color.PURPLE
    };
    Image redGem = new Image("file:assets/red.png");
    Image blueGem = new Image("file:assets/blue.png");
    Image greenGem = new Image("file:assets/green.png");
    Image yellowGem = new Image("file:assets/yellow.png");
    Image orangeGem = new Image("file:assets/orange.png");
    Image whiteGem = new Image("file:assets/white.png");
    Image purpleGem = new Image("file:assets/purple.png");

    private Image[] gems = new Image[]{
            redGem, blueGem, greenGem, yellowGem, orangeGem, whiteGem, purpleGem
    };

    private Jewel selected = null;

    private List<Jewel> jewels;

    private IntegerProperty score = new SimpleIntegerProperty();


    private Parent createContent(){
        Pane root = new Pane();
        root.setPrefSize(W*SIZE + 500, H*SIZE);

        jewels  = IntStream.range(0,W * H)
                .mapToObj(i -> new Point2D(i % W, i / H))
                .map(point -> new Jewel(point))
                .collect(Collectors.toList());

        root.getChildren().addAll(jewels);

        Text textScore = new Text();
        textScore.setTranslateX((W * SIZE));
        textScore.setTranslateY((100));
        textScore.setFont(Font.font((68)));
        textScore.textProperty().bind(score.asString("Score: [%d]"));

        root.getChildren().add(textScore);
        return root;
    }

    //this is basically all the logic that this game has
    private void checkState(){
        Map<Integer, List<Jewel>> rows =  jewels.stream().collect(Collectors.groupingBy(Jewel::getRow));
        Map<Integer, List<Jewel>> columns =  jewels.stream().collect(Collectors.groupingBy(Jewel::getColumn));

        rows.values().forEach(this::checkCombo);
        columns.values().forEach(this::checkCombo);

    }

    private void checkCombo(List<Jewel> jewelsLine){
        Jewel jewel = jewelsLine.get(0);

        long count = jewelsLine.stream().filter(j -> j.getColor() != jewel.getColor()).count();

        if (count ==0){ //all elements same color
            //add score
            score.set(score.get()+1000);
            jewelsLine.forEach(Jewel::randomize);
        }
    }

    private void swap(Jewel a, Jewel b){
        Image color = a.getColor();
        a.setColor(b.getColor());
        b.setColor(color);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }


    private class Jewel extends Parent{
        ImageView gem = new ImageView();
        Point2D point;
        public Jewel(Point2D point){
            this.point = point;
            gem.setImage(gems[new Random().nextInt(gems.length)]);
            gem.setFitWidth(GEM_SIZE);
            gem.setFitHeight(GEM_SIZE);

            setTranslateX(point.getX() * SIZE);
            setTranslateY(point.getY() * SIZE);
            getChildren().add(gem);

            setOnMouseClicked( event -> {
                if (selected == null) {
                    selected = this;
                }
                else {
                    swap(selected, this);
                    checkState();
                    selected = null;
                }
            });
        }

        public void randomize(){
            gem.setImage(gems[new Random().nextInt(gems.length)]);
        }

        public int getColumn(){
            return (int)getTranslateX() / SIZE;
        }

        public int getRow(){
            return (int)getTranslateY() / SIZE;
        }

        public Image getColor() {

            return this.gem.getImage();
        }

        public void setColor(Image color){

            this.gem.setImage(color);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
