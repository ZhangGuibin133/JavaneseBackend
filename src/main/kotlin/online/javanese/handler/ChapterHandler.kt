package online.javanese.handler

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.response.respondText
import online.javanese.model.Chapter
import online.javanese.model.ChapterTree
import online.javanese.model.Course
import online.javanese.model.CourseTree


fun ChapterHandler(
        tree: List<CourseTree>,
        chapterTemplate: (Course, Chapter, ChapterTree, previous: ChapterTree?, next: ChapterTree?) -> String
): suspend (Course, Chapter, ApplicationCall) -> Unit = { course, chapter, call ->

    // fixme: should stop keeping whole graph in memory
    val chapters = tree
            .first { it.id == course.basicInfo.id }
            .chapters

    val chapterTree = chapters
            .first { it.id == chapter.basicInfo.id }

    val idx = chapters.indexOf(chapterTree)
    val prev = (idx - 1).let { if (it < 0) null else chapters[it] }
    val next = (idx + 1).let { if (it < chapters.size) chapters[it] else null }

    call.respondText(chapterTemplate(course, chapter, chapterTree, prev, next), ContentType.Text.Html)

}