package engine.agents

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem, RecipientRef, Scheduler}
import akka.util.Timeout

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration.DurationInt


class Anchor(val x: Int, val y: Int, val initialPop: Int = 1, time: Timeout = 50.milliseconds) {
    private val system: ActorSystem[SupervisorCommand] = ActorSystem(Supervisor(x, y, initialPop), "Controller")
    private val supervisor: RecipientRef[SupervisorCommand] = system.ref

    implicit private val timeout: Timeout = time
    implicit private val scheduler: Scheduler = system.scheduler
    implicit val ec: ExecutionContextExecutor = system.executionContext

    def probeReport(): Future[Report] = supervisor.ask((ref: ActorRef[PromiseCommand]) => GetReport(ref)).flatMap {
        case PostReport(report) => Future.successful(report)
    }
}

object Anchor {
    def apply(x: Int = 5, y: Int = 5, pop: Int = 5) = new Anchor(x, y, pop)
}
