package apiTest

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


/**
 * Тесты для EmojiHub API.
 * Для запуска нужно клонировать репозиторий с сервером
 * git clone https://github.com/cheatsnake/emojihub.git
 * Перейти в него
 * cd ./emojihub
 * Докачать необходимык пакеты (перед этим установить Go - brew install go)
 * go mod download
 * Перед запуском тестов запустить сервер
 * go run cmd/main.go
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmojiHubApiTest {

    @BeforeAll
    fun setup() {
        RestAssured.baseURI = "http://localhost:4000/api"
    }

    @Test
    fun testGetRandomEmoji() {
        val emoji = Given {
            contentType(ContentType.JSON)
        } When {
            get("/random")
        } Then {
            statusCode(200)
        } Extract {
            response().`as`(Emoji::class.java)
        }

        assert(emoji.name.isNotEmpty())
        assert(emoji.category.isNotEmpty())
        assert(emoji.group.isNotEmpty())
        assert(emoji.htmlCode.isNotEmpty())
        assert(emoji.unicode.isNotEmpty())

    }

    @Test
    fun testGetAllEmojis() {
        val emojis = Given {
            contentType(ContentType.JSON)
        } When {
            get("/all")
        } Then {
            statusCode(200)
        } Extract {
            response().`as`(Array<Emoji>::class.java)
        }

        assert(emojis.isNotEmpty())

    }

    @ParameterizedTest
    @ValueSource(strings = ["smileys-and-people", "animals-and-nature", "food-and-drink", "travel-and-places", "activities", "objects", "symbols", "flags"])
    fun testGetEmojisByCategory(category: String) {
        val emojis = Given {
            contentType(ContentType.JSON)
        } When {
            get("all/category/$category")
        } Then {
            statusCode(200)
        } Extract {
            response().`as`(Array<Emoji>::class.java)
        }
        assert(emojis.isNotEmpty())
        assert(emojis.all { it.category.replace(" ", "-").lowercase() == category })

    }

    @ParameterizedTest
    @ValueSource(strings = ["travel-and-places", "activities", "animal-bug"])
    fun testGetEmojisByGroup(group: String) {
        val emojis = Given {
            contentType(ContentType.JSON)
        } When {
            get("all/group/$group")
        } Then {
            statusCode(200)
        } Extract {
            response().`as`(Array<Emoji>::class.java)
        }
        assert(emojis.isNotEmpty())
        assert(emojis.all { it.group.replace(" ", "-").lowercase() == group })

    }

    @Test
    fun testInvalidCategory() {
        Given {
            contentType(ContentType.JSON)
        } When {
            get("all/category/invalid-category")
        } Then {
            statusCode(404)
        }
    }

    @Test
    fun testInvalidGroup() {
        Given {
            contentType(ContentType.JSON)

        } When {
            get("all/group/invalid-group")
        } Then {
            statusCode(404)
        }
    }
}