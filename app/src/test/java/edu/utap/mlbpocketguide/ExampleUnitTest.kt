package edu.utap.mlbpocketguide

import edu.utap.mlbpocketguide.api.PlayerRepository
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    // We rely on the assumption that a player only shows up once in the repository
    @Test
    fun unique_repository() {
        val players = PlayerRepository().fetchData()
        val listOfPlayers = mutableListOf<String>()
        players.forEach {
            val fullName = it.firstName + " " + it.lastName
            listOfPlayers.add(fullName)
        }
        val playersUnique = players.distinct().size
        val namesUniques = listOfPlayers.distinct().size
        assertEquals(playersUnique, namesUniques)
    }
}