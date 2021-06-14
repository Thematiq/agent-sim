package engine.agents

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike


class AsyncPatientSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

    "A Patient Agent" must {
        "accept basic commands and react to them" in {
            // Get state
            val superCell = testKit.createTestProbe[CellCommand]()
            val patientZero = testKit.spawn(Patient(superCell.ref))
            val probe = testKit.createTestProbe[PromiseCommand]()

            patientZero ! GetState(probe.ref)
            probe.expectMessage(PostState(Patient.State()))
            superCell.expectNoMessage()

            // Inject
            patientZero ! Inject
            patientZero ! GetState(probe.ref)
            probe.expectMessage(PostState(Patient.State(Patient.Health.Infected)))
            // Vaccinate
            patientZero ! Vaccinate
            patientZero ! GetState(probe.ref)
            probe.expectMessage(PostState(Patient.State(Patient.Health.Recovered)))
            // Kill
            patientZero ! Shoot
            patientZero ! GetState(probe.ref)
            probe.expectMessage(PostState(Patient.State(Patient.Health.Dead)))
        }

        "inform the cell about leaving and accomodate in the new cell" in {
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