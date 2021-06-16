package App

import engine.agents.Config
import javafx.event.ActionEvent
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.TextField
import javafx.scene.{Node, Parent, Scene}
import javafx.stage.Stage

class InitialScene {

  @FXML
  var IRate: TextField = _;
  @FXML
  var MRate: TextField = _;
  @FXML
  var MeanDur: TextField = _;
  @FXML
  var STDDur: TextField = _;
  @FXML
  var Sever: TextField = _;
  @FXML
  var Mobil: TextField = _;
  @FXML
  var Sneeza: TextField = _;
  @FXML
  var PPs: TextField = _;
  @FXML
  var n: TextField = _;
  @FXML
  var m: TextField = _;

  @FXML
  var stage :Stage = _;
  @FXML
  var root : Parent = _;
  @FXML
  var scene : Scene = _;


  @FXML
  def switchTo1(e: ActionEvent): Unit = {
    val root: Parent = FXMLLoader.load(getClass().getResource("InitialScene.fxml"));
    val stage : Stage = ( e.getSource().asInstanceOf[Node]).getScene().getWindow.asInstanceOf[Stage];
    val scene : Scene = new Scene(root)
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  def switchTo2(e: ActionEvent): Unit = {
    val iRate : Double = IRate.getText().toDouble;
    val mRate : Double = MRate.getText().toDouble;
    val mDur : Double = MeanDur.getText().toDouble;
    val sDur : Double = STDDur.getText().toDouble;
    val sever : Double = Sever.getText().toDouble;
    val mobil : Double = Mobil.getText().toDouble;
    val sneeza : Double = Sneeza.getText().toDouble;
    val pplps : Int = PPs.getText().toInt;
    val MVal : Int = m.getText().toInt;
    val NVal : Int = n.getText().toInt;

    val loader :FXMLLoader = new FXMLLoader(getClass().getResource("DisplayValues.fxml"));
    val root: Parent = loader.load();
    val sc2 : Sc2ontrol = loader.getController();
    val stage : Stage = ( e.getSource().asInstanceOf[Node]).getScene().getWindow.asInstanceOf[Stage];
    sc2.displayChosen(iRate, mRate, mDur, sDur,sever, mobil, sneeza, pplps, MVal, NVal, stage)



    val scene : Scene = new Scene(root)
    stage.setScene(scene);
    stage.show();

  }

}
