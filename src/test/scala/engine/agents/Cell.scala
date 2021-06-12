package engine.agents

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike
import engine.Vector2D


class AsyncCellSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {
    "A Cell Agent" must {
        "return random Patient State" in {
            val cellZero = testKit.spawn(Cell(2, Vector2D(0, 0)))
            val probe = testKit.createTestProbe[PromiseCommand]()
            cellZero ! GetRandomState(probe.ref)
            probe.expectMessage(PostState(Patient.State()))
        }
    }
}