import org.scalatest.{Matchers, FlatSpec}

/**
 * Requirements:
 * - if it's cold (less than 65 degrees) => heat
 * - if it's hot (greater than 75 degrees) => cool
 * - after the heater stops => run the fan for five more minutes
 * - after cooler stops => don't turn it back on for three minutes
*/

class HvacSpy(
  override val heat: Boolean,
  override val cool: Boolean,
  override val fan: Boolean,
  override val temperature: Double
) extends Hvac(heat, cool, fan, temperature)
object HvacSpy {

  def as(stateStr: String) = new HvacSpy(
    stateStr.charAt(0).isUpper,
    stateStr.charAt(1).isUpper,
    stateStr.charAt(2).isUpper,
    stateStr.substring(3).toInt)
}


class EnvironmentControllerTest extends FlatSpec with Matchers {

  "EnvironmentController" should "have tests" in {
    true should be === true
  }

  var hvac: Hvac = HvacSpy.as("hcf70")
  var envCntl: EnvironmentController = EnvironmentController(hvac)

  it should "heat when too cold" in {
    for (temperature <- 60 to 61) {
      init("hcf" + temperature)
      changeTempTickValidate(envCntl, "HcF", temperature)
    }
  }

  it should "cool when too hot" in {
    for (temperature <- 80 to 81) {
      init("hcf" + temperature)
      changeTempTickValidate(envCntl, "hCF", temperature)
    }
  }

  it should "not heat or cool when 65-75 and it wasn't heating or cooling" in {
    for (temperature <- List(65, 70, 75)) {
      init("hcf" + temperature)
      changeTempTickValidate(envCntl, "hcf", temperature)
    }
  }

  it should "not heat or cool when 65-75 deg and it was heating" in {
    for (temperature <- List(65, 70, 75)) {
      init("Hcf" + temperature)
      changeTempTickValidate(envCntl, "hcf", temperature)
    }
  }

  it should "not heat or cool when 65-75 deg and it was cooling" in {
    for (temperature <- List(65, 70, 75)) {
      init("hCf" + temperature)
      changeTempTickValidate(envCntl, "hcf", temperature)
    }
  }

  it should "not run cooler if run within 3 minutes" in {
    init("hcf76")
    changeTempTickValidate(envCntl, "hCF", 76)
    changeTempTickValidate(envCntl, "hcf", 74)
    changeTempTickValidate(envCntl, "hcf", 76)
    changeTempTickValidate(envCntl, "hCF", 76)
  }

  it should "run fan for 5 minutes after heating" in {
    init("hcf64")
    changeTempTickValidate(envCntl, "HcF", 64)
    changeTempTickValidate(envCntl, "hcF", 66)
    changeTempTickValidate(envCntl, "hcF", 66)
    changeTempTickValidate(envCntl, "hcF", 66)
    changeTempTickValidate(envCntl, "hcF", 66)
    changeTempTickValidate(envCntl, "hcf", 66)
  }

  it should "continue to run fan after heating even if cooling soon after" in {
    init("hcf64")
    changeTempTickValidate(envCntl, "HcF", 64)
    changeTempTickValidate(envCntl, "hcF", 66)
    changeTempTickValidate(envCntl, "hCF", 76)
    changeTempTickValidate(envCntl, "hcF", 74)
    changeTempTickValidate(envCntl, "hcF", 74)
    changeTempTickValidate(envCntl, "hcf", 74)
  }

  def init(stateStr: String) {
    hvac = HvacSpy.as(stateStr)
    envCntl = EnvironmentController(hvac)
  }

  def changeTempTickValidate(envCntl:EnvironmentController, stateStr: String, temperature: Int) = {
    envCntl.changeTemperature(temperature).tick() shouldEqual HvacSpy.as(stateStr + temperature)
  }

  // don't think this applies
  //  it should "continue running cooler if still too hot" in {
  //    init("hcf76")
  //    changeTempTickValidate(envCntl, "hCF", 76)
  //    changeTempTickValidate(envCntl, "hCF", 76)
  //  }

}
