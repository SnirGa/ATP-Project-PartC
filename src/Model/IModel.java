package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.Observer;

public interface IModel {
    //void stopAllCommunication();
    Maze getMaze();
    int getCharColPosition();
    int getCharRowPosition();
    Solution getSolution();
    void assignObserver(Observer o);
    void generateMaze(int rows, int cols);
    void solve();
    void setSolvingAlgorithm(String algorithm);
    void updatePlayerLocation(KeyCode direction);

    void setGenerateAlgorithm(String algorithm);
    void setThreadSize(String size);
    void changePlayerPos(int newRow,int newCol);
    //String getMainCharacterDirection();
    //boolean isAtTheEnd();
    void saveCurrentMaze(String name);
    //void saveOriginalMaze(File file, String name);
    //boolean loadMaze(File file);
    //MazeCharacter getLoadedCharacter();
    //void setCharacter(String name, String url);
    //void init();

    void initForMain();
}