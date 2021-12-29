package App

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.layout.{AnchorPane, Pane}
import javafx.scene.paint.Color
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

object MainDisp
{
  def main(args: Array[String])
  {
    Application.launch(classOf[MainDisp], args: _*)
  }
}

class MainDisp extends Application
{
  override def start(primaryStage: Stage)
  {
    try{
      val root : Parent = FXMLLoader.load(getClass().getResource("InitialScene.fxml"));
      val scene : Scene = new Scene(root);
      primaryStage.setScene(scene);
      primaryStage.show;
    }
    catch{
      case e: Exception => e.printStackTrace();
    }
  }

}