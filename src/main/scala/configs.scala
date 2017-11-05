//******************************************************************************
// Copyright (c) 2015, The Regents of the University of California (Regents).
// All Rights Reserved. See LICENSE for license details.
//------------------------------------------------------------------------------

package boom
import Chisel._
import freechips.rocketchip.config.{Parameters, Config}
import freechips.rocketchip.coreplex.{SystemBusKey}
import freechips.rocketchip.devices.tilelink.{BootROMParams}
import freechips.rocketchip.rocket._
import freechips.rocketchip.tile._


// Try to be a reasonable BOOM design point.
class DefaultBoomConfig extends Config((site, here, up) => {

   // Top-Level
   case XLen => 64

   // Use this boot ROM for SimDTM.
   case BootROMParams => BootROMParams(contentFileName = "./rocket-chip/bootrom/bootrom.img")

   // Rocket/Core Parameters
   case boom.system.BoomTilesKey => up(boom.system.BoomTilesKey, site) map { r => r.copy(
      core = r.core.copy(
         fetchWidth = 1,
         decodeWidth = 1,
         numRobEntries = 24,
         issueParams = Seq(
            IssueParams(issueWidth=1, numEntries=20, iqType=IQT_MEM.litValue),
            IssueParams(issueWidth=2, numEntries=20, iqType=IQT_INT.litValue),
            IssueParams(issueWidth=1, numEntries=20, iqType=IQT_FP.litValue)),
         numIntPhysRegisters = 100,
         numFpPhysRegisters = 64,
         numLsuEntries = 16,
         maxBrCount = 8,
         btb = BTBsaParameters(nSets=64, nWays=4, nRAS=8, tagSz=20),
         enableBranchPredictor = true,
         gshare = Some(GShareParameters(enabled=true, history_length=15)),
         nPerfCounters = 29,
         fpu = Some(freechips.rocketchip.tile.FPUParams(sfmaLatency=4, dfmaLatency=4, divSqrt=true))),
      btb = Some(BTBParams(nEntries = 0, updatesOutOfOrder = true)),
      dcache = Some(DCacheParams(rowBits = site(SystemBusKey).beatBits, nSets=64, nWays=8, nMSHRs=2, nTLBEntries=8)),
      icache = Some(ICacheParams(fetchBytes = 1*4, rowBits = site(SystemBusKey).beatBits, nSets=64, nWays=8))
      )}
})


//class WithNPerfCounters(n: Int) extends Config((site, here, up) => {
//   case RocketTilesKey => up(RocketTilesKey, site) map { r => r.copy(core = r.core.copy(
//      nPerfCounters = n
//   ))}
//})
//
//// Small BOOM! Try to be fast to compile, easier to debug.
//class WithSmallBooms extends Config((site, here, up) => {
//   case RocketTilesKey => up(RocketTilesKey, site) map { r =>r.copy(
//      core = r.core.copy(
////         fWidth = 1, XXX
//         nPerfCounters = 2),
//      icache = Some(r.icache.get.copy(
//         fetchBytes=1*4))
//      )}
//   case BoomKey => up(BoomKey, site).copy(
//      numRobEntries = 24,
//      issueParams = Seq(
//         IssueParams(issueWidth=1, numEntries=4, iqType=IQT_MEM.litValue),
//         IssueParams(issueWidth=1, numEntries=4, iqType=IQT_INT.litValue),
//         IssueParams(issueWidth=1, numEntries=4, iqType=IQT_FP.litValue)),
//      numIntPhysRegisters = 56,
//      numFpPhysRegisters = 48,
//      numLsuEntries = 4,
//      maxBrCount = 4,
//      gshare = Some(GShareParameters(enabled = true, history_length=12))
//      )
//})
//
//
//// try to match the Cortex-A9
//class WithMediumBooms extends Config((site, here, up) => {
//   case RocketTilesKey => up(RocketTilesKey, site) map { r =>r.copy(
//      core = r.core.copy(
////         fWidth = 2, XXX
//         //nPerfCounters = 6,
//         fpu = Some(freechips.rocketchip.tile.FPUParams(sfmaLatency=4, dfmaLatency=4, divSqrt=true))),
//      dcache = Some(DCacheParams(rowBits = site(SystemBusKey).beatBits, nSets=64, nWays=4, nMSHRs=2, nTLBEntries=8)),
//      icache = Some(ICacheParams(rowBits = site(SystemBusKey).beatBits, nSets=64, nWays=4, fetchBytes=2*4))
//      )}
//   case BoomKey => up(BoomKey, site).copy(
//      numRobEntries = 48,
//      issueParams = Seq(
//         IssueParams(issueWidth=1, numEntries=20, iqType=IQT_MEM.litValue),
//         IssueParams(issueWidth=2, numEntries=16, iqType=IQT_INT.litValue),
//         IssueParams(issueWidth=1, numEntries=10, iqType=IQT_FP.litValue)),
//      numIntPhysRegisters = 70,
//      numFpPhysRegisters = 64,
//      numLsuEntries = 16,
//      maxBrCount = 8,
//      regreadLatency = 1,
//      renameLatency = 2,
//      enableBpdF2Redirect = false,
//      btb = BTBsaParameters(nSets=64, nWays=2, nRAS=8, tagSz=20, bypassCalls=false, rasCheckForEmpty=false),
//      gshare = Some(GShareParameters(enabled=true, history_length=13))
//      )
//})
//
//
//// try to match the Cortex-A15
//class WithMegaBooms extends Config((site, here, up) => {
//
//   // Set TL network to 128bits wide
//   case SystemBusKey => up(SystemBusKey, site).copy(beatBytes = 16)
//
//   case RocketTilesKey => up(RocketTilesKey, site) map { r =>r.copy(
////      core = r.core.copy(
////         fWidth = 4), XXX
//      icache = Some(r.icache.get.copy(
//         fetchBytes=4*4))
//      )}
//
//   case BoomKey => up(BoomKey, site).copy(
//      numRobEntries = 128,
//      issueParams = Seq(
//         IssueParams(issueWidth=1, numEntries=20, iqType=IQT_MEM.litValue),
//         IssueParams(issueWidth=2, numEntries=20, iqType=IQT_INT.litValue),
//         IssueParams(issueWidth=1, numEntries=20, iqType=IQT_FP.litValue)), // TODO make this 2-wide issue
//      numIntPhysRegisters = 128,
//      numFpPhysRegisters = 80,
//      numLsuEntries = 32,
//      gshare = Some(GShareParameters(enabled=true, history_length=15))
//      // tage is unsupported in boomv2 for now.
//      //tage = Some(TageParameters())
//      )
//})
