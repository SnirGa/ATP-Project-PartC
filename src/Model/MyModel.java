package Model;

import Server.Configurations;
import Client.Client;
import IO.MyDecompressorInputStream;
import Server.Server;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import Client.IClientStrategy;
import test.Main;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MyModel extends Observable implements IModel  {
    private int PlayerRow;
    private int PlayerColumn;
    private Maze maze;
    private MazeCharacter mazeCharacter;
    private Solution solution;
    private Server serverMazeGenerator;
    private Server serverMazeSolver;
    private ExecutorService threadPool;

    public MyModel() {

        this.mazeCharacter = new MazeCharacter();
        Configurations conf=Configurations.getInstance();
        this.serverMazeGenerator = new Server(5400,1000,new ServerStrategyGenerateMaze());
        this.serverMazeSolver = new Server(5401,1000,new ServerStrategySolveSearchProblem());
        this.serverMazeGenerator.start();
        this.serverMazeSolver.start();
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void stopAllCommunication() {

    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public int getCharColPosition() {
        return PlayerColumn;
    }

    @Override
    public int getCharRowPosition() {
        return PlayerRow;
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);

    }

    @Override
    public void generateMaze(int rows, int cols) {
        try{
            Client client=new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] MazeDims = new int[]{rows, cols};
                        toServer.writeObject(MazeDims);
                        toServer.flush();
                        byte[] CompressedMaze = (byte[]) fromServer.readObject();
                        InputStream dis = new MyDecompressorInputStream(new ByteArrayInputStream(CompressedMaze));
                        byte[] DecompressedMaze = new byte[MazeDims[0] * MazeDims[1] + 12];
                        dis.read(DecompressedMaze);
                        maze = new Maze(DecompressedMaze);
                        toServer.close();
                        fromServer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
            //mazeCharacter...
            this.PlayerRow=0;
            this.PlayerColumn=0;
            setChanged();
            notifyObservers("Maze generated");

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void solve() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                        public void clientStrategy(InputStream inFromServer,
                                                   OutputStream outToServer) {
                            try {
                                ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                                ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                                toServer.flush();
                                toServer.writeObject(maze); //send maze to server
                                toServer.flush();
                                Solution mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed withMyCompressor) from server%s", mazeSolution));//Print Maze Solution retrieved from the server

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            client.communicateWithServer();
            setChanged();
            notifyObservers("Maze solved");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSolvingAlgorithm(String algorithm) {
        Configurations conf=Configurations.getInstance();
        conf.setSolutionType(algorithm);
    }

    @Override
    public void updatePlayerLocation(KeyCode direction) {
        switch (direction){
            case UP,W,NUMPAD8->{
                if(PlayerRow>0 && maze.getArray()[PlayerRow-1][PlayerColumn]==0){
                    changePlayerPos(PlayerRow-1,PlayerColumn);
                }
                else{
                    break;
                }
            }
            case DOWN,S,NUMPAD2->{
                int rows=maze.getArray().length;
                if(PlayerRow<rows-1 && maze.getArray()[PlayerRow+1][PlayerColumn]==0){
                    changePlayerPos(PlayerRow+1,PlayerColumn);
                }
                else{
                    break;
                }
            }
            case LEFT,A,NUMPAD4->{
                if(PlayerColumn>0 && maze.getArray()[PlayerRow][PlayerColumn-1]==0){
                    changePlayerPos(PlayerRow,PlayerColumn-1);
                }
                else{
                    break;
                }
            }


            case RIGHT,D,NUMPAD6->{
                int cols=maze.getArray()[0].length;
                if(PlayerColumn<cols-1 && maze.getArray()[PlayerRow][PlayerColumn+1]==0){
                    changePlayerPos(PlayerRow,PlayerColumn+1);
                }
                else{
                    break;
                }
            }

            case NUMPAD7 -> {
                if(PlayerColumn>0 && PlayerRow>0 && maze.getArray()[PlayerRow-1][PlayerColumn-1]==0){
                    changePlayerPos(PlayerRow-1,PlayerColumn-1);

                }
                else{
                    break;
                }
            }

            case NUMPAD9 -> {
                int cols=maze.getArray()[0].length;
                if(PlayerRow>0 && PlayerColumn<cols-1 && maze.getArray()[PlayerRow-1][PlayerColumn+1]==0){
                    changePlayerPos(PlayerRow-1,PlayerColumn+1);

                }
                else{
                    break;
                }
            }
            case NUMPAD1 -> {
                int rows=maze.getArray().length;
                if(PlayerColumn>0 && PlayerRow<rows-1 && maze.getArray()[PlayerRow+1][PlayerColumn-1]==0){
                changePlayerPos(PlayerRow+1,PlayerColumn-1);
                }
                else{
                    break;
                }
            }

            case NUMPAD3 -> {
                int rows=maze.getArray().length;
                int cols=maze.getArray()[0].length;
                if(PlayerColumn<cols-1 && PlayerRow<rows-1 && maze.getArray()[PlayerRow+1][PlayerColumn+1]==0){
                    changePlayerPos(PlayerRow+1,PlayerColumn+1);

                }
                else{
                    break;
                }
            }


        }



}

    @Override
    public void setGenerateAlgorithm(String algorithm) {
        Configurations conf=Configurations.getInstance();
        conf.setMazeGenerator(algorithm);
    }

    @Override
    public void setThreadSize(String size) {
        Configurations conf=Configurations.getInstance();
        conf.setThreadSize(size);
    }

    public void changePlayerPos(int newRow,int newCol){
        this.PlayerColumn=newCol;
        this.PlayerRow=newRow;
        setChanged();
        notifyObservers("Player moved");
}

    public void saveCurrentMaze(String name) {
        try{
            if(maze==null){
                return;
            }
            FileOutputStream fileOutputStream=new FileOutputStream(name);
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(maze);
            //objectOutputStream.writeObject("|");
            objectOutputStream.writeObject(PlayerRow);
            //objectOutputStream.writeObject("|");
            objectOutputStream.writeObject(PlayerColumn);
            fileOutputStream.flush();
            objectOutputStream.flush();
            fileOutputStream.close();
            objectOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loadMaze(File filename){
        if (!filename.exists()){
            return false;
        }
        try {
            FileInputStream fileInputStream=new FileInputStream(filename);
            ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
            Maze maze=(Maze)objectInputStream.readObject();
            int Prow=(int)objectInputStream.readObject();
            int Pcol=(int)objectInputStream.readObject();
            maze.print();
            System.out.println(Prow);
            System.out.println(Prow);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;

    }





//    @Override
//    public void setCharacterPosition(int charRowPositionI, int charColPositionI) {
//
//    }
//
//    @Override
//    public String getMainCharacterDirection() {
//        return null;
//    }
//
//    @Override
//    public boolean isAtTheEnd() {
//        return false;
//    }
//
//    @Override
//    public void saveCurrentMaze(File file, String name) {
//
//    }
//
//    @Override
//    public void saveOriginalMaze(File file, String name) {
//
//    }
//
//    @Override
//    public boolean loadMaze(File file) {
//        return false;
//    }
//
//    @Override
//    public MazeCharacter getLoadedCharacter() {
//        return null;
//    }
//
//    @Override
//    public void setCharacter(String name, String url) {
//
//    }
//
//    @Override
//    public void init() {
//
//    }

    @Override
    public void initForMain() {

    }
}
