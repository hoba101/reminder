package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.StudyDao
import com.example.data.model.StudyItem
import com.example.data.model.Subject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Subject::class, StudyItem::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studyDao(): StudyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "girly_study_pink_v9"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                scope.launch(Dispatchers.IO) {
                    ensurePopulated(instance.studyDao())
                }
                instance
            }
        }

        suspend fun ensurePopulated(dao: StudyDao) {
            val count = dao.getSubjectsCount()
            if (count == 9) return // Exactly the 9 required university subjects
            // If count is 0 or corrupted/duplicated, clear and repopulate cleanly
            dao.clearStudyItems()
            dao.clearSubjects()
            prepopulatePosterCurriculum(dao)
        }

        suspend fun prepopulatePosterCurriculum(dao: StudyDao) {
            val subjectsWithTopics = listOf(
                PosterSubjectData(
                    name = "كيمياء فيزيائية",
                    code = "CHM 111",
                    hasTheory = true,
                    hasPractical = true,
                    iconKey = "chemistry",
                    accentColor = "#F06292",
                    keyConcepts = "الديناميكا الحرارية • اتزان كيميائي • سرعة التفاعل • المحاليل • الخلايا الجلفانية • الطيفية",
                    learningGoal = "كيف تتغير الطاقة والاتزان في التفاعلات الكيميائية، وكيف تؤثر الظروف الفيزيائية على سلوك الأنظمة الكيميائية.",
                    topics = listOf(
                        "مبادئ الديناميكا الحرارية وقوانينها",
                        "الاتزان الكيميائي ومفهوم ثابت الاتزان",
                        "سرعة التفاعلات الكيميائية وآلياتها",
                        "الخواص الفيزيائية للمحاليل والإلكتروليتات",
                        "الكيمياء الكهربائية (الخلايا الكهروكيميائية)",
                        "أساسيات الطيفية والخواص الجزيئية"
                    )
                ),
                PosterSubjectData(
                    name = "مقدمة في المعادن والصخور",
                    code = "GEO 111",
                    hasTheory = true,
                    hasPractical = true,
                    iconKey = "minerals",
                    accentColor = "#BA68C8",
                    keyConcepts = "المعادن • الصخور النارية • الصخور الرسوبية • الصخور المتحولة • دورة الصخور",
                    learningGoal = "كيفية تكون المعادن والصخور، وكيفية التعرف عليها في الواقع والمختبر، ودور كل نوع في تشكيل سطح الأرض.",
                    topics = listOf(
                        "خصائص المعادن وتصنيفها",
                        "أنواع الصخور (نارية، رسوبية، متحولة)",
                        "طرق تكون الصخور والعمليات الجيولوجية",
                        "التركيب المعدني والصخري للأرض",
                        "استخدام المجاهر والطرق العملية للتعرف على المعادن والصخور"
                    )
                ),
                PosterSubjectData(
                    name = "رياضيات عامة (1)",
                    code = "MAS 111",
                    hasTheory = true,
                    hasPractical = true,
                    iconKey = "math",
                    accentColor = "#FF8A80",
                    keyConcepts = "الأعداد • الجبر • الدوال • الهندسة التحليلية • التفاضل والتكامل (أساسيات)",
                    learningGoal = "كيفية التعامل مع المسائل الرياضية وحلها خطوة بخطوة، والربط بين المفاهيم النظرية والتطبيقات العملية.",
                    topics = listOf(
                        "الأسس والجذور واللوغاريتمات",
                        "المعادلات والمتباينات",
                        "الدوال (الخطية، التربيعية، المثلثية)",
                        "الحدود والمتتاليات",
                        "الهندسة التحليلية والجبرية الأساسية",
                        "التفاضل والتكامل (مقدمة)"
                    )
                ),
                PosterSubjectData(
                    name = "فيزياء عامة (1)",
                    code = "PHS 111",
                    hasTheory = true,
                    hasPractical = true,
                    iconKey = "physics",
                    accentColor = "#FF80AB",
                    keyConcepts = "الميكانيكا • حرارة • كهرباء • مغناطيسية • الموجات (مقدمة)",
                    learningGoal = "مفاهيم الحركة والقوى والطاقة وكيفية تطبيقها على أمثلة واقعية، بالإضافة إلى أساسيات الكهرباء والمغناطيسية.",
                    topics = listOf(
                        "الكميات الفيزيائية ووحداتها",
                        "الحركة والقوانين الحركية لنيوتن",
                        "الطاقة والشغل والقدرة",
                        "الزخم والتصادمات",
                        "الحرارة والغازات",
                        "الكهرباء والمغناطيسية (أساسيات)"
                    )
                ),
                PosterSubjectData(
                    name = "تصنيف مملكة حيوانية",
                    code = "ZOL 111",
                    hasTheory = true,
                    hasPractical = true,
                    iconKey = "zoology",
                    accentColor = "#4DD0E1",
                    keyConcepts = "ممالك الحيوانات • اللافقاريات • الفقاريات • شعب الحيوان • التصنيف التطوري",
                    learningGoal = "كيف يتم تصنيف الحيوانات بناءً على صفاتها التشريحية والوراثية، والتعرف على أهم المجموعات الحيوانية.",
                    topics = listOf(
                        "خصائص المملكة الحيوانية وتنوعها",
                        "أسس التصنيف الحيواني",
                        "دراسة صفات المجموعات الرئيسية (اللافقاريات والفقاريات)",
                        "أهم الشعب والمجاميع الحيوانية",
                        "التكيفات البيئية والسلوكية للحيوانات"
                    )
                ),
                PosterSubjectData(
                    name = "جيولوجيا طبيعية",
                    code = "PWC 111",
                    hasTheory = true,
                    hasPractical = false,
                    iconKey = "geology",
                    accentColor = "#FFB74D",
                    keyConcepts = "البنية الداخلية للأرض • البراكين • الزلازل • الدورات الجيولوجية • الجيومورفولوجيا • التراكيب الجيولوجية",
                    learningGoal = "كيف تتشكل الأرض، وما هي القوى والعمليات التي تغير سطحها وتؤثر على تضاريسها ومكوناتها.",
                    topics = listOf(
                        "تكوين الأرض وبنيتها الداخلية",
                        "البراكين والزلازل",
                        "الدورات الجيولوجية (الماء، الكربون، الصخور)",
                        "العمليات الجيومورفولوجية",
                        "الطبقات الأرضية والتراكيب الجيولوجية"
                    )
                ),
                PosterSubjectData(
                    name = "لغة إنجليزية (1)",
                    code = "HUM 111",
                    hasTheory = true,
                    hasPractical = false,
                    iconKey = "english",
                    accentColor = "#9575CD",
                    keyConcepts = "Grammar • Reading • Writing • Vocabulary • Listening & Speaking (أساسيات)",
                    learningGoal = "كيفية استخدام اللغة الإنجليزية في فهم النصوص، وكتابة جمل صحيحة، والتواصل بشكل أساسي في المواقف الأكاديمية.",
                    topics = listOf(
                        "مهارات القراءة والفهم",
                        "مهارات الكتابة الأكاديمية",
                        "قواعد اللغة الإنجليزية الأساسية",
                        "المفردات والتعبيرات الشائعة",
                        "التواصل والاستماع في المواقف الأكاديمية"
                    )
                ),
                PosterSubjectData(
                    name = "حاسب آلي",
                    code = "CSC 111",
                    hasTheory = true,
                    hasPractical = false,
                    iconKey = "computer",
                    accentColor = "#81C784",
                    keyConcepts = "مكونات الحاسوب • أنظمة التشغيل • الملفات والمجلدات • الإنترنت • أساسيات البرمجة",
                    learningGoal = "العمل على الحاسوب، استخدام البرامج الأساسية، وفهم كيف يعمل النظام وكيفية التعامل مع الملفات والإنترنت.",
                    topics = listOf(
                        "أساسيات الحاسب الآلي وأنظمته",
                        "أنظمة التشغيل (مثل Windows / Linux)",
                        "تطبيقات الحاسوب وبرامجه الأساسية",
                        "الإنترنت وخدماته وأمن المعلومات",
                        "مقدمة في البرمجة والتفكير المنطقي"
                    )
                ),
                PosterSubjectData(
                    name = "مقدمة في علم الجودة",
                    code = "QLT 111",
                    hasTheory = true,
                    hasPractical = false,
                    iconKey = "quality",
                    accentColor = "#E57373",
                    keyConcepts = "الجودة • TQM • ISO • الرقابة والجودة • التحسين المستمر • إدارة العمليات",
                    learningGoal = "كيف يتم تطبيق معايير الجودة في المؤسسات، وكيف تؤثر على كفاءة العمل وتحقيق الأهداف.",
                    topics = listOf(
                        "مفهوم الجودة وأهميتها وتاريخها",
                        "مبادئ إدارة الجودة الشاملة (TQM)",
                        "أنظمة ومعايير الجودة العالمية (ISO)",
                        "مراقبة الجودة وضبط العمليات والقياس",
                        "استراتيجيات تحسين وتطوير الأداء المستمر"
                    )
                )
            )

            val subjectsToInsert = subjectsWithTopics.mapIndexed { index, data ->
                Subject(
                    name = data.name,
                    code = data.code,
                    hasTheory = data.hasTheory,
                    hasPractical = data.hasPractical,
                    iconKey = data.iconKey,
                    accentColorHex = data.accentColor,
                    displayOrder = index + 1,
                    keyConcepts = data.keyConcepts,
                    learningGoal = data.learningGoal
                )
            }

            val subjectIds = dao.insertSubjects(subjectsToInsert)
            val itemsToInsert = mutableListOf<StudyItem>()

            subjectIds.forEachIndexed { index, subjectId ->
                val topics = subjectsWithTopics[index].topics
                topics.forEachIndexed { topicIndex, topicTitle ->
                    itemsToInsert.add(
                        StudyItem(
                            subjectId = subjectId,
                            title = topicTitle,
                            isCompleted = false,
                            orderIndex = topicIndex + 1,
                            completedAt = null
                        )
                    )
                }
            }

            dao.insertStudyItems(itemsToInsert)
        }
    }
}

private data class PosterSubjectData(
    val name: String,
    val code: String,
    val hasTheory: Boolean,
    val hasPractical: Boolean,
    val iconKey: String,
    val accentColor: String,
    val keyConcepts: String,
    val learningGoal: String,
    val topics: List<String>
)
