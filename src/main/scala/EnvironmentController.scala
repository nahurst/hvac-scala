case class Hvac(
  heat: Boolean,
  cool: Boolean,
  fan: Boolean,
  temperature: Double
)

class Countdown(defaultReset: Int) {
  var counter = 0
  def tick() = if (counter > 0) counter -= 1
  def isDone(): Boolean = counter == 0
  def reset() = counter = defaultReset
}

case class EnvironmentController(var hvac: Hvac) {

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
    hvac = hvac.copy(temperature = newTemperature)
    this
  }

  // ======== Privates ===========
  // is there a better way of grouping these?

  private val coolerCountdown = new Countdown(3)
  private val heaterCountdown = new Countdown(5)

  private def heatUp() : Hvac = {
    hvac = Hvac(true, false, true, hvac.temperature)
    heaterCountdown.reset()

    hvac
  }

  private def coolDown() : Hvac = {
    hvac = Hvac(false, true, true, hvac.temperature)
    coolerCountdown.reset()

    hvac
  }

  private def turnEverythingOff() : Hvac = {
    hvac = Hvac(false, false, false, hvac.temperature)
    hvac
  }

  private def runFan() : Hvac = {
    hvac = Hvac(false, false, true, hvac.temperature)
    hvac
  }


}
