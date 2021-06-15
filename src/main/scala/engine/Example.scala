package engine

import engine.agents._

import scala.util.{Failure, Success}
import scala.concurrent.duration.DurationInt


object Example {
    def main(args: Array[String]): Unit = {
        val timeout = 200.milliseconds
        val X = 10
        val Y = 10
        val pop = 20

        val anchor = Anchor(X, Y, pop, timeout, Config())

        println("Hello")

        // Let the system prepare itself
        Thread.sleep(2000)

        anchor.sendCommand(DebugCell(DebugRandomPatient(Inject), Vector2D(0, 0)))

        Thread.sleep(100)

        for (_ <- 1 to 100) {
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
                    val healthy = sub.get(Health.Healthy)
                    val infected = sub.get(Health.Infected)
                    val dead = sub.get(Health.Dead)
                    val recovered = sub.get(Health.Recovered)
                    print(
                        healthy.toString + "/" + infected.toString + "/" +
                        dead.toString + "/" + recovered.toString + "\t"
                    )
                }
                println("")
            }
            println("====== STATS =====")
            for ((k, v) <- report.total) println(k.toString + ": " + v.toString)
            anchor.sendCommand(WorldTick)
            Thread.sleep(timeout.toMillis * 2)
        }
        anchor.close()
    }
}