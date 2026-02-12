# Quick Start Guide - Assessment Feedback System

## Installation & Setup

1. **Extract the project** to your desired location
2. **Open terminal/command prompt** in the project directory
3. **Compile the project:**
   - Windows: Run `compile.bat`
   - Linux/Mac: Run `./compile.sh`
4. **Run the application:**
   - Windows: Run `run.bat`
   - Linux/Mac: Run `./run.sh`

## First Login

Use the default admin credentials:
- **User ID:** `AD00001`
- **Password:** `AD0000101011990`

## Common Tasks

### As Admin:
1. **Add a new student:**
   - Go to "Manage Users" tab
   - Click "Add User"
   - Fill in details (Name, DOB, Role: Student)
   - System generates ID (e.g., TP30016) and password
   - Share credentials with student

2. **Add a new class:**
   - Go to "Classes" tab
   - Click "Add Class"
   - Enter Class ID, Name, and Module ID

3. **Edit grading scale:**
   - Go to "Grading" tab
   - Select a grade
   - Click "Edit Range"
   - Modify min/max marks

### As Academic Leader:
1. **Assign lecturer to module:**
   - Login with leader credentials (AL10001, password: AL1000101011985)
   - Go to "My Modules" tab
   - Select a module
   - Click "Assign Lecturer"
   - Choose from your supervised lecturers

2. **Generate report:**
   - Go to "Reports" tab
   - Select a module from dropdown
   - Click "Generate Report"
   - View statistics and pass/fail chart

### As Lecturer:
1. **Create assessment:**
   - Login with lecturer credentials (LC20001, password: LC2000101011980)
   - Go to "Assessments" tab
   - Click "Add Assessment"
   - Enter title, type (Quiz/Assignment/Exam/Project), max marks

2. **Grade students:**
   - Go to "Grade Students" tab
   - Select an assessment
   - Click "Load Students"
   - Select a student
   - Click "Enter Marks"
   - Input marks and feedback

### As Student:
1. **Enroll in class:**
   - Login with student credentials (TP30001, password: TP3000101012000)
   - Go to "Enroll in Classes" tab
   - Select a class
   - Click "Enroll"

2. **View results:**
   - Go to "My Results" tab
   - See all grades, marks, and feedback
   - Click "Refresh" to update

## File Locations

All data files are stored in the `data/` folder:
- `users.txt` - All user accounts
- `modules.txt` - Course modules
- `classes.txt` - Available classes
- `assessments.txt` - All assessments
- `results.txt` - Student grades and feedback
- `enrollments.txt` - Student class enrollments
- `grading.txt` - Grading scale (A+, A, B, etc.)

## Password Format

Default passwords follow this pattern:
- **Format:** UserID + DateOfBirth (no slashes)
- **Example:** 
  - User ID: TP30001
  - DOB: 01/01/2000
  - Password: TP3000101012000

## Troubleshooting

**Problem:** Application won't start
- **Solution:** Ensure JDK 17+ is installed and JAVA_HOME is set

**Problem:** Compilation errors
- **Solution:** Check that all 15 source files are present in correct packages

**Problem:** No data showing
- **Solution:** Delete the `data/` folder and restart - system will re-seed

**Problem:** Can't login
- **Solution:** Check that you're using correct default credentials listed above

**Problem:** Reports show "Insufficient data"
- **Solution:** Ensure at least 5 results exist for the selected module

## Support

For issues or questions:
1. Check README.md for detailed information
2. Verify all files are present in correct structure
3. Ensure JDK version compatibility (17+)

## Tips

- ✓ Always use the "Refresh" button after making changes
- ✓ Edit your profile to change password, email, or phone
- ✓ Admin can delete any user except themselves
- ✓ Reports require minimum 5 results to generate
- ✓ All changes are saved immediately to text files
- ✓ Backup the `data/` folder regularly

## Next Steps

1. **Customize the system:**
   - Modify grading scale in Admin dashboard
   - Add more classes and modules
   - Create user accounts for your institution

2. **Test the workflow:**
   - Admin creates users
   - Leader assigns lecturers to modules
   - Lecturer creates assessments
   - Lecturer grades students
   - Students view their results
   - Leader generates reports

3. **Regular maintenance:**
   - Backup data files
   - Monitor system performance
   - Update user information as needed

---
**Version:** 1.0
**Last Updated:** 2026
