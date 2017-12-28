package online.javanese.model

import com.github.andrewoma.kwery.core.Session
import com.github.andrewoma.kwery.mapper.*
import online.javanese.Html
import online.javanese.krud.kwery.Uuid
import java.time.LocalDateTime

class Article(
        val basicInfo: BasicInfo,
        val meta: Meta,
        val heading: String,
        val bodyMarkup: Html,
        val sortIndex: Int,
        val published: Boolean,
        val vkPostInfo: VkPostInfo?,
        val createdAt: LocalDateTime,
        val lastModified: LocalDateTime
) {

    class BasicInfo(
            val id: Uuid,
            val linkText: String,
            val urlPathComponent: String,
            val lastModified: LocalDateTime
    )

    class VkPostInfo(
            val id: String,
            val hash: String
    )

    internal val vkPostIdOrNull get() = vkPostInfo?.id ?: ""
    internal val vkPostHashOrNull get() = vkPostInfo?.hash ?: ""

}

object ArticleTable : Table<Article, Uuid>("articles") {

    val Id by idCol(Article.BasicInfo::id, Article::basicInfo)
    val LinkText by linkTextCol(Article.BasicInfo::linkText, Article::basicInfo)
    val UrlPathComponent by urlPathComponentCol(Article.BasicInfo::urlPathComponent, Article::basicInfo)

    val MetaTitle by metaTitleCol(Article::meta)
    val MetaDescription by metaDescriptionCol(Article::meta)
    val MetaKeywords by metaKeywordsCol(Article::meta)

    val Heading by col(Article::heading, name = "heading")
    val BodyMarkup by col(Article::bodyMarkup, name = "bodyMarkup")
    val SortIndex by sortIndexCol(Article::sortIndex)
    val Published by col(Article::published, name = "published")

    val VkPostId by col(Article::vkPostIdOrNull, name = "vkPostId")
    val VkPostHash by col(Article::vkPostHashOrNull, name = "vkPostHash")

    val CreatedAt by col(Article::createdAt, name = "createdAt")
    val LastModified by lastModifiedCol(Article::lastModified)


    override fun idColumns(id: Uuid): Set<Pair<Column<Article, *>, *>> =
            setOf(Id of id)

    override fun create(value: Value<Article>): Article = Article(
            basicInfo = Article.BasicInfo(
                    id = value of Id,
                    linkText = value of LinkText,
                    urlPathComponent = value of UrlPathComponent,
                    lastModified = value of LastModified
            ),
            meta = Meta(
                    title = value of MetaTitle,
                    description = value of MetaDescription,
                    keywords = value of MetaKeywords
            ),
            heading = value of Heading,
            bodyMarkup = value of BodyMarkup,
            sortIndex = value of SortIndex,
            published = value of Published,
            vkPostInfo = vkPostInfoOrNull(
                    vkPostId = value of VkPostId,
                    vkPostHash = value of VkPostHash
            ),
            createdAt = value of CreatedAt,
            lastModified = value of LastModified
    )

    private fun vkPostInfoOrNull(vkPostId: String, vkPostHash: String): Article.VkPostInfo? {
        if (vkPostId.isBlank()) return null
        if (vkPostHash.isBlank()) return null
        return Article.VkPostInfo(
                id = vkPostId,
                hash = vkPostHash
        )
    }


}

private object ArticleBasicInfoTable : Table<Article.BasicInfo, Uuid>("articles") {

    val Id by idCol(Article.BasicInfo::id)
    val LinkText by linkTextCol(Article.BasicInfo::linkText)
    val UrlPathComponent by urlPathComponentCol(Article.BasicInfo::urlPathComponent)
    val LastModified by lastModifiedCol(Article.BasicInfo::lastModified)

    override fun idColumns(id: Uuid): Set<Pair<Column<Article.BasicInfo, *>, *>> =
            setOf(Id of id)

    override fun create(value: Value<Article.BasicInfo>): Article.BasicInfo = Article.BasicInfo(
            id = value of Id,
            linkText = value of LinkText,
            urlPathComponent = value of UrlPathComponent,
            lastModified = value of LastModified
    )

}

class ArticleDao(
        session: Session
) : AbstractDao<Article, Uuid>(session, ArticleTable, ArticleTable.Id.property) {

    private val tableName = ArticleTable.name
    private val basicCols = """"id", "linkText", "urlPathComponent", "lastModified""""
    private val urlComponentColName = ArticleTable.UrlPathComponent.name
    private val publishedColName = ArticleTable.Published.name
    private val sortIndexColName = ArticleTable.SortIndex.name

    override val defaultOrder: Map<Column<Article, *>, OrderByDirection> =
            mapOf(ArticleTable.SortIndex to OrderByDirection.ASC) // todo: pinned articles

    fun findAllBasicPublishedOrderBySortIndex(): List<Article.BasicInfo> =
            session.select(
                    sql = """SELECT $basicCols
                        |FROM "$tableName"
                        |WHERE "$publishedColName" = true
                        |ORDER BY "$sortIndexColName" ASC""".trimMargin(),

                    mapper = ArticleBasicInfoTable.rowMapper()
            )

    fun findAllPublishedOrderBySortIndex(): List<Article> =
            session.select(
                    sql = """SELECT *
                        |FROM "$tableName"
                        |WHERE "$publishedColName" = true
                        |ORDER BY "$sortIndexColName" ASC""".trimMargin(),

                    mapper = ArticleTable.rowMapper()
            )

    fun findByUrlComponent(component: String): Article? =
            session.select(
                    sql = """SELECT *
                        |FROM "$tableName"
                        |WHERE "$urlComponentColName" = :component
                        |LIMIT 1""".trimMargin(),

                    parameters = mapOf("component" to component),
                    mapper = ArticleTable.rowMapper()
            ).firstOrNull()

}

/*
CREATE TABLE public.articles (
	id uuid NOT NULL,
	"linkText" varchar(256) NOT NULL,
	"urlPathComponent" varchar(64) NOT NULL,
	"metaTitle" varchar(256) NOT NULL,
	"metaDescription" varchar(256) NOT NULL,
	"metaKeywords" varchar(256) NOT NULL,
	heading varchar(256) NOT NULL,
	"bodyMarkup" text NOT NULL,
	"sortIndex" int4 NOT NULL,
	published bool NOT NULL,
	"vkPostId" varchar(64) NOT NULL,
	"vkPostHash" varchar(64) NOT NULL,
	"createdAt" timestamp NOT NULL,
	"lastModified" timestamp NOT NULL,
	CONSTRAINT articles_pk PRIMARY KEY (id)
)
WITH (
	OIDS=FALSE
) ;
 */
