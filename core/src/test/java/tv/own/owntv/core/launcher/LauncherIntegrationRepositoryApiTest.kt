package tv.own.owntv.core.launcher

import org.junit.Assert.assertNotNull
import org.junit.Test

class LauncherIntegrationRepositoryApiTest {

    @Test
    fun `keeps both recent and favorite live refresh entry points`() {
        assertNotNull(LauncherIntegrationRepository::refreshRecentLive)
        assertNotNull(LauncherIntegrationRepository::refreshFavoriteLive)
    }
}
