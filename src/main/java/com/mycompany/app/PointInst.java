package com.mycompany.app;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PointInst {
    Point point;
    String name;
    List<String> fishes;
    ArrayList<String> data;
    String image;
    public PointInst(MapView mapView, double lon, double lat, String name){
        this.point = new Point(lon, lat, SpatialReferences.getWgs84());
        this.name = name;
        String split_name = name.replaceAll(" ", "");
        split_name = split_name.substring(0,1) + split_name.substring(1);
        this.image = split_name + ".png";
    }

    public PointInst(MapView mapView, double lon, double lat, String name, String image){
        this.point = new Point(lon, lat, SpatialReferences.getWgs84());
        this.name = name;
        this.image = "../"+image;
    }

    public Point getPoint(){
        return this.point;
    }

    public void setData(ArrayList<String> data){
        this.data = data;
    }

    public void createGraphicDialog(MapView mapView, List<String[]> status) {
        try {
            //Create Stage
            Stage newWindow = new Stage();
            newWindow.setTitle(this.name);
            String img_location;
            try{
                if(new File("/Users/ritvi/ritvi/IdeaProjects/java-gradle-starter-project/src/main/java/com/mycompany/app/waterImages2/"+this.image).exists()){
                    img_location = new File("/Users/ritvi/ritvi/IdeaProjects/java-gradle-starter-project/src/main/java/com/mycompany/app/waterImages2/"+this.image).toURI().toString();
                } else { //If we can not display an image, then display this as backup
                    img_location = new File("/Users/ritvi/ritvi/IdeaProjects/java-gradle-starter-project/src/main/java/com/mycompany/app/default.jpeg").toURI().toString();
                }
            } catch (Exception e){
                img_location = new File("/Users/ritvi/ritvi/IdeaProjects/java-gradle-starter-project/src/main/java/com/mycompany/app/default.jpeg").toURI().toString();
            }

            newWindow.setMaxWidth(200);
            newWindow.setMaxHeight(500);

            ImageView icon = new ImageView(img_location);
            icon.setFitHeight(100);
            icon.setFitWidth(100);


            List<String> values = new ArrayList<>();
            for (int i = 1; i < this.data.size(); i++) {
                values.add(data.get(i));
            }

            ObservableList l = FXCollections.observableList(values);
            ListView v = new ListView(l);
            Text t = new Text(this.name);

            v.setOnMouseClicked(e -> {
                String fish = e.getTarget().toString();
                String fish_name = "";
                if(fish.startsWith("List")){
                    fish_name = e.getTarget().toString().split("'")[1];
                }else{
                    fish_name = e.getTarget().toString().split("\"")[1];
                }
                Alert dialog = new Alert(Alert.AlertType.INFORMATION);


//                try{
//                    img_location = new File("/Users/ritvi/ritvi/IdeaProjects/java-gradle-starter-project/src/main/java/com/mycompany/app/waterImages2/"+this.image).toURI().toString();
//                } catch (Exception e){
//                    img_location = new File("/Users/ritvi/ritvi/IdeaProjects/java-gradle-starter-project/src/main/java/com/mycompany/app/default.jpeg").toURI().toString();
//                }

                String fish_location;
                try{
                    if(new File("/Users/ritvi/ritvi/IdeaProjects/java-gradle-starter-project/src/main/java/com/mycompany/app/fish-img/" + fish_name.replaceAll(" ", "_") + ".jpg").exists()) {
                        fish_location = new File("/Users/ritvi/ritvi/IdeaProjects/java-gradle-starter-project/src/main/java/com/mycompany/app/fish-img/" + fish_name.replaceAll(" ", "_") + ".jpg").toURI().toString();
                    }else{
                        fish_location = new File("/Users/ritvi/ritvi/IdeaProjects/java-gradle-starter-project/src/main/java/com/mycompany/app/default.jpeg").toURI().toString();
                    }
                } catch (Exception r){
                    fish_location = new File("/Users/ritvi/ritvi/IdeaProjects/java-gradle-starter-project/src/main/java/com/mycompany/app/default.jpeg").toURI().toString();
                }

                ImageView fish_icon = new ImageView(fish_location);
                fish_icon.setFitHeight(100);
                fish_icon.setFitWidth(100);
                dialog.getDialogPane().setGraphic(fish_icon);

                dialog.initOwner(mapView.getScene().getWindow());
                dialog.setHeaderText(null);
                dialog.setTitle(fish_name);
                String text = "Unknown" + fish_name;
                for(int i = 0; i < status.size(); i++){
//                    System.out.println(status.get(i)[1]);
                    System.out.println(fish_name +" " + status.get(i)[0]);
                    if(fish_name.strip().equalsIgnoreCase(status.get(i)[0].strip())){
                        text = status.get(i)[1];
                        break;
                    }
                }

                dialog.setContentText(text);
                dialog.showAndWait();
                newWindow.toFront();
            });

            Button button = new Button("OK");
            t.maxWidth(newWindow.getWidth()/2);
            button.setOnAction(event -> {
                newWindow.close();
            });

            VBox container = new VBox(t,icon,v, button);
            container.setSpacing(15);
            container.setPadding(new Insets(25));
            container.setAlignment(Pos.CENTER);
            newWindow.setScene(new Scene(container));
            newWindow.show();

        } catch (Exception e) {
            // on any error, display the stack trace
            e.printStackTrace();
        }
    }

    public String getName(){
        return this.name;
    }
}
