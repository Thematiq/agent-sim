package engine.agents


object Health extends Enumeration {
    type Health = Value
    val Healthy, Infected, Recovered, Dead = Value
}