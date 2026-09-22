package tv.own.owntv.core.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import tv.own.owntv.core.database.entity.ChannelEntity
import tv.own.owntv.core.database.entity.ContentOrderEntity
import tv.own.owntv.core.database.entity.FavoriteEntity
import tv.own.owntv.core.database.entity.ProfileEntity
import tv.own.owntv.core.database.entity.SourceEntity
import tv.own.owntv.core.database.entity.WatchHistoryEntity
import tv.own.owntv.core.model.MediaType
import tv.own.owntv.core.model.SourceType

@RunWith(AndroidJUnit4::class)
class ChannelDaoLauncherFavoritesTest {
    private lateinit var db: OwnTVDatabase

    @Before
    fun setUp() {
        db = ownTVTestDatabase()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun launcherFavoritesAreProfileScopedManuallyOrderedAndBounded() = runBlocking {
        val profileId = 1L
        db.profileDao().insert(ProfileEntity(id = profileId, name = "Profile", avatarColor = 0))
        db.sourceDao().insert(SourceEntity(id = 10, name = "Active", type = SourceType.M3U, url = "https://example.invalid/active"))
        db.sourceDao().insert(SourceEntity(id = 20, name = "Inactive", type = SourceType.M3U, url = "https://example.invalid/inactive"))
        db.channelDao().upsertAll(
            listOf(
                channel(id = 1, sourceId = 10, name = "Second"),
                channel(id = 2, sourceId = 10, name = "First"),
                channel(id = 3, sourceId = 10, name = "Unplaced"),
                channel(id = 4, sourceId = 10, name = "History only"),
                channel(id = 5, sourceId = 20, name = "Inactive source"),
            ),
        )
        db.favoriteDao().add(FavoriteEntity(profileId = profileId, mediaType = MediaType.LIVE, itemId = 1, addedAt = 100))
        db.favoriteDao().add(FavoriteEntity(profileId = profileId, mediaType = MediaType.LIVE, itemId = 2, addedAt = 200))
        db.favoriteDao().add(FavoriteEntity(profileId = profileId, mediaType = MediaType.LIVE, itemId = 3, addedAt = 300))
        db.favoriteDao().add(FavoriteEntity(profileId = profileId, mediaType = MediaType.LIVE, itemId = 5, addedAt = 400))
        db.historyDao().insertIfAbsent(WatchHistoryEntity(profileId = profileId, mediaType = MediaType.LIVE, itemId = 4, watchedAt = 500))
        db.contentOrderDao().insertAll(
            listOf(
                ContentOrderEntity(profileId = profileId, mediaType = MediaType.LIVE, contextKey = ContentOrderEntity.FAV_CONTEXT, itemId = 2, position = 0),
                ContentOrderEntity(profileId = profileId, mediaType = MediaType.LIVE, contextKey = ContentOrderEntity.FAV_CONTEXT, itemId = 1, position = 1),
            ),
        )

        val channels = db.channelDao().launcherFavorites(profileId, ContentOrderEntity.FAV_CONTEXT, listOf(10), limit = 2)

        assertEquals(listOf(2L, 1L), channels.map { it.id })
    }

    private fun channel(id: Long, sourceId: Long, name: String) = ChannelEntity(
        id = id,
        sourceId = sourceId,
        name = name,
        streamUrl = "https://example.invalid/$id",
    )
}
