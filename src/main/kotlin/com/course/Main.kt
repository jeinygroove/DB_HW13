package com.course

import com.course.models.Course
import com.course.models.Student
import com.course.models.StudentGrade
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

/***
 * Returns a list of course ids with name {courseName}
 */
fun getCourseIdsByName(courseName: String): List<Int> {
    return transaction {
        Course.select { Course.courseName eq courseName }
                .map { course -> course[Course.courseId] }.toList()
    }
}

/***
 * Returns a list of student ids with last name {lastName}
 */
fun getStudentIdsByLastName(lastName: String): List<Int> {
    return transaction {
        Student.slice(Student.studentId, Student.lastName)
                .select { Student.lastName eq lastName }
                .map { student -> student[Student.studentId] }.toList()
    }
}

fun main(args: Array<String>) {
    if (args.size < 3) {
        println("Not enough arguments! Usage: ./gradlew run <url> <driver> <user> [password]")
        return
    }

    Database.connect(
            url = args[0], driver = args[1],
            user = args[2], password = args.getOrElse(3) { "" }
    )

    val msgWrong = "Wrong command! Try again."
    var command: String?
    loop@ while (true) {
        print("\nPrint command/exit: ")
        command = readLine()
        val params = command?.split(";")?.map { param -> param.trim() }
        if (params == null) {
            print(msgWrong)
        } else {
            when (params.size) {
                1 -> when (params[0]) {
                    "c" -> {
                        val courses = transaction {
                            Course.selectAll().orderBy(Course.courseName to SortOrder.ASC)
                                    .mapIndexed { i, course ->
                                        "${i + 1}) Course ID: ${course[Course.courseId]}, Course Name: ${course[Course.courseName]}"
                                    }.joinToString(separator = "\n")
                        }
                        println(courses)
                    }
                    "s" -> {
                        val studentNames = transaction {
                            Student.slice(Student.firstName, Student.lastName).selectAll()
                                    .mapIndexed { i, studentName ->
                                        "${i + 1}) Last name: ${studentName[Student.lastName]}, First name: ${studentName[Student.firstName]}"
                                    }.joinToString(separator = "\n")
                        }
                        println(studentNames)
                    }
                    "exit" -> break@loop
                    else -> print(msgWrong)
                }
                2, 3 -> {
                    if (params[0] != "g") {
                        print(msgWrong)
                    } else {
                        val courseName = params[1]
                        val lastName = params.getOrNull(2)
                        val courseIds = getCourseIdsByName(courseName)
                        if (courseIds.isEmpty()) {
                            println("Course $courseName doesn't exist")
                            continue@loop
                        }

                        val selectOps: List<Op<Boolean>>
                        when (lastName) {
                            null -> {
                                selectOps = courseIds.map { courseId ->
                                    Op.build { StudentGrade.courseId eq courseId }
                                }
                            }
                            else -> {
                                val studentIds = getStudentIdsByLastName(lastName)
                                if (studentIds.isEmpty()) {
                                    println("Student with last name $lastName doesn't exist")
                                    continue@loop
                                }
                                selectOps = courseIds.map { courseId ->
                                    Op.build { StudentGrade.courseId eq courseId and (Student.studentId inList studentIds) }
                                }
                            }
                        }

                        selectOps.zip(courseIds).forEach { (selectOp, courseId) ->
                            val studentGradeInfo = transaction {
                                StudentGrade.join(
                                        Student,
                                        JoinType.LEFT,
                                        additionalConstraint = { StudentGrade.studentId eq Student.studentId })
                                        .slice(Student.lastName, Student.firstName, StudentGrade.grade)
                                        .select(selectOp)
                                        .mapIndexed { i, studentGradeInfo ->
                                            "${i + 1}) Student: ${studentGradeInfo[Student.lastName]}" +
                                                    " ${studentGradeInfo[Student.firstName]}," +
                                                    " Grade: ${studentGradeInfo[StudentGrade.grade]}"
                                        }.joinToString(separator = "\n")
                            }
                            if (studentGradeInfo.isBlank()) {
                                println("Student $lastName doesn't have a grade for the course $courseName")
                            } else {
                                println("Course ID: $courseId, Grades: ")
                                println(studentGradeInfo)
                            }
                        }
                    }
                }
                4 -> {
                    if (params[0] != "u") {
                        print(msgWrong)
                    } else {
                        val courseName = params[1]
                        val lastName = params[2]
                        val grade = params[3].toIntOrNull()

                        if (grade == null) {
                            println("Grade should be a number!")
                            continue@loop
                        }

                        val courseIds = getCourseIdsByName(courseName)
                        when (courseIds.size) {
                            0 -> {
                                println("Course $courseName doesn't exist")
                                continue@loop
                            }
                            1 -> {
                            }
                            else -> {
                                println(
                                        "Course with name $courseName isn't unique," +
                                                " I[the program] don't think that you are" +
                                                " going to add grade to multiple courses"
                                )
                                continue@loop
                            }
                        }
                        val courseId = courseIds[0]

                        val studentIds = getStudentIdsByLastName(lastName)
                        when (studentIds.size) {
                            0 -> {
                                println("Student with last name $lastName doesn't exist")
                                continue@loop
                            }
                            1 -> {
                            }
                            else -> {
                                println(
                                        "Student with last name $lastName isn't unique," +
                                                " I[the program] don't think that you are" +
                                                " going to add grade to multiple students"
                                )
                                continue@loop
                            }
                        }
                        val studentId = studentIds[0]

                        val studentGradeId = transaction {
                            StudentGrade.slice(StudentGrade.studentGradeId)
                                    .select { StudentGrade.studentId eq studentId and (StudentGrade.courseId eq courseId) }
                                    .map { it[StudentGrade.studentGradeId] }
                                    .toList().getOrNull(0)
                        }

                        if (studentGradeId == null) {
                            transaction {
                                StudentGrade.insert {
                                    it[StudentGrade.courseId] = courseId
                                    it[StudentGrade.grade] = grade
                                    it[StudentGrade.studentId] = studentId
                                }
                            }
                        } else {
                            transaction {
                                StudentGrade.update({ StudentGrade.studentGradeId eq studentGradeId }) {
                                    it[StudentGrade.courseId] = courseId
                                    it[StudentGrade.grade] = grade
                                    it[StudentGrade.studentId] = studentId
                                }
                            }
                        }
                    }
                }
                else -> print(msgWrong)
            }
        }
    }
}