package engine.agents

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike
import engine.Vector2D


class AsyncCellSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

    val config = Config()

    "A Cell Agent" must {
        "return random Patient State" in {
            val supZero = testKit.createTestProbe[SupervisorCommand]()
            val cellZero = testKit.spawn(Cell(supZero.ref, 2, Vector2D(0, 0), config))
            val probe = testKit.createTestProbe[PromiseCommand]()
            cellZero ! GetRandomState(probe.ref)
            probe.expectMessage(PostState(Patient.State(config = config)))
            supZero.expectNoMessage()
        }
        "return population of the cell" in {
            val supZero = testKit.createTestProbe[SupervisorCommand]()
            val cellZero = testKit.spawn((Cell(supZero.ref, 2, Vector2D(0, 0), config)))
            val probe = testKit.createTestProbe[PromiseCommand]()
            cellZero ! GetPopulation(probe.ref)
            probe.expectMessage(PostPopulation(2))
            supZero.expectNoMessage()
        }

        "leave the patient after moving out and accommodate in the new cell" in {
            val supZero = testKit.createTestProbe[SupervisorCommand]()
            val cellZero = testKit.spawn(Cell(supZero.ref, 2, Vector2D(0, 0), config))
            val cellOne = testKit.spawn(Cell(supZero.ref, 2, Vector2D(1, 1), config))
            val probe = testKit.createTestProbe[PromiseCommand]()

            cellZero ! DebugRandomPatient(Move)
            val msg = supZero.expectMessageType[MoveFromCell]
            msg.pos should be (Vector2D(0, 0))
            cellZero ! GetPopulation(probe.ref)
            probe.expectMessage(PostPopulation(1))

            cellOne ! MoveTo(msg.patient)
            cellOne ! GetPopulation(probe.ref)
            probe.expectMessage(PostPopulation(3))
        }

        "generate report using Statistician Agent" in {
            val supZero = testKit.createTestProbe[SupervisorCommand]()
            val cellZero = testKit.spawn(Cell(supZero.ref, 2, Vector2D(0, 0), config))
            val probe = testKit.createTestProbe[PromiseCommand]()

            cellZero ! GetCellReport(probe.ref)
            supZero.expectNoMessage()
            val msg = probe.expectMessageType[PostCellReport]
            msg.pos should be (Vector2D(0, 0))
            msg.report.summary foreach ( k =>
                if (k._1 == Health.Healthy) k._2 should be (2)
                else k._2 should be (0)
            )
        }
    }
}