package engine.agents

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike


class AsyncPatientSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {
    val config = Config()

    "A Patient Agent" must {
        "accept basic commands and react to them" in {
            // Get state
            val superCell = testKit.createTestProbe[CellCommand]()
            val patientZero = testKit.spawn(Patient(superCell.ref, config = config))
            val probe = testKit.createTestProbe[PromiseCommand]()

            patientZero ! GetState(probe.ref)
            probe.expectMessage(PostState(Patient.State(config = config)))
            superCell.expectNoMessage()

            // Inject
            patientZero ! Inject
            patientZero ! GetState(probe.ref)
            probe.expectMessage(PostState(Patient.State(Health.Infected, config = config)))
            // Vaccinate
            patientZero ! Vaccinate
            patientZero ! GetState(probe.ref)
            probe.expectMessage(PostState(Patient.State(Health.Recovered, config = config)))
            // Kill
            patientZero ! Shoot
            patientZero ! GetState(probe.ref)
            probe.expectMessage(PostState(Patient.State(Health.Dead, config = config)))
        }

        "inform the cell about leaving and accommodate in the new cell" in {
            val cellZero = testKit.createTestProbe[CellCommand]()
            val cellOne = testKit.createTestProbe[CellCommand]()
            val patientZero = testKit.spawn(Patient(cellZero.ref, config = config))

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