package engine.agents

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike
import engine.Vector2D


class AsyncSupervisorSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

    "A Supervisor Agent" must {
        "return Patient State from the given Cell" in {
            val supervisor = testKit.spawn(Supervisor(5, 5, 5))
            val probe = testKit.createTestProbe[PromiseCommand]()
            supervisor ! GetRandomStateAt(Vector2D(3, 3), probe.ref)
            probe.expectMessage(PostState(Patient.State()))
        }

        "Handle patient transition" in {
            val supervisor = testKit.spawn(Supervisor(1, 1, 2))
            val probe = testKit.createTestProbe[PromiseCommand]()

            supervisor ! DebugCell(DebugPatient(Move), Vector2D(0, 0))

            // Async tests are dumb
            Thread.sleep(500)

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
    }
}