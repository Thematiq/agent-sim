package engine.agents

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors


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
            ref ! MoveFrom(context.self)
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
        case Inject =>
            infectedAutomata(ref, state.copy(state = Health.Infected))
        case Shoot =>
            deadAutomata(ref, state.copy(state = Health.Dead))
        case Vaccinate =>
            recoveredAutomata(ref, state.copy(state = Health.Recovered))
        case e =>
            context.log.info("Unhandled event {}", e)
            Behaviors.unhandled
    }}

    private def infectedAutomata(ref: ActorRef[CellCommand], state: State): Behavior[PatientCommand] = Behaviors.receive { (context, message) => message match {
        case Move =>
            ref ! MoveFrom(context.self)
            Behaviors.same
        case Accommodate(cell) =>
            infectedAutomata(cell, state)
        case Poison =>
            Behaviors.stopped
        case GetState(replyTo) =>
            replyTo ! PostState(state)
            Behaviors.same
        case Inject =>
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
        case Move | Shoot | Vaccinate | Inject =>
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
        case Accommodate(cell) =>
            recoveredAutomata(cell, state)
        case Poison =>
            Behaviors.stopped
        case GetState(replyTo) =>
            replyTo ! PostState(state)
            Behaviors.same
        case Shoot =>
            deadAutomata(ref, state.copy(state = Health.Dead))
        case Inject | Vaccinate =>
            Behaviors.same
        case e =>
            context.log.info("Unhandled event {}", e)
            Behaviors.unhandled
    }}
}

