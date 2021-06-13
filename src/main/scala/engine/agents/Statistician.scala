package engine.agents

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import engine.Vector2D

import scala.concurrent.duration.FiniteDuration


object Statistician {
    def apply(ref: ActorRef[SupervisorCommand]): Behavior[ReportCommand] = awaitCommand(ref)

    private def awaitCommand(ref: ActorRef[SupervisorCommand]): Behavior[ReportCommand] = Behaviors.receive { (context, message) => message match {
        case GenerateReport(replyTo, timeout) =>
            ref ! PostToEveryCell(GetCellReport(context.self, (timeout * 8) / 10))
            awaitResponse(ref, replyTo, timeout)
    }}

    private def awaitResponse(ref: ActorRef[SupervisorCommand], to: ActorRef[PromiseCommand], timeout: FiniteDuration): Behavior[ReportCommand] = Behaviors.setup { _ =>
        var raw: Map[Vector2D, CellReport] = Map()
        Behaviors.withTimers { timers =>
            timers.startSingleTimer(Finished, timeout)
            Behaviors.receiveMessagePartial {
                case PostCellReport(report, pos) =>
                    raw += ((pos, report))
                    Behaviors.same
                case Finished =>
                    to ! PostReport(Report(raw))
                    awaitCommand(ref)
    }}}
}


object CellStatistician {
    def apply(ref: ActorRef[CellCommand]): Behavior[ReportCommand] = awaitCommand(ref)

    private def awaitCommand(ref: ActorRef[CellCommand]): Behavior[ReportCommand] = Behaviors.receive{ (context, message) => message match {
        case GenerateCellReport(replyTo, timeout, cell) =>
            ref ! GetEveryState(context.self)
            awaitResponse(ref, replyTo, timeout, cell)
    }}

    private def awaitResponse(ref: ActorRef[CellCommand], to: ActorRef[PromiseCommand], timeout: FiniteDuration, cell: Vector2D): Behavior[ReportCommand] = Behaviors.setup { _ =>
        var raw: Map[Patient.Health.Health, Int] = Map()
        Behaviors.withTimers[ReportCommand] { timers =>
            timers.startSingleTimer(Finished, timeout)
            Behaviors.receiveMessagePartial {
                case Finished =>
                    to ! PostCellReport(CellReport(raw), cell)
                    awaitCommand(ref)
                case PostState(Patient.State(hp)) =>
                    raw += ((hp, (raw getOrElse (hp, 0)) + 1))
                    Behaviors.same
    }}}
}