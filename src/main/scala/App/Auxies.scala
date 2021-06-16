package App

case class Auxies() {
  def countNearest(a: Int): Int = {
    var x: Double = scala.math.sqrt(a);
    x = x + 1;
    x.toInt;
  }
  def main(Args : Array[String]): Unit ={
    print(countNearest(13));
    print(countNearest(17));
    print(countNearest(8));
    print(countNearest(60));
  }
}

object Auxies {
  def countNearest(a: Int): Int = {
    var x: Double = scala.math.sqrt(a);
    x = x + 1;
    x.toInt;
  }
}
