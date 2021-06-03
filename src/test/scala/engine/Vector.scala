import engine.{Vector2D}
import org.scalatest._
import flatspec._
import matchers._


class Vector2DSpec extends AnyFlatSpec with should.Matchers {
    "A Vector2D" should "sum two Vectors with +" in {
        val a = Vector2D(1, 3)
        val b = Vector2D(3, 5)
        val c = Vector2D(6, 6)
        (a + b) should be (Vector2D(4, 8))
        (a + c) should be (Vector2D(7, 9))
        (b + c) should be (Vector2D(9, 11))
        ((a + b) + c) should be (a + (b + c))
    }

    it should "subtract two Vectors with - and return opposite vector" in {
        val a = Vector2D(1, 3)
        val b = Vector2D(3, 5)
        (a - b) should be (Vector2D(-2, -2))
        (b - a) should be (-(a - b))
        (-a) should be (Vector2D(-1, -3))
    }

    it should "scale Vector by a scalar with * and divide using /" in {
        val a = Vector2D(3, 5)
        (a * 2) should be (Vector2D(6, 10))
        (a * 2.5) should be (Vector2D(7, 12))
        (a / 2) should be (Vector2D(1, 2))
        (a / 2) should be (a * (1.0 / 2.0))
    }
}
