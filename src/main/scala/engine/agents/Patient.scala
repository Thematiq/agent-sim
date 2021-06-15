package engine.agents

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import engine.Tools


object Patient {
    final case class State(
        state: Health.Health = Health.Healthy,
        config: Config
    )

    def apply(ref: ActorRef[CellCommand], state: Health.Health = Health.Healthy, config: Config): Behavior[PatientCommand]
        = healthyAutomata(ref, State(state, config))

    private def healthyAutomata(ref: ActorRef[CellCommand], state: State): Behavior[PatientCommand] = Behaviors.receive { (context, message) => message match {
        case Tick =>
            // More stuff later on
            if (Tools.decide(state.config.mobility)) context.self ! Move
            Behaviors.same
        case Move =>
            ref ! MoveFrom(context.self)
            Behaviors.same
        case Accommodate(cell) =>
            healthyAutomata(cell, state)
        case Poison =>
            Behaviors.stopped
        case GetState(replyTo) =>
            replyTo ! PostState(state)
            Behaviors.same
        case Inject | Sneeze =>
            // Roll the dice
            val days: Int = Math.round(Tools.sampleGauss(state.config.durationMean, state.config.durationStd)).toInt
            infectedAutomata(ref, state.copy(state = Health.Infected), days)
        case Shoot =>
            deadAutomata(ref, state.copy(state = Health.Dead))
        case Vaccinate =>
            recoveredAutomata(ref, state.copy(state = Health.Recovered))
        case e =>
            context.log.info("Unhandled event {}", e)
            Behaviors.unhandled
    }}

    private def infectedAutomata(ref: ActorRef[CellCommand], state: State, daysLeft: Int): Behavior[PatientCommand] = Behaviors.receive { (context, message) => message match {
        case Move =>
            ref ! MoveFrom(context.self)
            Behaviors.same
        case Tick =>
            if (Tools.decide(state.config.sneezebility)) ref ! Sneeze
            if (Tools.decide(state.config.mobility - state.config.severity)) context.self ! Move
            if (daysLeft == 0) {
                // Roll the dice
                if (Tools.decide(state.config.mortalityRate)) {
                    deadAutomata(ref, state.copy(state = Health.Dead))
                } else {
                    recoveredAutomata(ref, state.copy(state = Health.Recovered))
                }
            } else {
                infectedAutomata(ref, state, daysLeft - 1)
            }
        case Accommodate(cell) =>
            infectedAutomata(cell, state, daysLeft)
        case Poison =>
            Behaviors.stopped
        case GetState(replyTo) =>
            replyTo ! PostState(state)
            Behaviors.same
        case Inject | Sneeze =>
            Behaviors.same
        case Shoot =>
            deadAutomata(ref, state.copy(state = Health.Dead))
        case Vaccinate =>
            recoveredAutomata(ref, state.copy(state = Health.Recovered))
        case e =>
            context.log.info("Unhandled event {}", e)
            Behaviors.unhandled
    }}

    private def deadAutomata(ref: ActorRef[CellCommand], state: State): Behavior[PatientCommand] = Behaviors.receive { (context, message) => message match {
        case Move | Shoot | Vaccinate | Inject | Sneeze | Tick =>
            Behaviors.same
        case Accommodate(cell) =>
            deadAutomata(cell, state)
        case Poison =>
            Behaviors.stopped
        case GetState(replyTo) =>
            replyTo ! PostState(state)
            Behaviors.same
        case e =>
            context.log.info("Unhandled event {}", e)
            Behaviors.unhandled
    }}

    private def recoveredAutomata(ref: ActorRef[CellCommand], state: State): Behavior[PatientCommand] = Behaviors.receive { (context, message) => message match {
        case Move =>
            ref ! MoveFrom(context.self)
            Behaviors.same
        case Tick =>
            if (Tools.decide(state.config.mobility)) context.self ! Move
            Behaviors.same
        case Accommodate(cell) =>
            recoveredAutomata(cell, state)
        case Poison =>
            Behaviors.stopped
        case GetState(replyTo) =>
            replyTo ! PostState(state)
            Behaviors.same
        case Shoot =>
            deadAutomata(ref, state.copy(state = Health.Dead))
        case Inject | Vaccinate | Sneeze =>
            Behaviors.same
        case e =>
            context.log.info("Unhandled event {}", e)
            Behaviors.unhandled
    }}
}

