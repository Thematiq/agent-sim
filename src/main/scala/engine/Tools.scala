package engine

import java.util.Random

object Tools {
    private val random = new Random()

    // java.util.Random is thread safe
    def decide(prob: Double): Boolean = random.nextDouble <= prob
    def getRandomElement[T](vec: Vector[T]): T = vec(random.nextInt(vec.length))
    def sampleGauss(mean: Double, std: Double): Double = (random.nextGaussian * std) + mean
}