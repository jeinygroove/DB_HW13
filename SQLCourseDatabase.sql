drop table if exists Teacher_Course_Faculty;
drop table if exists Student_Grade;
drop table if exists Student_Unit;
drop table if exists Unit;
drop table if exists Student;
drop table if exists Course;
drop table if exists Teacher;
drop table if exists Faculty;

create table Faculty(
        faculty_id serial primary key,
        faculty_name text
);

create table Course(
        course_id serial primary key,
        course_name text
);

create table Teacher(
        teacher_id serial primary key,
        last_name text not null,
        first_name text not null,
        birthday date,
        curator_id int,

        constraint fk_teacher_teacher
                foreign key (curator_id)
                references Teacher(teacher_id)
);

create table Student(
        student_id serial primary key,
        student_number text not null,
        last_name text not null,
        first_name text not null,
        gender int not null,
        birthday date
);

create table Unit(
        unit_id serial primary key,
        unit_number text,
        faculty_id int not null,

        constraint fk_unit_faculty
                foreign key (faculty_id)
                references Faculty(faculty_id)
);

create table Student_Unit(
        student_unit_id serial primary key,
        unit_id int not null,
        student_id int not null,
        is_head int not null,

        constraint fk_student_student_unit
                foreign key (unit_id)
                references Unit(unit_id),

        constraint fk_student_unit_student
                foreign key (student_id)
                references Student(student_id)
);

create table Teacher_Course_Faculty(
        teacher_course_faculty_id serial primary key,
        teacher_id int not null,
        course_id int not null,
        faculty_id int not null,
        duration numeric(8,2),

        constraint fk_teacher_course_faculty_teacher
                foreign key (teacher_id)
                references Teacher(teacher_id),

        constraint fk_teacher_course_faculty_course
                foreign key (course_id)
                references Course(course_id),

        constraint fk_teacher_course_faculty_faculty
                foreign key (faculty_id)
                references Faculty(faculty_id)
);

create table Student_grade(
        student_grade_id serial primary key,
        student_id int not null,
        course_id int not null,
        grade int not null,

        constraint fk_student_grade_student
                foreign key (student_id)
                references Student(student_id),

        constraint fk_student_grade_course
                foreign key (course_id)
                references Course(course_id)
);

insert into Faculty
	(faculty_name)
values
	('Математико-механический'),
	('Физический'),
	('Экономический');

insert into Course
	(course_name)
values
	('Алгебра'),
	('Геометрия'),
	('Высшая математика'),
	('Оптика'),
	('История'),
	('Теория вероятностей'),
	('Экономическая теория');

insert into Teacher
	(last_name, first_name, birthday, curator_id)
values
	('Петров', 'Иван', '19640413', null),
	('Никитин', 'Сергей', '19610623', null),
	('Иванов', 'Олег', '19451001', null),
	('Носов', 'Максим', '19721225', 2),
	('Алексеев', 'Алексей', '19690312', null),
	('Данилов', 'Александр', '19790422', 1),
	('Сидоров', 'Денис', '19750502', 1),
	('Лаптев', 'Андрей', '19710719', null),
	('Стеклов', 'Никита', '19500820', 2),
	('Оленев', 'Игорь', '19401025', 3);

insert into Student
	(student_number, last_name, first_name, gender, birthday)
values
	('12', 'Таранов', 'Максим', 1, '19891212'),
	('23', 'Жуков', 'Андрей', 1, '19891023'),
	('45', 'Шарапова', 'Анна', 0, '19860107'),
	('67', 'Власова', 'Светлана', 0, '19860308'),
	('89', 'Алиев', 'Искандер', 1, '19860427'),
	('234', 'Кузнецов', 'Павел', 1, '19850502'),
	('567', 'Кузнецова', 'Алла', 0, '19850507'),
	('28', 'Миронов', 'Андрей', 1, '19850528'),
	('13', 'Голубева', 'Полина', 0, '19850507'),
	('93', 'Орлов', 'Андрей', 1, '19850917'),
	('123', 'Толстой', 'Стас', 1, '19890606'),
	('654', 'Иванов', 'Сергей', 1, '19851207'),
	('987', 'Сидорова', 'Анастасия', 0, '19841124');

insert into Unit
	(unit_number, faculty_id)
values
	('13', 1),
	('14', 1),
	('13', 2),
	('13', 3),
	('15', 2);

insert into Student_Unit
	(unit_id, student_id, is_head)
values
	(1, 1, 1),
	(1, 2, 0),
	(1, 3, 0),
	(2, 4, 1),
	(2, 5, 0),
	(2, 6, 0),
	(2, 7, 0),
	(3, 8, 1),
	(3, 9, 0),
	(4, 10, 1),
	(4, 11, 0),
	(4, 12, 0),
	(5, 13, 1);


insert into Student_grade
	(student_id, course_id, grade)
values
	(1, 1, 5),
	(1, 2, 4),
	(1, 3, 4),
	(1, 4, 3),
	(1, 7, 5),
	(2, 1, 4),
	(2, 3, 5),
	(2, 6, 3),
	(2, 7, 5),
	(3, 2, 4),
	(3, 3, 4),
	(3, 5, 3),
	(3, 6, 4),
	(4, 1, 5),
	(4, 6, 4),
	(5, 7, 5),
	(5, 4, 4),
	(6, 2, 3),
	(6, 3, 5),
	(6, 7, 4),
	(7, 3, 4),
	(7, 7, 5),
	(7, 4, 4),
	(8, 1, 3),
	(8, 2, 5),
	(8, 7, 4),
	(8, 3, 5),
	(9, 2, 4),
	(10, 1, 4),
	(10, 2, 3),
	(10, 3, 4),
	(10, 6, 3),
	(12, 3, 4);

insert into Teacher_Course_Faculty
	(teacher_id, course_id, faculty_id, duration)
values
	(1, 1, 1, 80),
	(1, 2, 1, 56),
	(1, 3, 2, 90),
	(2, 6, 1, 40),
	(3, 4, 2, 32),
	(4, 7, 3, 202),
	(5, 3, 1, 86),
	(5, 3, 3, 62),
	(7, 4, 2, 34),
	(7, 6, 2, 24),
	(8, 2, 3, 26),
	(9, 1, 1, 88),
	(9, 3, 3, 74),
	(9, 6, 3, 62);