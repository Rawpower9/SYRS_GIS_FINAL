/**
 * Copyright 2019 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.mycompany.app;

import com.esri.arcgisruntime.geometry.*;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.*;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;


import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mycompany.app.ParseTxt.*;
import static com.mycompany.app.ParseTxt.readFishStatus;

public class App extends Application {

    private MapView mapView;

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        List<PointInst> points = new ArrayList<PointInst>();
        // set the title and size of the stage and show it
        stage.setTitle("My Map App");
        stage.setWidth(800);
        stage.setHeight(700);
        stage.show();

        // create a JavaFX scene with a stack pane as the root node and add it to the scene
        StackPane stackPane = new StackPane();
        Scene scene = new Scene(stackPane);
        stage.setScene(scene);
        // Note: it is not best practice to store API keys in source code.
        // An API key is required to enable access to services, web maps, and web scenes hosted in ArcGIS Online.
        // If you haven't already, go to your developer dashboard to get your API key.
        // Please refer to https://developers.arcgis.com/java/get-started/ for more information
        String yourApiKey = "AAPKceb0a351064c4540a73c88e6dcd6012c4xzxSUdSzDFZz-AxUzaevmk2CZBxE-OkrIF0KHU-sSR9PtWnxjVetDwiZT9LroRZ";
        ArcGISRuntimeEnvironment.setApiKey(yourApiKey);

        // create a MapView to display the map and add it to the stack pane
        mapView = new MapView();
        stackPane.getChildren().add(mapView);

        // create an ArcGISMap with an imagery basemap
        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_IMAGERY);
        List<ArrayList<String>> fish = readFish("src/main/java/com/mycompany/app/fish.txt");
        List<String[]> latlong = readLongLat("src/main/java/com/mycompany/app/LatLong.txt");
        List<String[]> status = readFishStatus("src/main/java/com/mycompany/app/fishes.txt");
        List<String[]> custom = readCustomFish("src/main/java/com/mycompany/app/data.txt");


        for(String[] i: latlong){
            points.add(new PointInst(mapView, Double.parseDouble(i[2]), Double.parseDouble(i[1]), i[0]));
        }

        for(ArrayList<String> i: fish){
            for(PointInst j : points){
                if(j.getName().equals(i.get(0))){
                    j.setData(i);
                }
            }
        }

        for(String[] i : custom){
            points.add(new PointInst(mapView, Double.parseDouble(i[2]), Double.parseDouble(i[1]), i[0], i[4]));
            ArrayList<String> custom_fishes = new ArrayList<>();
            String[] split_fishes = i[3].split(",");
            for (int j = 0; j < split_fishes.length; j++) {
                custom_fishes.add(split_fishes[j]);
            }
            System.out.println();
            points.get(points.size()-1).setData(custom_fishes);
        }

        mapView.setMap(map);
        mapView.setViewpoint(new Viewpoint(33.328617, -115.843414, 144447.638572));

        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);


//        Point point = new Point(-118.80657463861, 34.0005930608889, SpatialReferences.getWgs84());
//        points.add(point);
        // create an opaque orange point symbol with a opaque blue outline symbol

        for(PointInst p:points) {
            Point point = p.getPoint();
            SimpleMarkerSymbol simpleMarkerSymbol =
                    new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.ORANGE, 10);
            // create a graphic with the point geometry and symbol
            Graphic pointGraphic = new Graphic(point, simpleMarkerSymbol);
            // add the point graphic to the graphics overlay
            graphicsOverlay.getGraphics().add(pointGraphic);
        }





        mapView.setOnMouseClicked(e -> {
            // check that the primary mouse button was clicked and user is not panning
            if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
                // create a point from where we click
                Point2D point2 = new Point2D(e.getX(), e.getY());
                // convert to mapPoint
                Point mapPoint = mapView.screenToLocation(point2);
                Point projectedPoint = (Point)GeometryEngine.project(mapPoint, SpatialReferences.getWgs84());
                for(PointInst i : points){
                    Point point = i.getPoint();
                    if(Math.abs(projectedPoint.getX() - point.getX()) <= 0.01 && Math.abs(projectedPoint.getY() - point.getY()) <= 0.01) {
                        i.createGraphicDialog(mapView, status);
                        break;
                    }
                }
            } else if (e.getButton() == MouseButton.SECONDARY && e.isStillSincePress()) {
            }
        });

    }
    /**
     * Stops and releases all resources used in application.
     */
    @Override
    public void stop() {
        if (mapView != null) {
            mapView.dispose();
        }
    }
}
//
//import java.util.List;
//
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.geometry.Point2D;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Alert.AlertType;
//import javafx.scene.input.MouseButton;
//import javafx.scene.layout.StackPane;
//import javafx.stage.Stage;
//
//import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
//import com.esri.arcgisruntime.concurrent.ListenableFuture;
//import com.esri.arcgisruntime.geometry.Point;
//import com.esri.arcgisruntime.geometry.PointCollection;
//import com.esri.arcgisruntime.geometry.Polygon;
//import com.esri.arcgisruntime.mapping.ArcGISMap;
//import com.esri.arcgisruntime.mapping.BasemapStyle;
//import com.esri.arcgisruntime.mapping.view.Graphic;
//import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
//import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
//import com.esri.arcgisruntime.mapping.view.MapView;
//import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
//
//public class App extends Application {
//
//    private MapView mapView;
//    private GraphicsOverlay graphicsOverlay;
//    private ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphics;
//
//    @Override
//    public void start(Stage stage) {
//
//        try {
//            // create stack pane and application scene
//            StackPane stackPane = new StackPane();
//            Scene scene = new Scene(stackPane);
//
//            // set title, size, and add scene to stage
//            stage.setTitle("Identify Graphics Sample");
//            stage.setWidth(800);
//            stage.setHeight(700);
//            stage.setScene(scene);
//            stage.show();
//
//            // authentication with an API key or named user is required to access basemaps and other location services
//            String yourAPIKey = System.getProperty("apiKey");
//            ArcGISRuntimeEnvironment.setApiKey(yourAPIKey);
//
//            // create a map with the topographic basemap style
//            final ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);
//
//            // create a map view and set the map to it
//            mapView = new MapView();
//            mapView.setMap(map);
//
//            // create a graphics overlay
//            graphicsOverlay = new GraphicsOverlay();
//
//            // add graphics overlay to the map view
//            mapView.getGraphicsOverlays().add(graphicsOverlay);
//
//            // work with the MapView after it has loaded
//            mapView.addSpatialReferenceChangedListener(src -> addGraphicsOverlay());
//
//            mapView.setOnMouseClicked(e -> {
//                if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
//                    // create a point from location clicked
//                    Point2D mapViewPoint = new Point2D(e.getX(), e.getY());
//
//                    // identify graphics on the graphics overlay
//                    identifyGraphics = mapView.identifyGraphicsOverlayAsync(graphicsOverlay, mapViewPoint, 10, false);
//
//                    identifyGraphics.addDoneListener(() -> Platform.runLater(this::createGraphicDialog));
//                }
//            });
//
//            // add the map view to stack pane
//            stackPane.getChildren().add(mapView);
//
//        } catch (Exception e) {
//            // on any error, print stack trace
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Creates four different Graphics and renders them to the GraphicsOverlay.
//     *
//     */
//    private void addGraphicsOverlay() {
//
//        // polygon graphic
//        PointCollection pointsPoly = new PointCollection(mapView.getSpatialReference());
//        pointsPoly.add(new Point(-20E5, 20E5));
//        pointsPoly.add(new Point(20E5, 20E5));
//        pointsPoly.add(new Point(20E5, -20E5));
//        pointsPoly.add(new Point(-20E5, -20E5));
//        // hex code for yellow color
//        int yellowColor = 0xFFFFFF00;
//        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, yellowColor, null);
//        Polygon polygon = new Polygon(pointsPoly);
//        // create graphic from polygon and symbol
//        Graphic polygonGraphic = new Graphic(polygon, fillSymbol);
//        // add the polygon graphic
//        graphicsOverlay.getGraphics().add(polygonGraphic);
//    }
//
//    /**
//     * Indicates when a graphic is clicked by showing an Alert.
//     */
//    private void createGraphicDialog() {
//
//        try {
//            // get the list of graphics returned by identify
//            IdentifyGraphicsOverlayResult result = identifyGraphics.get();
//            List<Graphic> graphics = result.getGraphics();
//
//            if (!graphics.isEmpty()) {
//                // show a alert dialog box if a graphic was returned
//                Alert dialog = new Alert(AlertType.INFORMATION);
//                dialog.initOwner(mapView.getScene().getWindow());
//                dialog.setHeaderText(null);
//                dialog.setTitle("Information Dialog Sample");
//                dialog.setContentText("Clicked on " + graphics.size() + " graphic");
//                dialog.showAndWait();
//            }
//        } catch (Exception e) {
//            // on any error, display the stack trace
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Stops and releases all resources used in application.
//     */
//    @Override
//    public void stop() {
//
//        if (mapView != null) {
//            mapView.dispose();
//        }
//    }
//
//    /**
//     * Opens and runs application.
//     *
//     * @param args arguments passed to this application
//     */
//    public static void main(String[] args) {
//
//        Application.launch(args);
//    }
//
//}