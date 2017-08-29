/**
 * Created by AdminShanwer on 2017/2/24.
 * Developers:Shanwer
 */

import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.layout.*;

public class GUI extends Application {

    public static void main(String[] args) {

        //Start the JavaFX application by calling launch().
        launch(args);
    }

    //Override the init() method.
    public void init() {
    }

    //Override the star() method.
    public void start(Stage myStage) {


        //Give the stage a title
        myStage.setTitle("S_Chat Client");

        //Create a root node. In this case,a flow layout
        //is used,but several alternatives exist.
        FlowPane rootNode = new FlowPane();//创造根节点

        //Create a scene.
        Scene myScene = new Scene(rootNode, 300, 200);//创建场景

        //Set the scene on the stage
        myStage.setScene(myScene);//设置舞台的场景

        //Show the stage and its scene.
        myStage.show();//显示场景
    }
    //Override the stop() method.
    public void stop() {}
}

