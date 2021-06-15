package engine

import engine.agents.{Anchor, Health, Report, WorldTick}

import scala.util.{Failure, Success}
import scala.concurrent.duration.DurationInt


object Example {
    def main(args: Array[String]): Unit = {
        val timeout = 100.milliseconds
        val X = 5
        val Y = 5
        val pop = 5

        val anchor = Anchor(X, Y, pop, timeout)

        println("Hello")

        // Let the system prepare itself
        Thread.sleep(2000)

        for (_ <- 1 to 5) {
            val future = anchor.probeReport()
            implicit val ec = anchor.ec
            var report: Report = Report(Map())

            future.onComplete {
                case Success(value) => report = value
                case Failure(why) => println("Uh oh " + why.toString)
            }
            Thread.sleep(timeout.toMillis * 2)
            var sum = 0
            for (y <- 0 to Y) {
                for (x <- 0 to X) {
                    val sub = report.summary(Vector2D(x, y))
                    print(
                        (sub.summary getOrElse(Health.Healthy, 0)).toString + "/" +
                            (sub.summary getOrElse(Health.Infected, 0)).toString + "/" +
                            (sub.summary getOrElse(Health.Recovered, 0)).toString + "/" +
                            (sub.summary getOrElse(Health.Dead, 0)).toString + "\t"
                    )
                    sum += sub.summary.values.sum
                }
                println("")
            }
            println("!!!! TOTAL SUM: " + sum.toString)
            anchor.sendCommand(WorldTick)
            Thread.sleep(timeout.toMillis * 2)
        }
        anchor.close()
    }
}