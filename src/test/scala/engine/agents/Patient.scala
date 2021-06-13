package engine.agents

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike


class AsyncPatientSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

    "A Patient Agent" must {
        "return its state" in {
            val superCell = testKit.createTestProbe[CellCommand]()
            val patientZero = testKit.spawn(Patient(superCell.ref))
            val probe = testKit.createTestProbe[PromiseCommand]()

            patientZero ! GetState(probe.ref)
            probe.expectMessage(PostState(Patient.State()))
            superCell.expectNoMessage()
        }

        "Inform the cell about leaving and accomodate in the new cell" in {
            val cellZero = testKit.createTestProbe[CellCommand]()
            val cellOne = testKit.createTestProbe[CellCommand]()
            val patientZero = testKit.spawn(Patient(cellZero.ref))

            patientZero ! Move
            cellZero.expectMessage(MoveFrom(patientZero))
            cellOne.expectNoMessage()

            patientZero ! Accommodate(cellOne.ref)
            patientZero ! Move
            cellOne.expectMessage(MoveFrom(patientZero))
            cellZero.expectNoMessage()
        }
    }

}