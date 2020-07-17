package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    int iterations = 0, numProd = 0, stockVar;
    double priceVar;

    HashMap<VBox,Double> price = new HashMap<>();
    HashMap<VBox,Integer> stock = new HashMap<>();
    HashMap<VBox,Integer> units = new HashMap<>();
    HashMap<VBox,Double> total = new HashMap<>();
    HashMap<VBox,HBox> windows = new HashMap<>();

    ImageView images(File f){
        Image image = new Image(f.toURI().toString(), 200, 200, true, true);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    Double fill(HashMap<VBox,Double> add) {
        Double local = 0.0;
        for (Map.Entry x: add.entrySet())
            local += (Double) x.getValue();
        return local;
    }

    Integer counter(HashMap<VBox,Integer> add) {
        int local = 0;
        for (Map.Entry x: add.entrySet())
            local += (Integer) x.getValue();
        return local;
    }

    String genTotal(VBox box) {
        return String.format("%d x %.2f€ TOTAL: %.2f€", units.get(box), price.get(box), total.get(box));
    }

    void setPrice(HashMap<VBox,HBox> mapa, String cadena, VBox index) {
        ((Text)((VBox)mapa.get(index).getChildren().get(1)).getChildren().get(1)).setText(cadena);
    }

    void increasePrice(VBox box, FlowPane pane, HashMap<VBox,Double> mapa) {
        if (stock.get(box) > 0) {
            units.put(box, units.get(box) + 1);
            stock.put(box, stock.get(box) - 1);
            total.put(box, units.get(box) * price.get(box));
            setPayment(pane,mapa,1);
        }
    }

    void setUpdate(FlowPane pane, VBox box, String num){
        units.put(box, Integer.parseInt(num));
        total.put(box, units.get(box) * price.get(box));
        pane.getChildren().remove(pane.getChildren().size()-1);
        pane.getChildren().add(new Text(String.format("Precio total: %.2f€", fill(total))));
        setPrice(windows,genTotal(box),box);
    }

    void setPayment(FlowPane pane, HashMap<VBox,Double> map, int pos) {
        if (pane.getChildren().size() > 1) pane.getChildren().remove(pane.getChildren().size()-pos);
        pane.getChildren().add(new Text(String.format("Total price: %.2f€", fill(map))));
    }

    @Override
    public void start(Stage stage) {
        BorderPane mainPan = new BorderPane();
        mainPan.setId("main");
        stage.setTitle("Book store");
        Scene scene = new Scene(mainPan, 1165, 650);

        HBox upperBar = new HBox();
        upperBar.setStyle("-fx-background-color: #000000");
        upperBar.setPadding(new Insets(35));;
        Text title = new Text("Your book store");
        title.setFont((new Font(40)));
        title.setFill(Color.WHITE);
        upperBar.setAlignment(Pos.CENTER);

        Circle circle = new Circle(10);
        circle.setFill(Color.RED);
        Text counter = new Text("0");
        counter.setFont(new Font(15));
        counter.setStroke(Color.WHITE);

        StackPane stackPane = new StackPane(new ImageView(new Image(new File("src/sample/trolley.png").toURI().toString(),
                50, 50, true, true)), circle, counter);
        upperBar.getChildren().addAll(title, stackPane);
        StackPane.setAlignment(circle, Pos.TOP_RIGHT);
        StackPane.setAlignment(counter, Pos.TOP_RIGHT);
        StackPane.setMargin(counter,new Insets(0,5,5,0));
        stackPane.setPadding(new Insets(0,0,0,scene.getHeight()/2));
        HBox.setMargin(title,new Insets(0,0,0,scene.getHeight()/2));

        TilePane shopWindow = new TilePane();
        shopWindow.setHgap(20);
        shopWindow.setVgap(20);
        shopWindow.setPadding(new Insets(20));
        shopWindow.setPrefColumns(7);
        ScrollPane scroll = new ScrollPane(shopWindow);

        String PATH = getClass().getResource("stock").toString().substring(6);
        File dir = new File(PATH);
        File[] files = dir.listFiles();

        FlowPane sidePan = new FlowPane();
        ScrollPane scroll2 = new ScrollPane(sidePan);
        sidePan.setPadding(new Insets(10));
        sidePan.setVgap(10);
        sidePan.setHgap(10);
        sidePan.setPrefWidth(320);

        for (var i = 0; i < files.length; i++) {
            VBox box = new VBox();

            Text txt = new Text("Name "+ ++numProd);
            txt.setFont(Font.font("verdana",FontWeight.BOLD, 12));

            priceVar = (Math.random() * 30) + 1;
            Text money = new Text(String.format("%.2f", priceVar).replace(".",",")+"€");
            price.put(box, priceVar);

            stockVar = (int)(Math.random() * 20) + 1;
            Text amount = new Text("Stock: " + stockVar);
            stock.put(box,stockVar);

            ImageView image = images(files[i]);
            box.getChildren().addAll(image,txt,money,amount);
            box.setStyle("-fx-background-color: #FFFFFF");
            box.setPadding(new Insets(12));
            box.setAlignment(Pos.CENTER);

            windows.put(box,new HBox());

            box.setOnMouseClicked(e->{

                if (total.get(box) != null) {
                    increasePrice(box,sidePan,total);
                    setPrice(windows,genTotal(box),box);
                    amount.setText("Stock: "+stock.get(box));
                    counter.setText(counter(units)+"");
                    return;
                } else {

                    shopWindow.setPrefColumns(5);
                    mainPan.setRight(scroll2);

                    units.put(box, 1);
                    total.put(box, price.get(box));
                    stock.put(box,stock.get(box) > 0 ? stock.get(box)-1 : 0);
                    amount.setText("Stock: "+stock.get(box));

                    ImageView imageView = new ImageView(image.getImage());
                    imageView.setFitHeight(75);
                    imageView.setFitWidth(50);

                    Text titu = new Text(txt.getText());
                    Text localMoney = new Text(genTotal(box));

                    TextField input = new TextField();
                    input.setPrefWidth(25);
                    Button refresh = new Button("0");
                    Button remove = new Button("X");
                    HBox interaction = new HBox(input, refresh, remove);
                    interaction.setSpacing(10);

                    VBox data = new VBox(titu, localMoney, interaction);
                    data.setSpacing(10);

                    windows.get(box).getChildren().addAll(imageView, data);
                    windows.get(box).setSpacing(10);
                    windows.get(box).setStyle("-fx-background-color: #E8E8E8");
                    windows.get(box).setPadding(new Insets(20));
                    windows.get(box).setPrefWidth(290);

                    refresh.setOnMouseClicked(f->{
                        if (input.getText().length()>0
                            && Integer.parseInt(input.getText()) <= stock.get(box) + units.get(box))
                            setUpdate(sidePan,box,input.getText());
                        counter.setText(counter(units)+"");
                    });

                    remove.setOnMouseClicked(f->{
                        sidePan.getChildren().remove(windows.get(box));
                        windows.put(box,new HBox());
                        stock.put(box,stock.get(box)+ units.get(box));
                        amount.setText("Stock: "+stock.get(box));
                        units.remove(box);
                        total.remove(box);
                        if (sidePan.getChildren().size() == 1) {
                            sidePan.getChildren().remove(0);
                            shopWindow.setPrefColumns(7);
                            mainPan.setRight(null);
                        }
                        setPayment(sidePan,total,1);
                        counter.setText(counter(units)+"");
                    });

                    counter.setText(counter(units)+"");
                    sidePan.getChildren().add(windows.get(box));
                    setPayment(sidePan,total,2);
                }
            });

            shopWindow.getChildren().add(box);
            if (iterations < 2 && i+1 == files.length) {
                iterations++;
                i = -1;
            }
        }

        mainPan.setCenter(scroll);
        mainPan.setTop(upperBar);
        scene.getStylesheets().add(getClass().getResource("myStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {launch(args);}
}