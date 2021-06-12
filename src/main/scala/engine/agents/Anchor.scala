package engine.agents

import akka.actor.typed.scaladsl.AskPattern.Askable
import engine.Vector2D
import akka.actor.typed.{ActorSystem, RecipientRef, Scheduler}
import akka.util.Timeout

import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration.DurationInt


class Anchor(val x: Int, val y: Int, val initialPop: Int = 1) {
    private val system: ActorSystem[SupervisorCommand] = ActorSystem(Supervisor(x, y, initialPop), "Controller")
    private val supervisor: RecipientRef[SupervisorCommand] = system.ref

    implicit private val timeout: Timeout = 1.seconds
    implicit private val scheduler: Scheduler = system.scheduler
    implicit val ec: ExecutionContextExecutor = system.executionContext


    def probeRandomAt(pos: Vector2D): Future[Patient.State] = {
        supervisor.ask(ref => GetRandomStateAt(pos, ref)).flatMap {
            case PostState(state) => Future.successful(state)
        }
    }
}

object Anchor {
    def apply(x: Int = 5, y: Int = 5, pop: Int = 5) = new Anchor(x, y, pop)
}
