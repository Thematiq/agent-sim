package engine.agents

import engine.Vector2D
import akka.actor.typed.{ ActorRef }


sealed trait Command

/**
 * Commands for getting model information outside the Actor ecosystem
 */
sealed trait PromiseCommand
final case class PostState(state: Patient.State) extends PromiseCommand


/**
 * Supervisor commands
 */
sealed trait SupervisorCommand extends Command
final case class GetRandomStateAt(pos: Vector2D, replyTo: ActorRef[PromiseCommand]) extends SupervisorCommand


/**
 * Cell commands
 */
sealed trait CellCommand extends Command
final case class GetRandomState(replyTo: ActorRef[PromiseCommand]) extends CellCommand


/**
 * Patient commands
 */
sealed trait PatientCommand extends Command
case object SayHello extends PatientCommand
final case class GetState(replyTo: ActorRef[PromiseCommand]) extends PatientCommand


/**
 * Special one
 */
case object Poison extends Command with PatientCommand with CellCommand with SupervisorCommand