package com.sllauncher.app.commands

import javax.inject.Inject
import javax.inject.Singleton

/** Routes feature commands (music, news, notes, profiles, torch, replies) to their feature areas. */
@Singleton
class FeatureCommandHandlersImpl @Inject constructor() : FeatureCommandHandlers {
    override suspend fun handle(command: LauncherCommand): ActionResult =
        ActionResult.Failure("${command.description} isn't available yet.")
}
