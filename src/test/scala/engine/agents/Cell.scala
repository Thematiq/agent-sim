package engine.agents

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike
import engine.Vector2D


class AsyncCellSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {
    "A Cell Agent" must {
        "return random Patient State" in {
            val supZero = testKit.createTestProbe[SupervisorCommand]()
            val cellZero = testKit.spawn(Cell(supZero.ref, 2, Vector2D(0, 0)))
            val probe = testKit.createTestProbe[PromiseCommand]()
            cellZero ! GetRandomState(probe.ref)
            probe.expectMessage(PostState(Patient.State()))
            supZero.expectNoMessage()
        }
        "return population of the cell" in {
            val supZero = testKit.createTestProbe[SupervisorCommand]()
            val cellZero = testKit.spawn((Cell(supZero.ref, 2, Vector2D(0, 0))))
            val probe = testKit.createTestProbe[PromiseCommand]()
            cellZero ! GetPopulation(probe.ref)
            probe.expectMessage(PostPopulation(2))
            supZero.expectNoMessage()
        }

        "leave the patient after moving out and accommodate in the new cell" in {
            val supZero = testKit.createTestProbe[SupervisorCommand]()
            val cellZero = testKit.spawn(Cell(supZero.ref, 2, Vector2D(0, 0)))
            val cellOne = testKit.spawn(Cell(supZero.ref, 2, Vector2D(1, 1)))
            val probe = testKit.createTestProbe[PromiseCommand]()

            cellZero ! DebugPatient(Move)
            val msg = supZero.expectMessageType[MoveFromCell]
            msg.pos should be (Vector2D(0, 0))
            cellZero ! GetPopulation(probe.ref)
            probe.expectMessage(PostPopulation(1))

            cellOne ! MoveTo(msg.patient)
            cellOne ! GetPopulation(probe.ref)
            probe.expectMessage(PostPopulation(3))
        }
    }
}