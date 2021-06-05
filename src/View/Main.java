package View;

import Model.MyModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        MyModel model=new MyModel();
        model.generateMaze(10,10);
        model.saveCurrentMaze("xxx");
        FileInputStream fileInputStream=new FileInputStream("xxx.txt");
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("xxx.txt").getFile());



        model.loadMaze(file);
        //Parent root = FXMLLoader.load(getClass().getResource("MyView.fxml"));
        //primaryStage.setTitle("Hello World");
       // primaryStage.setScene(new Scene(root, 300, 275));
       // primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
