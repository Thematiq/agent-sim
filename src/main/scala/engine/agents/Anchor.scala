package engine.agents

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorSystem, RecipientRef, Scheduler}
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContextExecutor, Future}


class Anchor(val x: Int, val y: Int, val initialPop: Int = 1, time: Timeout = 50.milliseconds, arbitraryTimeout: Timeout = 5.seconds, config: Config) {
    private val system: ActorSystem[SupervisorCommand] = ActorSystem(Supervisor(x, y, initialPop, config), "Controller")
    private val supervisor: RecipientRef[SupervisorCommand] = system.ref

    implicit private val timeout: Timeout = arbitraryTimeout
    implicit private val scheduler: Scheduler = system.scheduler
    implicit val ec: ExecutionContextExecutor = system.executionContext

    def probeReport(): Future[Report] = supervisor.ask(ref => GetReport(ref, (time.duration * 8) / 10)).flatMap {
        case PostReport(report) => Future.successful(report)
        case _ => Future.failed(new RuntimeException())
    }

    def close(): Unit = {
        supervisor ! Poison
        system.terminate()
    }

    def sendCommand(cmd: SupervisorCommand): Unit = supervisor ! cmd
}

object Anchor {
    def apply(x: Int = 5, y: Int = 5, pop: Int = 5, timeout: Timeout = 50.milliseconds, config: Config) = new Anchor(x, y, pop, timeout, config = config)
}
