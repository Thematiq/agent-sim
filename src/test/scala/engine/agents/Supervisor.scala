package engine.agents

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike
import engine.Vector2D


class AsyncSupervisorSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {
    val config = Config()

    "A Supervisor Agent" must {
        "return Patient State from the given Cell" in {
            val supervisor = testKit.spawn(Supervisor(5, 5, 5, config = config))
            val probe = testKit.createTestProbe[PromiseCommand]()
            supervisor ! GetRandomStateAt(Vector2D(3, 3), probe.ref)
            probe.expectMessage(PostState(Patient.State(config = config)))
        }

        "handle patient transition" in {
            val supervisor = testKit.spawn(Supervisor(1, 1, 2, config = config))
            val probe = testKit.createTestProbe[PromiseCommand]()

            supervisor ! DebugCell(DebugRandomPatient(Move), Vector2D(0, 0))

            // Async tests are dumb
            Thread.sleep(10)

            supervisor ! GetPopulationAt(Vector2D(0, 0), probe.ref)
            probe.expectMessage(PostPopulation(1))

            var anomalies = 0
            for (x <- 0 to 1; y <- 0 to 1) {
                supervisor ! GetPopulationAt(Vector2D(x, y), probe.ref)
                val msg = probe.expectMessageType[PostPopulation]
                if (msg.pop != 2) anomalies += 1
            }

            anomalies should be (2)
        }
        "generate report for every cell using Statistician Agent" in {
            val supervisor = testKit.spawn(Supervisor(1, 1, 2, config = config))
            val probe = testKit.createTestProbe[PromiseCommand]()

            supervisor ! GetReport(probe.ref)
            val msg = probe.expectMessageType[PostReport]

            for (x <- 0 to 1; y <- 0 to 1) {
                (msg.report.summary contains Vector2D(x, y)) should be (true)
                msg.report.summary(Vector2D(x, y)).summary foreach ( k =>
                    if (k._1 == Health.Healthy) k._2 should be (2)
                    else k._2 should be (0)
                )
            }

        }
    }
}