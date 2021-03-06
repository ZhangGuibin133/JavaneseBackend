package online.javanese

import com.github.andrewoma.kwery.core.Session
import com.github.andrewoma.kwery.mapper.Dao
import online.javanese.krud.AdminPanel
import online.javanese.krud.RoutedModule
import online.javanese.krud.crud.Crud
import online.javanese.krud.kwery.*
import online.javanese.krud.stat.HardwareStat
import online.javanese.krud.stat.HitStat
import online.javanese.krud.template.MaterialTemplate
import online.javanese.krud.template.control.*
import online.javanese.model.*


fun JavaneseAdminPanel(
        adminRoute: String,
        session: Session,
        taskDao: Dao<Task, Uuid>, lessonDao: Dao<Lesson, Uuid>, chapterDao: Dao<Chapter, Uuid>,
        courseDao: Dao<Course, Uuid>, articleDao: Dao<Article, Uuid>, pageDao: Dao<Page, Uuid>,
        taskErrorReportDao: Dao<TaskErrorReport, Uuid>, codeReviewCandidateDao: Dao<CodeReviewCandidate, Uuid>,
        codeReviewDao: Dao<CodeReview, Uuid>,
        hitStat: HitStat
): AdminPanel {

    val escape = session.dialect::escapeName
    val uuidGenerator = UuidGeneratingMap("id")

    return AdminPanel("/$adminRoute", MaterialTemplate("/$adminRoute/", "/admin-static"),
            RoutedModule("crud", Crud(
                    KweryTable("task",
                            TaskTable, taskDao,
                            SelectCount(session, session.dialect.escapeName(TaskTable.name)),
                            getTitleOf = TaskTable.LinkText.property,
                            transformColumn = { when (it) {
                                TaskTable.LessonId -> EnumeratedCol(TaskTable.LessonId, KweryForeignEnumeratedColAdapter(LessonTable, lessonDao, LessonTable.LinkText.property))
                                TaskTable.Condition -> TextCol(TaskTable.Condition, createControlFactory = CodeMirror.Html)
                                TaskTable.InitialCode -> TextCol(TaskTable.InitialCode, createControlFactory = TextArea)
                                TaskTable.CodeToAppend -> TextCol(TaskTable.CodeToAppend, createControlFactory = TextArea)
                                TaskTable.CheckRules -> TextCol(TaskTable.CheckRules, createControlFactory = TextArea)
                                TaskTable.ExpectedOutput -> TextCol(TaskTable.ExpectedOutput, createControlFactory = TextArea)
                                TaskTable.SortIndex -> TextCol(TaskTable.SortIndex, createControlFactory = TextInput.Editable, editControlFactory = TextInput.ReadOnly)
                                else -> KweryTable.TransformKweryColumn<Task>()(it)
                            } },
                            sort = KweryExplicitSort(session, escape, TaskTable, TaskTable.SortIndex),
                            fallback = uuidGenerator
                    ),
                    KweryTable("lesson",
                            LessonTable, lessonDao,
                            SelectCount(session, session.dialect.escapeName(LessonTable.name)),
                            getTitleOf = LessonTable.LinkText.property,
                            transformColumn = { when (it) {
                                LessonTable.ChapterId -> EnumeratedCol(LessonTable.ChapterId, KweryForeignEnumeratedColAdapter(ChapterTable, chapterDao, ChapterTable.LinkText.property))
                                LessonTable.BodyMarkup -> TextCol(LessonTable.BodyMarkup, createControlFactory = CodeMirror.Html)
                                LessonTable.SortIndex -> TextCol(LessonTable.SortIndex, createControlFactory = TextInput.Editable, editControlFactory = TextInput.ReadOnly)
                                else -> KweryTable.TransformKweryColumn<Lesson>()(it)
                            } },
                            sort = KweryExplicitSort(session, escape, LessonTable, LessonTable.SortIndex),
                            fallback = uuidGenerator
                    ),
                    KweryTable("chapter",
                            ChapterTable, chapterDao,
                            SelectCount(session, session.dialect.escapeName(ChapterTable.name)),
                            getTitleOf = ChapterTable.LinkText.property,
                            transformColumn = { when (it) {
                                ChapterTable.CourseId -> EnumeratedCol(ChapterTable.CourseId, KweryForeignEnumeratedColAdapter(CourseTable, courseDao, CourseTable.LinkText.property))
                                ChapterTable.Description -> TextCol(ChapterTable.Description, createControlFactory = CodeMirror.Html)
                                ChapterTable.SortIndex -> TextCol(ChapterTable.SortIndex, createControlFactory = TextInput.Editable, editControlFactory = TextInput.ReadOnly)
                                else -> KweryTable.TransformKweryColumn<Chapter>()(it)
                            } },
                            sort = KweryExplicitSort(session, escape, ChapterTable, ChapterTable.SortIndex),
                            fallback = uuidGenerator
                    ),
                    KweryTable("course",
                            CourseTable, courseDao,
                            SelectCount(session, session.dialect.escapeName(CourseTable.name)),
                            getTitleOf = CourseTable.LinkText.property,
                            transformColumn = { when (it) {
                                CourseTable.Description -> TextCol(CourseTable.Description, createControlFactory = CodeMirror.Html)
                                CourseTable.SortIndex -> TextCol(CourseTable.SortIndex, createControlFactory = TextInput.Editable, editControlFactory = TextInput.ReadOnly)
                                else -> KweryTable.TransformKweryColumn<Course>()(it)
                            } },
                            sort = KweryExplicitSort(session, escape, CourseTable, CourseTable.SortIndex),
                            fallback = uuidGenerator
                    ),
                    KweryTable("article",
                            ArticleTable, articleDao,
                            SelectCount(session, session.dialect.escapeName(ArticleTable.name)),
                            getTitleOf = ArticleTable.LinkText.property,
                            transformColumn = { when (it) {
                                ArticleTable.BodyMarkup -> TextCol(ArticleTable.BodyMarkup, createControlFactory = CodeMirror.Html)
                                else -> KweryTable.TransformKweryColumn<Article>()(it)
                            } },
                            fallback = uuidGenerator
                    ),
                    KweryTable("page",
                            PageTable, pageDao,
                            SelectCount(session, session.dialect.escapeName(PageTable.name)),
                            getTitleOf = PageTable.Heading.property,
                            transformColumn = { when (it) {
                                PageTable.BodyMarkup -> TextCol(PageTable.BodyMarkup, createControlFactory = CodeMirror.Html)
                                PageTable.HeadMarkup -> TextCol(PageTable.HeadMarkup, createControlFactory = CodeMirror.Html)
                                PageTable.BeforeBodyEndMarkup -> TextCol(PageTable.BeforeBodyEndMarkup, createControlFactory = CodeMirror.Html)
                                else -> KweryTable.TransformKweryColumn<Page>()(it)
                            } },
                            sort = KweryExplicitSort(session, escape, PageTable, PageTable.SortIndex),
                            fallback = uuidGenerator
                    ),
                    KweryTable("taskErrorReport", // todo: make read-only
                            TaskErrorReportTable, taskErrorReportDao,
                            SelectCount(session, session.dialect.escapeName(TaskErrorReportTable.name)),
                            getTitleOf = { it.errorKind.name },
                            transformColumn = { when (it) {
                                TaskErrorReportTable.Code -> TextCol(TaskErrorReportTable.Code, createControlFactory = TextArea)
                                TaskErrorReportTable.Text -> TextCol(TaskErrorReportTable.Text, createControlFactory = TextArea)
                                else -> KweryTable.TransformKweryColumn<TaskErrorReport>()(it)
                            } },
                            fallback = uuidGenerator
                    ),
                    KweryTable("codeReviewCandidate", // todo: make read-only
                            CodeReviewCandidateTable, codeReviewCandidateDao,
                            SelectCount(session, session.dialect.escapeName(CodeReviewCandidateTable.name)),
                            getTitleOf = CodeReviewCandidateTable.SenderName.property,
                            transformColumn = { when (it) {
                                CodeReviewCandidateTable.ProblemStatement -> TextCol(CodeReviewCandidateTable.ProblemStatement, createControlFactory = TextArea)
                                CodeReviewCandidateTable.Code -> TextCol(CodeReviewCandidateTable.Code, createControlFactory = TextArea)
                                else -> KweryTable.TransformKweryColumn<CodeReviewCandidate>()(it)
                            } },
                            fallback = uuidGenerator
                    ),
                    KweryTable("codeReview",
                            CodeReviewTable, codeReviewDao,
                            SelectCount(session, session.dialect.escapeName(CodeReviewTable.name)),
                            getTitleOf = CodeReviewTable.MetaTitle.property,
                            transformColumn = { when (it) {
                                CodeReviewTable.ProblemStatement -> TextCol(CodeReviewTable.ProblemStatement, createControlFactory = TextArea)
                                CodeReviewTable.ReviewMarkup -> TextCol(CodeReviewTable.ReviewMarkup, createControlFactory = CodeMirror.Html)
                                else -> KweryTable.TransformKweryColumn<CodeReview>()(it)
                            } },
                            fallback = uuidGenerator
                    )
            )),
            RoutedModule("hw", HardwareStat()),
            RoutedModule("hits", hitStat)
    )
}
