package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.scene.control.Alert;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

public class MyViewController implements IView, Observer {
    private MyViewModel viewModel;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    public Label playerRow;
    public Label playerCol;

    public void generateMaze(int rows,int cols){
        //int rows=Integer.valueOf(textfield_mazeRows)
    }

    public void solveMaze(ActionEvent actionEvent){
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        //alert.setContentText();
    }
    public void displayMaze(Maze M){
            
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}

