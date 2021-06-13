package engine.agents

import engine.Vector2D
import org.scalatest._
import flatspec._
import matchers._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Success


class AnchorSpec extends AnyFlatSpec with should.Matchers with ScalaFutures {
    "An Anchor" should "return promise of the patient state" in {
        val anchor = Anchor()

        // Read data
        val report: Future[Report] = anchor.probeReport()
        implicit val ec = anchor.ec

        report map { x => assert(x.isInstanceOf[Report])}
    }
}