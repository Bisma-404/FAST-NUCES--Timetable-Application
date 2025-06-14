# FAST-NUCES--Timetable-Application

A comprehensive, Java Swing-based Timetable Management System designed specifically for **FAST NUCES**. This application provides role-based access and real-time timetable scheduling for students, teachers, and administrators.

## Overview

This desktop application simplifies academic scheduling and resolves classroom/teacher conflicts through a clean, intuitive interface. It uses object-oriented design, modular components, and persistent data storage—making it both practical and extensible.

## Key Features

### 1. Role-Based Access  
- **Students**: View personalized timetables by entering roll number, department, and section.  
- **Teachers**: Access consolidated schedules with assigned courses, timeslots, and rooms.  
- **Administrators**: Full control over timetable creation, updates, and deletions with password protection.

### 2. Dynamic Timetable Management  
- **Batch/Department/Section-wise Scheduling**: Automatically generates timetables for student groups.  
- **Teacher-Course Assignment**: Tracks which teacher is assigned to which course (with conflict prevention).  
- **Room Allocation**: Smart room booking system to avoid double-booking conflicts.

### 3. Conflict Prevention  
- **Room Availability Checks**: Ensures no two classes are scheduled in the same room simultaneously.  
- **Course Assignment Tracking**: Prevents duplicate course assignments to multiple teachers.

### 4. Data Persistence  
- **File-Based Storage**: Schedules are saved to `.txt` files for student/teacher timetables.  
- **Binary Data Tracking**: Uses `.dat` files to persistently track room allocations and course assignments.

### 5. User-Friendly GUI  
- **Sleek Interface**: Gradients, responsive buttons, and intuitive navigation.  
- **Real-Time Updates**: Teachers/students see changes immediately after admin modifications.  
- **Error Handling**: Validates inputs (e.g., roll number format: `24k-1234`).

### 6. Admin Superpowers  
- **CRUD Operations**: Create, Read, Update, or Delete timetables for any student/teacher.  
- **Bulk Assignments**: Assign multiple courses to teachers in one session.  
- **Overwrite Protection**: Confirmation prompts prevent accidental data loss.

### 7. Consolidated Views  
- Teachers see a **unified timetable** combining all their assigned courses across batches/sections.  
- Admins can **search/filter** timetables by batch, department, or teacher name.
- 

## Why It Stands Out

- ✅ **Scalable**: Handles large student/teacher datasets without a database  
- ✅ **Zero Dependencies**: Built in pure Java — no third-party libraries required  
- ✅ **Institutional Fit**: Tailored for FAST NUCES’s academic hierarchy and structure


## System Architecture
### Main Components
- `Student`, `Teacher`, and `Administrator` roles with separate login and views
- `Timetable`: Manages weekly schedule data
- `CourseAssignmentTracker`: Tracks teacher-course assignments
- `RoomAllocationManager`: Manages and validates room availability

### Design Patterns
- **Singleton Pattern**: For main application frame
- **Observer Pattern**: For real-time UI updates
- **MVC-Like Separation**: Keeps GUI and logic modular and organized

### Data Handling
- `.txt` files for human-readable timetables
- `.dat` serialized files for object storage (rooms, assignments)
- JSON-like structures for section/department management


## Usage
### Student Flow
1. Launch app → Select **Student**
2. Enter roll number (e.g., `24k-1234`)
3. Choose department and section
4. View your personalized weekly timetable

### Teacher Flow
1. Launch app → Select **Teacher**
2. Enter your name
3. View your complete teaching schedule across batches

### Admin Flow
1. Launch app → Select **Administrator**
2. Enter password (`admin123`)
3. Access dashboard to:
   - Create/update student timetables
   - Assign courses to teachers
   - Allocate rooms and detect conflicts


## Requirements

- Java 8 or higher
- No external libraries required
- Compatible with Windows, macOS, and Linux (GUI tested on Windows)


## Future Improvements

- Migrate to a relational database (e.g., MySQL or SQLite) for large-scale use
- Add export options (PDF/CSV) for timetables
- Modularize GUI code further for readability
- Unit testing for business logic and file handling
- Configurable paths for file storage


## License

This project is for academic and institutional use. Please credit the developers if reused or modified for deployment.


## Developed By
BISMA SHAHID  
Department of Software Engineering  
FAST NUCES KHI

