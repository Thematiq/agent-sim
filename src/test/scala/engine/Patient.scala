import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import engine.agents.{GetState, Patient, PostState, PromiseCommand}
import org.scalatest._
import matchers._
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