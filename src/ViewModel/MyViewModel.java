package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.InvalidationListener;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;

import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

public class MyViewModel extends Observable implements Observer {
    public StringProperty imageString,charRowPosition,charColPosition;
    private IModel model;
    int PlayerRow;
    int PlayerCol;
    Solution solution;

    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof IModel){
            PlayerRow= model.getCharRowPosition();
            PlayerCol=model.getCharColPosition();
            solution=model.getSolution();
            setChanged();
            notifyObservers();

        }
    }

    public void generateMaze(int row,int col){
        model.generateMaze(row,col);
    }

    public Maze getMaze(){
        return model.getMaze();
    }

    public void solveMaze(){
        model.solve();
    }

    public void movePlayer(KeyCode direction){
        model.updatePlayerLocation(direction);
    }

    public int getPlayerCol(){
        return model.getCharColPosition();
    }

    public int getPlayerRow(){
        return model.getCharRowPosition();
    }

    public Solution getSolution(){
        return model.getSolution();
    }
}
