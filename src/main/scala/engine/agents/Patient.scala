package engine.agents

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors



object Health extends Enumeration {
    type Health = Value
    val Healthy, Infected, Dead = Value
}


object Patient {
    final case class State(
        state: Health.Health = Health.Healthy
    )

    def apply(state: Health.Health = Health.Healthy): Behavior[PatientCommand] = patientAutomata(State(state))

    def patientAutomata(state: State): Behavior[PatientCommand] =
        Behaviors.receive { (context, message) => message match {
            case SayHello =>
                context.log.info("Patient{} says hello!", context.self.toString)
                Behaviors.same
            case Poison =>
                Behaviors.stopped
            case GetState(replyTo) =>
                replyTo ! PostState(state)
                Behaviors.same
        }}
}

