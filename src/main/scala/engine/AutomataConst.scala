package engine.automata

import engine.{EngineDefault}
import java.util.concurrent.atomic.AtomicBoolean


object PersonStates extends Enumeration {
    type States     = Value
    val Healty      = Value
    val Infected    = Value
    val Dead        = Value
    val Recovered   = Value
}

final case class DiseaseConfig (
    infectionRate: Double,
    mortalityRate: Double,
    period: Int
)

object DiseaseConfig {
    def apply() = 
        new DiseaseConfig(EngineDefault.infectionRate, EngineDefault.mortalityRate, EngineDefault.period)
    def apply(infectionRate: Double, mortalityRate: Double, period: Int) = {
        if (period <= 0)
            throw new java.lang.IllegalArgumentException("Period cannot be negative integer")
        if (mortalityRate > 1 || mortalityRate < 0)
            throw new java.lang.IllegalArgumentException("Mortality rate must be in range [0,1]")
        if (infectionRate > 1 || infectionRate < 0)
            throw new java.lang.IllegalArgumentException("Infenction rate must be in range [0,1]")
        
        new DiseaseConfig(infectionRate, mortalityRate, period)
    }
}

final case class PersonConfig (
    activityRatio: Double
)

object PersonConfig {
    def apply() = new PersonConfig(EngineDefault.activityRatio)
    def apply(activityRatio: Double) = {
        if (activityRatio > 1 || activityRatio < 0)
            throw new java.lang.IllegalArgumentException("Activity rate must be in range [0,1]")
        
        new PersonConfig(activityRatio)
    }
}

object Config {
    private var disease = DiseaseConfig()
    private var person  = PersonConfig()
    private var locked = new AtomicBoolean(false)
    def setDisease(t: DiseaseConfig) = if (!locked.get) disease = t
    def getDisease = disease
    def getPerson = person
    def setPerson(t: PersonConfig) = if (!locked.get) person = t
    def lock() = locked.set(true)
}