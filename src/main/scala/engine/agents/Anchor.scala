package engine.agents

import akka.actor.typed.scaladsl.AskPattern.Askable
import engine.Vector2D
import akka.actor.typed.{ActorSystem, RecipientRef, Scheduler}
import akka.util.Timeout
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


class Anchor(val x: Int, val y: Int, val initialPop: Int = 1, time: Timeout = 50.milliseconds) {
    private val system: ActorSystem[SupervisorCommand] = ActorSystem(Supervisor(x, y, initialPop), "Controller")
    private val supervisor: RecipientRef[SupervisorCommand] = system.ref

    implicit private val timeout: Timeout = time
    implicit private val scheduler: Scheduler = system.scheduler
    implicit val ec: ExecutionContextExecutor = system.executionContext

    private def throwWrongMsg(promise: PromiseCommand): Unit = throw new RuntimeException("Received wrong message type " + promise.toString)

    def probeRandomAt(pos: Vector2D): Future[Patient.State] = {
        supervisor.ask(ref => GetRandomStateAt(pos, ref)).flatMap {
            case PostState(state) => Future.successful(state)
        }
    }

    def probePopAt(pos: Vector2D): Future[Int] = {
        supervisor.ask(ref => GetPopulationAt(pos, ref)).flatMap {
            case PostPopulation(pop) => Future.successful(pop)
        }
    }

    def awaitStateAt(pos: Vector2D): Patient.State = {
        val promise: Future[PromiseCommand] = supervisor.ask(ref => GetRandomStateAt(pos, ref))
        var ret: Patient.State = Patient.State()

        promise.onComplete {
            case Success(PostState(state)) =>
                ret = state
            case Success(other) =>
                throwWrongMsg(other)
            case Failure(ex) =>
                throw ex
        }
        ret
    }

    def awaitPopAt(pos: Vector2D): Int = {
        val promise: Future[PromiseCommand] = supervisor.ask(ref => GetPopulationAt(pos, ref))
        var ret = -1

        promise.onComplete {
            case Success(PostPopulation(pop)) =>
                ret = pop
            case Success(other) =>
                throwWrongMsg(other)
            case Failure(ex) =>
                throw ex
        }
        ret
    }
}

object Anchor {
    def apply(x: Int = 5, y: Int = 5, pop: Int = 5) = new Anchor(x, y, pop)
}
