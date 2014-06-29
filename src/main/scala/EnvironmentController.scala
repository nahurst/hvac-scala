case class Hvac(
  heat: Boolean,
  cool: Boolean,
  fan: Boolean,
  temperature: Double
)

case class Countdown(defaultReset: Int, var counter: Int = 0) {
  def tick() = if (counter > 0) counter -= 1
  def isDone(): Boolean = counter == 0
  def reset() = counter = defaultReset
}

case class EnvironmentController(
  hvac: Hvac,
  coolerCountdown: Countdown = Countdown(3),
  heaterCountdown: Countdown = Countdown(5)
) {

  def tick() : Hvac = {
    coolerCountdown.tick()
    heaterCountdown.tick()

    hvac match {
      case Hvac(_, _, _, temperature) if temperature < 65 => heatUp
      case Hvac(_, _, _, temperature) if temperature > 75 && coolerCountdown.isDone() => coolDown
      case _ if !heaterCountdown.isDone() => runFan
      case _ => turnEverythingOff
    }
  }

  def changeTemperature(newTemperature: Double) : EnvironmentController = {
    copy(hvac.copy(temperature = newTemperature))
  }

  private def heatUp() : Hvac = {
    heaterCountdown.reset()
    Hvac(true, false, true, hvac.temperature)
  }

  private def coolDown() : Hvac = {
    coolerCountdown.reset()
    Hvac(false, true, true, hvac.temperature)
  }

  private def runFan() : Hvac = Hvac(false, false, true, hvac.temperature)
  private def turnEverythingOff() : Hvac = Hvac(false, false, false, hvac.temperature)

}
