package App

import engine.Vector2D
import engine.agents.{Anchor, Config, DebugCell, DebugRandomPatient, Health, Inject, Report, WorldTick}
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.{Node, Parent, Scene}
import javafx.scene.control.{Button, Label}
import javafx.scene.layout.{AnchorPane, GridPane, HBox}
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class Sc2ontrol {

  @FXML
  var PlaceForMap: AnchorPane = _;
  var cfgg :Config = _;
  var TTime = 200.milliseconds;
  var N :Int = _;
  var M :Int = _;
  var PPS:Int = _;
  var Stg: Stage = _;
  @FXML
  var gloryButton: Button = _;


  @FXML
  def displayChosen( infectionRate: Double,
                     mortalityRate: Double,
                     durationMean : Double,
                     durationStd: Double,
                     severity: Double,
                     mobility: Double,
                     sneezability: Double,
                     peoplePerSquare: Int,
                     n : Int,
                     m : Int,
                     stg : Stage): Unit = {


    val cfg = Config.apply(infectionRate, mortalityRate, durationMean, durationStd, severity, mobility, sneezability);
    cfgg = cfg;
    N = n;
    M = m;
    PPS = peoplePerSquare;
    Stg = stg;
  }

  @FXML
  def fireUpThisBaby():Unit={
    val anchor = Anchor(N,M,PPS,TTime, cfgg);

    Thread.sleep(2000)

    anchor.sendCommand(DebugCell(DebugRandomPatient(Inject), Vector2D(0, 0)))

    Thread.sleep(100)
    val gridpane = new GridPane();
    PlaceForMap.getChildren.addAll(gridpane)
    for (_ <- 1 to 100) {


      val future = anchor.probeReport()
      implicit val ec = anchor.ec
      var report: Report = Report(Map())


      future.onComplete {
        case Success(value) => report = value
        case Failure(why) => println("Uh oh " + why.toString)
      }
      Thread.sleep(TTime.toMillis * 2)
      var sum = 0

      for (y <- 0 to N) {
        for (x <- 0 to M) {
          val npane :GridPane = new GridPane
          val sub = report.summary(Vector2D(x, y))
          val healthy = sub.get(Health.Healthy)
          val infected = sub.get(Health.Infected)
          val dead = sub.get(Health.Dead)
          val recovered = sub.get(Health.Recovered)
          val size :Int = Auxies.countNearest(healthy+infected+dead+recovered);
          var s:Int = 0
          var ctr:Int = 0

          while (ctr < healthy){
            val healthyRec: Rectangle = new Rectangle
            healthyRec.setFill(Color.GREEN);
            npane.add(healthyRec,s%M, s/N)
            ctr+=1;
            s+=1;
          }
          ctr = 0;
          while(ctr< infected){
            val ungealthyRec: Rectangle = new Rectangle
            ungealthyRec.setFill(Color.RED)
            npane.add(ungealthyRec,s%M, s/N)
            ctr+=1;
            s+=1;
          }
          ctr = 0;
          while (ctr < recovered){
            val recoveredRectangle : Rectangle = new Rectangle
            recoveredRectangle.setFill(Color.BLUE)
            npane.add(recoveredRectangle,s%M, s/N)
            ctr+=1;
            s+=1;
          }
          ctr = 0;
          while(ctr< dead){
            val deadRectangle :Rectangle = new Rectangle
            deadRectangle.setFill(Color.BLACK)
            npane.add(deadRectangle,s%M, s/N)
            ctr+=1;
            s+=1;
          }
          gridpane.add(npane,y,x);
        }
      }
      val root: Parent = FXMLLoader.load(getClass().getResource("DisplayValues.fxml"));
      val stage = Stg;
      val scene : Scene = new Scene(root)
      stage.setScene(scene);
      stage.show();

      anchor.sendCommand(WorldTick)
      Thread.sleep(TTime.toMillis * 2)

    }
    anchor.close()
  }


}
