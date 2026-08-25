package com.example.data

import com.example.data.dao.BookmarkDao
import com.example.data.dao.HistoryDao
import com.example.data.entity.BookmarkEntity
import com.example.data.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

class BrowserRepository(
  private val bookmarkDao: BookmarkDao,
  private val historyDao: HistoryDao
) {
  val allBookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()
  val allHistory: Flow<List<HistoryEntity>> = historyDao.getAllHistory()

  suspend fun isBookmarked(url: String): Boolean {
    return bookmarkDao.findByUrl(url) != null
  }

  suspend fun toggleBookmark(title: String, url: String, favicon: String? = null): Boolean {
    val existing = bookmarkDao.findByUrl(url)
    return if (existing != null) {
      bookmarkDao.deleteById(existing.id)
      false
    } else {
      bookmarkDao.insertBookmark(BookmarkEntity(title = title, url = url, favicon = favicon))
      true
    }
  }

  suspend fun deleteBookmark(id: Long) {
    bookmarkDao.deleteById(id)
  }

  suspend fun addHistory(title: String, url: String, favicon: String? = null) {
    if (url.isNotBlank() && !url.startsWith("about:") && !url.startsWith("data:")) {
      historyDao.insertHistory(
        HistoryEntity(
          title = if (title.isBlank()) url else title,
          url = url,
          favicon = favicon,
          visitTime = System.currentTimeMillis()
        )
      )
    }
  }

  suspend fun deleteHistory(id: Long) {
    historyDao.deleteById(id)
  }

  suspend fun clearHistory() {
    historyDao.clearAllHistory()
  }
}
