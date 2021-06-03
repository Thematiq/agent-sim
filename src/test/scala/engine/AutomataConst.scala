import engine.automata._
import engine.{EngineDefault}
import org.scalatest._
import flatspec._
import matchers._


class ConfigSpec extends AnyFlatSpec with should.Matchers {
    "A DiseaseConfig" should "throw IllegalArgumentException if params are out of bounds" in {
        a [java.lang.IllegalArgumentException] should be thrownBy {
            DiseaseConfig(2.0, 0.5, 1)
        }
    }

    "A PersonConfig" should "throw IllegalArgumentException if params are out of bounds" in {
        a [java.lang.IllegalArgumentException] should be thrownBy {
            PersonConfig(1.1)
        }
    }

    "A Config" should "block data changes when locked" in {
        Config.lock()
        Config.setDisease(DiseaseConfig(0.5, 0.5, 10))
        Config.setPerson(PersonConfig(0.6))

        (Config.getDisease) should be (DiseaseConfig())
        (Config.getPerson) should be (PersonConfig())
    }
}
