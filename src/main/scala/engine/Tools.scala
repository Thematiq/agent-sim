package engine

import java.util.Random


object Tools {
    private val random = new Random()

    def getRandomElement[T](vec: Vector[T]): T = vec(random.nextInt(vec.length))
}