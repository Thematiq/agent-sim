package engine.agents

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import engine.Vector2D


object Cell {
    private def getPatientName(pos: Vector2D, id: Int): String = pos.toString + "_" + id.toString

    def apply(initialPop: Int, pos: Vector2D): Behavior[Command] =
        Behaviors.setup { context =>
            val pop: Vector[ActorRef[PatientCommand]] =
                Vector.tabulate(initialPop) (x => context.spawn(Patient(), getPatientName(pos, x)))

            Behaviors.receive { (context, message) => message match {
                case Poison =>
                    pop.foreach(x => x ! Poison)
                    Behaviors.stopped
            }}
        }
}