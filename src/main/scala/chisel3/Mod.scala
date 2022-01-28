package chisel3

import chisel3.internal.Builder
import chisel3.internal.sourceinfo.UnlocatableSourceInfo
// Module without clk
abstract class Mod(implicit moduleCompileOptions: CompileOptions) extends RawModule {
  // Implicit reset pins
  final val reset: Reset = IO(Input(mkReset)).suggestName("reset")

  // TODO It's hard to remove these deprecated override methods because they're used by
  //   Chisel.QueueCompatibility which extends chisel3.Queue which extends chisel3.Module
  private var _override_reset: Option[Bool] = None
  @deprecated("Use withClock at Module instantiation", "Chisel 3.5")
  protected def override_reset: Option[Bool] = _override_reset
  @deprecated("Use withClock at Module instantiation", "Chisel 3.5")
  protected def override_reset_=(rhs: Option[Bool]): Unit = {
    _override_reset = rhs
  }

  private[chisel3] def mkReset: Reset = {
    // Top module and compatibility mode use Bool for reset
    // Note that a Definition elaboration will lack a parent, but still not be a Top module
    val inferReset = (_parent.isDefined || Builder.inDefinition) && moduleCompileOptions.inferModuleReset
    if (inferReset) Reset() else Bool()
  }

  // Setup ClockAndReset
  Builder.currentReset = Some(reset)
  Builder.clearPrefix()

  private[chisel3] override def initializeInParent(parentCompileOptions: CompileOptions): Unit = {
    implicit val sourceInfo = UnlocatableSourceInfo

    super.initializeInParent(parentCompileOptions)
    reset := _override_reset.getOrElse(Builder.forcedReset)
  }
}
