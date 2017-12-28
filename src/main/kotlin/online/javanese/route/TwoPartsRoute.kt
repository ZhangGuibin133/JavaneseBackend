package online.javanese.route

import io.ktor.application.ApplicationCall
import online.javanese.exception.NotFoundException
import online.javanese.model.*


fun TwoPartsRoute(
        pageDao: PageDao,
        articleDao: ArticleDao,
        courseDao: CourseDao,
        chapterDao: ChapterDao,
        articleHandler: suspend (articlesPage: Page, article: Article, call: ApplicationCall) -> Unit,
        chapterHandler: suspend (Course, Chapter, ApplicationCall) -> Unit
): suspend (ApplicationCall, String, String) -> Unit = f@ { call, first, second ->

    pageDao.findByUrlPathComponent(first)?.let { page ->
        if (page.magic == Page.Magic.Articles) {
            articleDao.findByUrlComponent(second)?.let { article ->
                return@f articleHandler(page, article, call)
            }
        }
    }

    courseDao.findByUrlComponent(first)?.let { course -> // todo: this lookup may be optimized
        chapterDao.findByUrlComponent(second)?.let { chapter ->
            return@f chapterHandler(course, chapter, call)
        }
    }

    throw NotFoundException("neither article nor course exist for address /$first/$second/")
}
