# Assessment Feedback System (AFS)

A comprehensive Java Swing-based Assessment Feedback System for educational institutions.

## Features

### Admin Dashboard
- Manage users (Students, Lecturers, Academic Leaders)
- Auto-generate user IDs and default passwords
- Manage classes and grading system
- Delete users

### Academic Leader Dashboard
- View assigned lecturers
- View and manage modules
- Assign lecturers to modules
- Generate reports with statistics and charts
- View pass/fail distribution

### Lecturer Dashboard
- Create and manage assessments
- Grade students with marks and feedback
- View all assessments for assigned module

### Student Dashboard
- Enroll in available classes
- View results with grades and feedback
- Track academic performance

## Default Login Credentials

### Admin
- **User ID:** AD00001
- **Password:** AD0000101011990

### Academic Leaders
- **User ID:** AL10001, AL10002, AL10003
- **Password:** [ID]01011985, [ID]01011986, [ID]01011987

### Lecturers
- **User ID:** LC20001 to LC20009
- **Password:** [ID]01011980

### Students
- **User ID:** TP30001 to TP30015
- **Password:** [ID]01012000

## System Requirements
- JDK 17 or higher
- No external libraries required (uses only Java standard libraries)

## Project Structure
```
AssessmentFeedbackSystem/
├── src/
│   ├── Main.java
│   ├── admin/
│   │   ├── Admin.java
│   │   └── AdminDashboard.java
│   ├── common/
│   │   ├── User.java
│   │   ├── LoginFrame.java
│   │   └── ProfileEditor.java
│   ├── leader/
│   │   ├── AcademicLeader.java
│   │   └── AcademicLeaderDashboard.java
│   ├── lecturer/
│   │   ├── Lecturer.java
│   │   └── LecturerDashboard.java
│   ├── student/
│   │   ├── Student.java
│   │   └── StudentDashboard.java
│   └── utils/
│       ├── DataSeeder.java
│       ├── FileHandler.java
│       └── Session.java
└── data/
    ├── users.txt
    ├── modules.txt
    ├── classes.txt
    ├── grading.txt
    ├── assessments.txt
    ├── results.txt
    └── enrollments.txt
```

## Compilation and Execution

### On Windows:
```batch
compile.bat
run.bat
```

### On Linux/Mac:
```bash
chmod +x compile.sh run.sh
./compile.sh
./run.sh
```

### Manual Compilation:
```bash
# Navigate to project root
cd AssessmentFeedbackSystem

# Compile all Java files
javac -d bin src/**/*.java src/*.java

# Run the application
java -cp bin Main
```

## Data Persistence
All data is stored in text files in the `data/` directory:
- **users.txt** - User accounts
- **modules.txt** - Course modules
- **classes.txt** - Class information
- **grading.txt** - Grading scale
- **assessments.txt** - Assessment details
- **results.txt** - Student results
- **enrollments.txt** - Student enrollments

## Features by Role

### Admin
✓ Add/Delete users
✓ Auto-generate user credentials
✓ Manage classes
✓ Edit grading ranges
✓ Profile management

### Academic Leader
✓ View supervised lecturers
✓ View assigned modules
✓ Assign lecturers to modules
✓ Generate statistical reports
✓ Visual pass/fail charts

### Lecturer
✓ Create assessments (Quiz, Assignment, Exam, Project)
✓ Grade students
✓ Provide feedback
✓ View all students

### Student
✓ Enroll in classes
✓ View results and grades
✓ View feedback from lecturers
✓ Track academic performance

## Color Theme
- Primary Blue: RGB(41, 128, 185)
- Success Green: RGB(46, 204, 113)
- Danger Red: RGB(231, 76, 60)
- Info Blue: RGB(52, 152, 219)
- Neutral Gray: RGB(127, 140, 141)
- Background: White

## Notes
- The system automatically seeds initial data on first run
- User IDs follow the pattern: AD(Admin), AL(Leader), LC(Lecturer), TP(Student)
- Default passwords are generated as: UserID + DateOfBirth (without slashes)
- All users can edit their profile (password, email, phone)
- File format uses " | " as delimiter

## Future Enhancements
- Export reports to PDF
- Email notifications
- Bulk student import
- Advanced analytics
- Mobile responsive version
