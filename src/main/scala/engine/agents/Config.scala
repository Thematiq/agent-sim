package engine.agents


final case class Config(
                           infectionRate: Double,
                           mortalityRate: Double,
                           durationMean: Double,
                           durationStd: Double,
                           severity: Double,
                           mobility: Double
)

object Config {
    private def check(value: Double, name: String, low: Double = 0, high: Double = 1) = {
        if (value >= high || value < low) {
            throw new RuntimeException(name + " should be in range [" + low + ", " + high + ")")
        }
    }

    def apply(ir: Double, mr: Double, dm: Double, ds: Double, s: Double, m: Double): Config = {
        check(ir, "Infection rate")
        check(mr, "Mortality rate")
        check(dm, "Mean duration", 0, 20)
        check(ds, "Std duration", 0, 20)
        check(s, "Severity")
        check(m, "Mobility")
        new Config(ir, mr, dm, ds, s, m)
    }

    def apply(): Config = Config(0.1, 0.1, 5, 2, 0.1, 0.4)
}