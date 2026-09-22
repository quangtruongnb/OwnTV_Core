package tv.own.owntv.core.launcher

/**
 * App-facing launcher repository. It keeps the public methods in one place while the old TV Provider
 * publisher remains the implementation detail for now.
 */
class LauncherIntegrationRepository(
    private val planner: LauncherRecommendationPlanner,
    private val resolver: LauncherLaunchResolver,
    private val tvHomeRepository: tv.own.owntv.core.tv.TvHomeRepository,
) {
    suspend fun buildSnapshot(profileId: Long): LauncherSnapshot = planner.buildSnapshot(profileId)

    suspend fun refreshProfile(profileId: Long, allowBrowsableRequest: Boolean = false) =
        tvHomeRepository.refreshProfile(profileId, allowBrowsableRequest)

    suspend fun clearProfile(profileId: Long) = tvHomeRepository.clearProfile(profileId)

    suspend fun publishMovieProgress(profileId: Long, movieId: Long, positionMs: Long, durationMs: Long) =
        tvHomeRepository.publishMovieProgress(profileId, movieId, positionMs, durationMs)

    suspend fun publishEpisodeProgress(profileId: Long, episodeId: Long, positionMs: Long, durationMs: Long) =
        tvHomeRepository.publishEpisodeProgress(profileId, episodeId, positionMs, durationMs)

    suspend fun refreshFavoriteLive(profileId: Long, allowBrowsableRequest: Boolean = false) =
        tvHomeRepository.refreshFavoriteLive(profileId, allowBrowsableRequest)

    /**
     * Compatibility entry point for hosts built before the launcher row changed from recent live
     * channels to favorite live channels.
     */
    @Deprecated("The Android TV row now shows favorite live channels; use refreshFavoriteLive.")
    suspend fun refreshRecentLive(profileId: Long, allowBrowsableRequest: Boolean = false) =
        refreshFavoriteLive(profileId, allowBrowsableRequest)

    suspend fun resolveLaunch(profileId: Long, deepLink: LauncherDeepLink): LauncherLaunch? =
        resolver.resolveLaunch(profileId, deepLink)
}
