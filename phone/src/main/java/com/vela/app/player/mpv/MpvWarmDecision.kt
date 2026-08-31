package com.vela.app.player.mpv

internal enum class MpvWarmDecision {
    SkipNotMpv,
    SkipLivePlayback,
    SkipAlreadyWarm,
    StartWarmup
}

internal fun decideMpvWarmup(
    engineIsMpv: Boolean,
    hasLiveController: Boolean,
    alreadyWarmOrWarming: Boolean
): MpvWarmDecision {
    return when {
        !engineIsMpv -> MpvWarmDecision.SkipNotMpv
        hasLiveController -> MpvWarmDecision.SkipLivePlayback
        alreadyWarmOrWarming -> MpvWarmDecision.SkipAlreadyWarm
        else -> MpvWarmDecision.StartWarmup
    }
}
