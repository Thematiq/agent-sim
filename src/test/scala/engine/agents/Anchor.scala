package engine.agents

import engine.Vector2D
import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future
import scala.util.Success


class AnchorSpec extends AnyFlatSpec with should.Matchers with ScalaFutures {
    "An Anchor" should "return promise of the patient state" in {
        val anchor = Anchor()
        val promise: Future[Patient.State] = anchor.probeRandomAt(Vector2D(3, 3))

        implicit val ec = anchor.ec

        promise map { x => assert(x == Patient.State())}
    }
}