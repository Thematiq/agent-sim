package engine.agents

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike


class AsyncPatientSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

    "A Patient Agent" must {
        "return its state" in {
            val patientZero = testKit.spawn(Patient())
            val probe = testKit.createTestProbe[PromiseCommand]()
            patientZero ! GetState(probe.ref)
            probe.expectMessage(PostState(Patient.State()))
        }
    }
}