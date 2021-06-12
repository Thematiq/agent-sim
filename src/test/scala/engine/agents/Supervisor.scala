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
    }
}