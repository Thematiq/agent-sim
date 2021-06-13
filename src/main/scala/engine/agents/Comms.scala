package engine.agents

import engine.{Vector2D, agents}
import akka.actor.typed.ActorRef
import akka.util.Timeout

import scala.concurrent.duration.{DurationInt, FiniteDuration}


sealed trait Command

/**
 * Commands for getting model information outside the Actor ecosystem
 */
sealed trait ReportCommand extends Command
final case class GenerateCellReport(replyTo: ActorRef[PromiseCommand], timeout: FiniteDuration = 50.milliseconds, pos: Vector2D) extends ReportCommand
final case class GenerateReport(replyTo: ActorRef[PromiseCommand], timeout: FiniteDuration = 50.milliseconds) extends ReportCommand
case object Finished extends ReportCommand

sealed trait PromiseCommand extends ReportCommand
final case class PostState(state: Patient.State) extends PromiseCommand
final case class PostPopulation(pop: Int) extends PromiseCommand
final case class PostReport(report: Report) extends PromiseCommand
final case class PostCellReport(report: CellReport, pos: Vector2D) extends PromiseCommand

/**
 * Supervisor commands
 */
sealed trait SupervisorCommand extends Command
final case class GetReport(replyTo: ActorRef[PromiseCommand], timeout: FiniteDuration = 100.milliseconds) extends SupervisorCommand
final case class PostToEveryCell(cmd: CellCommand) extends SupervisorCommand
final case class GetRandomStateAt(pos: Vector2D, replyTo: ActorRef[PromiseCommand]) extends SupervisorCommand
final case class GetPopulationAt(pos: Vector2D, replyTo: ActorRef[PromiseCommand]) extends SupervisorCommand
final case class MoveFromCell(pos: Vector2D, patient: ActorRef[PatientCommand]) extends SupervisorCommand

/**
 * Cell commands
 */
sealed trait CellCommand extends Command
final case class GetRandomState(replyTo: ActorRef[PromiseCommand]) extends CellCommand
final case class GetEveryState(replyTo: ActorRef[PromiseCommand]) extends CellCommand
final case class GetCellReport(replyTo: ActorRef[PromiseCommand], timeout: FiniteDuration = 50.milliseconds) extends CellCommand
final case class GetPopulation(replyTo: ActorRef[PromiseCommand]) extends CellCommand
final case class MoveFrom(patient: ActorRef[PatientCommand]) extends CellCommand
final case class MoveTo(patient: ActorRef[PatientCommand]) extends CellCommand


/**
 * Patient commands
 */
sealed trait PatientCommand extends Command
case object DoStuff extends PatientCommand
case object Move extends PatientCommand
final case class GetState(replyTo: ActorRef[PromiseCommand]) extends PatientCommand
final case class Accommodate(cell: ActorRef[CellCommand]) extends PatientCommand


/**
 * Special ones
 */
case object Poison extends Command with PatientCommand with CellCommand with SupervisorCommand

sealed trait DebugCommands
final case class DebugPatient(cmd: PatientCommand) extends DebugCommands with CellCommand
final case class DebugCell(cmd: CellCommand, pos: Vector2D) extends DebugCommands with SupervisorCommand
