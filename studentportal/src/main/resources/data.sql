insert into student (first_name, last_name, email, date_of_birth)
values ('John', 'Smith', 'john.smith@gmail.com', '2001-04-12');
insert into student (first_name, last_name, email, date_of_birth)
values ('Priya', 'Patel', 'priya.patel@gmail.com', '2002-09-30');
insert into student (first_name, last_name, email, date_of_birth)
values ('Marcus', 'Lee', 'marcus.lee@yahoo.com', '2000-01-22');

insert into course (course_code, course_name, description)
values ('CS101', 'Intro to Programming', 'Basics of programming with Java');
insert into course (course_code, course_name, description)
values ('MATH201', 'Linear Algebra', 'Vectors, matrices and linear systems');
insert into course (course_code, course_name, description)
values ('ENG105', 'Academic Writing', 'Writing and communication skills');
insert into course (course_code, course_name, description)
values ('PHY150', 'Mechanics', 'Classical mechanics with labs');

insert into student_course (student_id, course_id)
values (1, 1);
insert into student_course (student_id, course_id)
values (1, 2);
insert into student_course (student_id, course_id)
values (2, 1);
insert into student_course (student_id, course_id)
values (3, 4);
