package online.javanese.page

import kotlinx.html.*
import online.javanese.model.*
import java.util.*

class ChapterPage(
        private val indexPage: Page,
        private val treePage: Page,
        private val course: Course,
        private val chapter: Chapter,
        private val chapterTree: ChapterTree,
        private val previousChapter: ChapterTree?,
        private val nextChapter: ChapterTree?,
        private val pageLink: Link<Page>,
        private val courseLink: Link<Course.BasicInfo>,
        private val urlOfChapter: (ChapterTree) -> String,
        private val urlOfLesson: (LessonTree) -> String,
        private val urlOfTask: (TaskTree) -> String,
        private val messages: Properties
) : Layout.Page {
// fixme: should be Chapter.BasicInfo ^          ^

    override val meta: Meta get() = chapter.meta

    override fun additionalHeadMarkup(head: HEAD) = Unit

    override fun bodyMarkup(body: BODY) = with(body) {
        contentCardDiv {
            nav {
                pageLink.insert(this, indexPage)
                +" / "
                pageLink.insert(this, treePage)
                +" / "
                courseLink.insert(this, course.basicInfo)
            }

            main {
                h1(classes = "content-padding-v") {
                    +chapter.h1
                }

                unsafe {
                    +chapter.description
                }
            }

            nav {
                TreeMode.Lessons.lessonsTree(this, chapterTree.lessons, urlOfLesson, urlOfTask)
            }

            prevNextPane(
                    previousChapter, nextChapter, urlOfChapter, ChapterTree::linkText,
                    messages.getProperty("chapter.previous"), messages.getProperty("chapter.next")
            )
        }
    }

    override fun scripts(body: BODY) = Unit

}