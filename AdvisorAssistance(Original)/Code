#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <vector>
#include <list>
#include <algorithm>

using namespace std;

//course info structure
struct Course {
    string courseId;
    string title;
    vector<string> prerequisites;

    Course() = default;
    Course(string id, string name, vector<string> prereqs) : courseId(id), title(name), prerequisites(prereqs) {}
};
//HashTable class for storing courses
class HashTable {
private:
    vector<list<Course>> table;
    int tableSize; //hash table size

    int hashFunction(string key) const {
        int hash = 0;
        for (char c : key) {
            hash = hash * 31 + c;
        }
        return hash % tableSize;
    }

public:
    HashTable(int size) : tableSize(size) {
        table.resize(size); //initialize table
    }
    //insert course into the hash table
    void insertCourse(const Course& course) {
        int index = hashFunction(course.courseId);
        table[index].push_back(course);
    }
    //search for course
    Course searchCourse(string courseId) const {
        int index = hashFunction(courseId);
        for (const Course& course : table[index]) {
            if (course.courseId == courseId) {
                return course;
            }
        }
        return Course();
    }
    //list of all courses
    vector<Course> getAllCourses() const {
        vector<Course> courses;
        for (const auto& bucket : table) {
            for (const Course& course : bucket) {
                courses.push_back(course);
            }
        }
        return courses;
    }
};
//load courses into hash table from csv file
void loadCourses(string filename, HashTable& courses) {
    ifstream file(filename);
    if (!file.is_open()) {
        cout << "Unable to open the file: " << filename << endl;
        return;
    }

    string line;
    while (getline(file, line)) {
        stringstream ss(line);
        string id, name, prereq;
        vector<string> prereqs;

        getline(ss, id, ','); //course ID
        getline(ss, name, ','); //course title

        while (getline(ss, prereq, ',')) {
            prereqs.push_back(prereq); //check prerequisites
        }

        courses.insertCourse(Course(id, name, prereqs)); //insert into hash table
    }

    file.close(); //close file
}

void printCourses(const HashTable& courses) {
    vector<Course> allCourses = courses.getAllCourses();
    sort(allCourses.begin(), allCourses.end(), [](const Course& a, const Course& b) {
        return a.courseId < b.courseId;
        });
    for (const Course& course : allCourses) {
        cout << course.courseId << ": " << course.title << endl;
    }
}
//print specific course information
void printCourseInfo(const HashTable& courses, const string& courseId) {
    Course course = courses.searchCourse(courseId);
    if (!course.courseId.empty()) {
        cout << "Course ID: " << course.courseId << endl;
        cout << "Course Title: " << course.title << endl;
        cout << "Prerequisites: ";
        for (const string& prereq : course.prerequisites) {
            cout << prereq << " ";
        }
        cout << endl;
    }
    else {
        cout << "Course not found." << endl;
    }
}

int main() {
    HashTable courses(50);
    int choice = 0;
    string filename = "C:/Users/itsbo/Downloads/CS 300 ABCU_Advising_Program_Input.csv";
    //had to use a direct filepath to open the file

    while (choice != 9) { //menu options
        cout << "Menu:" << endl;
        cout << "1. Load Courses" << endl;
        cout << "2. Print Course List" << endl;
        cout << "3. Print Course Information" << endl;
        cout << "9. Exit" << endl;
        cout << "Which choice would you like to make? ";
        cin >> choice;

        switch (choice) {
        case 1: { //load courses from file
            loadCourses(filename, courses);
            break;
        }
        case 2: { //print list of courses
            printCourses(courses);
            break;
        }
        case 3: { //prompt for courseId and print info for specified course
            string courseId;
            cout << "\nWhich course would you like to know more about? \nEnter course ID: ";
            cin >> courseId;
            printCourseInfo(courses, courseId);
            break;
        }
        case 9: //exit
            cout << "Thank you for checking out the course planner!" << endl;
            break;
        default: //any other input is invalid
            cout << "Choice was invalid." << endl;
        }
    }

    return 0;
}
