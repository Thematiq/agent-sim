package engine.automata

import engine.{Vector2D}

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.LoggerOps
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.{Actor, ActorLogging}



sealed trait Input
sealed trait DebugInput extends Input
sealed trait Output
sealed trait DebugOutput extends Output 

class Cell extends Actor with ActorLogging{
    def receive = {
        case x =>
            log.warning("[receive] Uknown request {}", x)
    }
}

object Cell {

}


class Person extends Actor with ActorLogging {
    final case class AutomataState(pos: Option[ActorRef[Cell]], hp: PersonStates.States)

    final case class UpdatePos(pos: ActorRef[Cell]) extends Input
    final case class UpdateHp(hp: PersonStates.States) extends Input
    case object RequestState extends DebugInput
    private case object Tick extends Input
    
    final case class ResponseState(state: AutomataState) extends DebugOutput


    def receive: Receive = inVoid(AutomataState(None, PersonStates.Healty))

    def inVoid(s: AutomataState): Receive = {
        case UpdatePos(pos) => 
            context become working(AutomataState(Some(pos), s.hp))
        case UpdateHp(hp) =>
            context become inVoid(AutomataState(None, hp))
        case x =>
            log.warning("[inVoid] Uknown request {}", x)
    }

    def working(s: AutomataState): Receive = {
        case x =>
            log.warning("[working] Uknown request {}", x)
    }
}
