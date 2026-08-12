# brainstorm - student management

take home is basically student + course management.

don't overbuild this. main goal is show angular + spring boot working end to end and make sure CRUD, forms, routing, errors, rxjs etc are covered.

## Envisioning

two main things

- students
- courses

student can have multiple courses.

home page can just have two cards/buttons

- Students
- Courses

and nav on top/hamburger.

dont think home needs anything fancy.

---

## student page

student list/search page.

top:

- Students header
- search
- Add Student button
- What if it returns hundreds of records? 
- consider table headers with search ? almost like 

table something like

| Name | Email | DOB | Courses | Actions |
|------|-------|-----|---------|---------|

actions

- view
- edit
- delete

delete should ask confirmation. this can cover modal confirmation requirement too.

search by name is enough probably.

could make it partial search, so typing john returns John Smith, Johnny etc.

not sure if search needs backend or frontend.
for take home either is fine but backend query param looks a little better.

`GET /api/students?search=john`

---

## add/edit student

reuse same form for create and edit.

fields

- first name
- last name
- email
- DOB

dont need address, phone, parents, all that.

validation

- first name required
- last name required
- email required
- valid email
- DOB cant be future

show errors under field.

create -> POST

edit -> PUT

after save go back to student list or student profile.

---

## student details

when clicking student show profile.

something like

John Smith

email
DOB

then enrolled courses under it.

| Course Code | Course Name | Action |
|---|---|---|

action can be Remove.

buttons somewhere

- Edit Student
- Enroll in Course
- Delete Student

Enroll can open modal or take to a small selection screen.

dont need separate huge enrollment module unless it becomes easier.

---

## enroll course

click Enroll in Course.

show dropdown or list of courses.

dont show already enrolled ones if possible.

user selects course and clicks enroll.

backend should check:

- student exists
- course exists
- student is not already enrolled

API could be

`POST /api/students/{studentId}/courses/{courseId}`

remove

`DELETE /api/students/{studentId}/courses/{courseId}`

duplicate enrollment should return good error, not 500.

maybe 409.

Angular can show snackbar

"Student is already enrolled in this course"

---

## courses page

same style as student page.

header
search bar
Add Course button

table

| Code | Course Name | Description | Students | Actions |
|------|-------------|-------------|----------|---------|

actions

- view
- edit
- delete

search by course name or code.

course delete confirmation modal.

question: what if course has students?

probably dont cascade delete enrollment silently.

either block delete and return message

"Course cannot be deleted while students are enrolled"

or remove mapping first.

blocking delete feels safer and gives another backend validation example.

---

## add/edit course

fields

- course code
- course name
- description

validation

- code required
- name required
- code unique

same component for create/edit.

---

## course details

show

course code
course name
description

then list students enrolled.

| Student | Email |
|---------|-------|

could have count at top too.

example

Students Enrolled: 8

---

## database


Student
----------------
- id
- first_name
- last_name
- email
- date_of_birth

Course
----------------
- id
- course_code
- course_name
- description

student_course
----------------
- student_id
- course_id

many to many.

need to decide whether to use direct `@ManyToMany` or make Enrollment entity.

for this assignment direct many-to-many is probably enough because there is no grade/date/status fields.

if later adding grade/semester then Enrollment entity makes more sense.

dont solve problem i dont have yet.

---

## backend structure

probably

controller
service
repository
entity
dto
exception

StudentController
StudentService
StudentRepository

CourseController
CourseService
CourseRepository

dto maybe

StudentRequest
StudentResponse
CourseRequest
CourseResponse

do not return entity directly if relationship causes recursion issues.

could also keep dto simple and map manually.
dont bring mapstruct for 4 fields.

---

## student APIs

`GET /api/students`

`GET /api/students/{id}`

`POST /api/students`

`PUT /api/students/{id}`

`DELETE /api/students/{id}`

optional search

`GET /api/students?search=john`

enroll

`POST /api/students/{studentId}/courses/{courseId}`

remove

`DELETE /api/students/{studentId}/courses/{courseId}`

---

## course APIs

`GET /api/courses`

`GET /api/courses/{id}`

`POST /api/courses`

`PUT /api/courses/{id}`

`DELETE /api/courses/{id}`

optional search

`GET /api/courses?search=java`

---

## errors

need GlobalExceptionHandler for proper consistent error message.

cases

- student not found -> 404
- course not found -> 404
- bad request / validation -> 400
- duplicate enrollment -> 409
- duplicate course code -> 409 maybe
- delete course with students -> 409

unexpected stuff -> 500 but dont expose stack trace.

Angular should show useful error.

---

## transaction

good place to use `@Transactional` is enroll/remove course.

enroll flow:

1. get student
2. get course
3. check already enrolled
4. add course
5. save

all one transaction.

delete course may need transaction too.

---

## angular

probably Angular Material because table, dialog, forms, buttons are already there.

structure maybe

students/
- student-list
- student-detail
- student-form

courses/
- course-list
- course-detail
- course-form

common/
- confirm-dialog

services/
- student.service
- course.service

routes

`/`
`/students`
`/students/new`
`/students/:id`
`/students/:id/edit`

`/courses`
`/courses/new`
`/courses/:id`
`/courses/:id/edit`

---

## rxjs

dont force ngrx.

HttpClient already gives Observable.

student service methods return Observable<Student[]> etc.

if need component communication, can use BehaviorSubject for refreshing student list / current state.

could do

`students$`

with BehaviorSubject.


---

## forms

Reactive Forms.

Student form:

-- angular material
firstName
lastName
email
dateOfBirth

Course form:

courseCode
courseName
description

front end validation and error messages .

backend validation still required because frontend validation can be bypassed from postman.

---

## tests

backend unit tests mainly service layer.

StudentServiceTest

- create success
- get success
- get not found
- update
- delete
- search maybe

CourseServiceTest

- create success
- duplicate code
- get not found
- delete with students maybe ?

Enrollment

- enroll success
- duplicate enrollment
- student not found
- course not found
- remove success

mock repositories with Mockito.

dont spend whole assignment writing 50 tests.

cover business rules and failure paths.

---

## rest template???

we dont have any service to service communication, so, no this.

---

## H2

use H2 to reduce setup friction.

people should be able to clone and run.

could seed some students/courses with data.sql.

example:

3 students
4 courses
couple enrollments

makes UI immediately usable.

---

## README

important because they specifically ask setup instructions.

include

- project overview
- tech stack
- prerequisites
- run backend
- run frontend
- H2 info
- API summary
- assumptions
- tests
- screenshots maybe

commands should be exact.

backend

`greadle bootRun`

frontend

`npm install`
`ng serve`

---

## UI feel

clean nav
material table
forms
dialogs
snackbar
loading spinner

make empty states.

"No students found"

"No courses enrolled"

maybe disable buttons while save is happening so duplicate requests dont happen.

---

## things nice to have if time permits

- semesters
- professor
- grades
- GPA
- attendance
- course capacity
- login/auth
- roles
- pagination
- microservices

---

## order to build

backend first

1. entities
2. repositories
3. services
4. controllers
5. exception handling
6. tests

then angular

1. routes
2. services
3. student pages
4. course pages
5. enrollment
6. delete dialogs
7. errors/loading
8. cleanup styling

finally README.

---

## Features to check

i can

- create student
- edit student
- delete student
- view student
- create course
- edit course
- delete course
- view course
- enroll student in course
- remove course from student
- navigate between pages
- see validation errors
- see backend errors properly
- refresh page and data is still there
- run backend tests
- another developer can follow README and start it
