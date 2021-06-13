package engine

import engine.Vector2D
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

    it should "check whether vectors are preceding or following" in {
        val a = Vector2D()
        val b = Vector2D(1, 1)
        val c = Vector2D(2, 1)
        (a follows b) should be (false)
        (a follows c) should be (false)
        (b follows c) should be (false)

        (c follows a) should be (true)
        (c follows b) should be (true)
        (c follows c) should be (true)
        (c precedes c) should be (true)
    }

    it should "return nhbd of the vector within the borders" in {
        val a = Vector2D()
        val b = Vector2D(1, 1)
        val c = Vector2D(2, 2)

        val nhbd = Vector2D.getNhbd(b, a, c)
        for (x <- 0 to 2; y <- 0 to 2 if x != y) nhbd.contains(Vector2D(x, y)) should be (true)
        nhbd.contains(b) should be (false)

        Vector2D.getNhbd(b, b, b).length should be (0)
    }
}
