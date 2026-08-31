package com.vela.app.ui.screens.detail

import com.vela.data.model.BaseItemDto
import com.vela.data.model.SeerrRecommendationTitle

/**
 * 进程内详情缓存。PlayerActivity 在前台时主 Activity 可能被回收；
 * 返回后用这里的快照立刻还原，避免整页闪成 loading。
 */
internal object DetailPageCache {
    private const val MAX_PERSON_ENTRIES = 8
    private const val MAX_ITEM_ENTRIES = 16

    data class PersonSnapshot(
        val person: BaseItemDto?,
        val relatedTitles: List<BaseItemDto>,
        val seerrRelatedTitles: List<SeerrRecommendationTitle>
    ) {
        val hasContent: Boolean
            get() = person != null ||
                relatedTitles.isNotEmpty() ||
                seerrRelatedTitles.isNotEmpty()
    }

    private val persons = LinkedHashMap<String, PersonSnapshot>(MAX_PERSON_ENTRIES, 0.75f, true)
    private val items = LinkedHashMap<String, BaseItemDto>(MAX_ITEM_ENTRIES, 0.75f, true)

    fun person(personId: String, serverId: String?): PersonSnapshot? {
        synchronized(persons) {
            return persons[key(personId, serverId)]
        }
    }

    fun putPerson(
        personId: String,
        serverId: String?,
        snapshot: PersonSnapshot
    ) {
        synchronized(persons) {
            persons[key(personId, serverId)] = snapshot
            evict(persons, MAX_PERSON_ENTRIES)
        }
    }

    fun item(itemId: String, serverId: String?): BaseItemDto? {
        synchronized(items) {
            return items[key(itemId, serverId)]
        }
    }

    fun putItem(itemId: String, serverId: String?, item: BaseItemDto) {
        synchronized(items) {
            items[key(itemId, serverId)] = item
            evict(items, MAX_ITEM_ENTRIES)
        }
    }

    fun clear() {
        synchronized(persons) { persons.clear() }
        synchronized(items) { items.clear() }
    }

    private fun key(id: String, serverId: String?): String = "${serverId.orEmpty()}|$id"

    private fun <V> evict(map: LinkedHashMap<String, V>, maxEntries: Int) {
        while (map.size > maxEntries) {
            val eldest = map.keys.firstOrNull() ?: return
            map.remove(eldest)
        }
    }
}
