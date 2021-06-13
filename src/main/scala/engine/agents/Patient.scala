package engine.agents

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors


object Patient {
    object Health extends Enumeration {
        type Health = Value
        val Healthy, Infected, Dead = Value
    }

    final case class State(
        state: Health.Health = Health.Healthy
    )

    def apply(ref: ActorRef[CellCommand],state: Health.Health = Health.Healthy): Behavior[PatientCommand]
        = patientAutomata(ref, State(state))

    def patientAutomata(ref: ActorRef[CellCommand], state: State): Behavior[PatientCommand] =
        Behaviors.receive { (context, message) => message match {
            case Move =>
                ref ! MoveFrom(context.self)
                Behaviors.same
            case Accommodate(cell) =>
                patientAutomata(cell, state)
            case Poison =>
                Behaviors.stopped
            case GetState(replyTo) =>
                replyTo ! PostState(state)
                Behaviors.same
        }}
}

