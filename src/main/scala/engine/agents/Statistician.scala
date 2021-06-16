package engine.agents

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import engine.Vector2D

import scala.concurrent.duration.FiniteDuration


object Statistician {
    def apply(ref: ActorRef[SupervisorCommand], cells: Int): Behavior[ReportCommand] = awaitCommand(ref, cells)

    private def awaitCommand(ref: ActorRef[SupervisorCommand], cells: Int): Behavior[ReportCommand] = Behaviors.receive { (context, message) => message match {
        case GenerateReport(replyTo, timeout) =>
            context.log.info("Received report request")
            ref ! PostToEveryCell(GetCellReport(context.self, (timeout * 5) / 10))
            awaitResponse(ref, replyTo, cells, cells, Map())
        case e =>
            context.log.debug("Received ignored message {}", e)
            Behaviors.unhandled
    }}

    private def awaitResponse(ref: ActorRef[SupervisorCommand], to: ActorRef[PromiseCommand], cells: Int, cellsLeft: Int, raw: Map[Vector2D, CellReport]): Behavior[ReportCommand] =
        Behaviors.receiveMessage {
                case PostCellReport(report, pos) =>
                    val nraw = raw + ((pos, report))
                    if (cellsLeft == 1) {
                        to ! PostReport(Report(nraw))
                        awaitCommand(ref, cells)
                    } else {
                        awaitResponse(ref, to, cells, cellsLeft - 1, nraw)
                    }
                case Finished =>
                    to ! PostReport(Report(raw))
                    awaitCommand(ref, cells)
    }
}


object CellStatistician {
    def apply(ref: ActorRef[CellCommand]): Behavior[ReportCommand] = awaitCommand(ref)

    private def awaitCommand(ref: ActorRef[CellCommand]): Behavior[ReportCommand] = Behaviors.receive{ (context, message) => message match {
        case GenerateCellReport(replyTo, timeout, cell) =>
            ref ! GetEveryState(context.self)
            awaitResponse(ref, replyTo, timeout, cell)
    }}

    private def awaitResponse(ref: ActorRef[CellCommand], to: ActorRef[PromiseCommand], timeout: FiniteDuration, cell: Vector2D): Behavior[ReportCommand] = Behaviors.setup { _ =>
        var raw: Map[Health.Health, Int] = Map()
        Behaviors.withTimers[ReportCommand] { timers =>
            timers.startSingleTimer(Finished, timeout)
            Behaviors.receiveMessagePartial {
                case Finished =>
                    to ! PostCellReport(CellReport(raw), cell)
                    awaitCommand(ref)
                case PostState(Patient.State(hp, _)) =>
                    raw += ((hp, (raw getOrElse (hp, 0)) + 1))
                    Behaviors.same
    }}}
}