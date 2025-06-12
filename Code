/*
============================================================
 Advisor Assistance Program
 -----------------------------------------------------------
 Branden Boehnke

 The Advisor Assistance Program is designed to assist advisors with planning student's program courses.
  
 The program includes the following abilities:
   
    - Access to Course Catalog
    - Sorting Courses Topologically, by Department, or by Course Level
    - Schedule Creation (with prerequisite restrictions)
    - Course Recommendations (based on prerequisites)

 How the program works:
    - Program reads and parses the course data from the CS 300 ABCU_Advising_Program_Input.csv file
    - Stores courses in a hash table
    - Uses a dependency graph for topological sorting and prerequisites
    - Includes a menu system for selecting, viewing, or sorting courses
    - Tracks course schedule and displays recommendations based on prerequisites.
============================================================
*/

#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <vector>
#include <unordered_map>
#include <queue>
#include <algorithm>

using namespace std;

// Structure for each course and their prerequites
struct Course {
    string courseId;
    string title;
    vector<string> prerequisites;
    Course() = default;
    Course(string id, string name, vector<string> prereqs) : courseId(id), title(name), prerequisites(prereqs) {}
};

// HashTable class for course information
class HashTable {
private:
    unordered_map<string, Course> table;
public:
    void insertCourse(const Course& course) { table[course.courseId] = course; }
    Course searchCourse(const string& courseId) const {
        auto it = table.find(courseId);
        return it != table.end() ? it->second : Course();
    }
    vector<Course> getAllCourses() const {
        vector<Course> courses;
        for (const auto& pair : table) courses.push_back(pair.second);
        return courses;
    }
    bool contains(const string& courseId) const {
        return table.find(courseId) != table.end();
    }
};

// Graph class (directed graph) for handling prerequisites
class CourseGraph {
private:
    unordered_map<string, vector<string>> adjList;
    unordered_map<string, int> inDegree;
public:
    void addEdge(const string& prereq, const string& course) {
        adjList[prereq].push_back(course);
        inDegree[course]++;
        if (inDegree.find(prereq) == inDegree.end()) inDegree[prereq] = 0;
    }

    // Topological sorting to determine the order of courses
    vector<string> topologicalSort() const {
        queue<string> q;
        unordered_map<string, int> degree = inDegree;
        for (const auto& pair : degree)
            if (pair.second == 0) q.push(pair.first);

        vector<string> sorted;
        while (!q.empty()) {
            string curr = q.front(); q.pop();
            sorted.push_back(curr);
            if (adjList.find(curr) != adjList.end()) {
                for (const string& neighbor : adjList.at(curr))
                    if (--degree[neighbor] == 0) q.push(neighbor);
            }
        }
        return sorted;
    }
};

// Check if prerequisites are met
bool prerequisitesMet(const Course& course, const vector<string>& schedule) {
    for (const string& prereq : course.prerequisites)
        if (find(schedule.begin(), schedule.end(), prereq) == schedule.end())
            return false;
    return true;
}

// Display recommended courses based on current schedule
void showRecommended(const HashTable& courses, const vector<string>& schedule) {
    cout << "\n----------------------------------------------\n";
    cout << "\nRecommended Courses:\n";
    for (const Course& c : courses.getAllCourses())
        if (prerequisitesMet(c, schedule) && find(schedule.begin(), schedule.end(), c.courseId) == schedule.end())
            cout << "- " << c.courseId << ": " << c.title << endl;
    cout << "\n----------------------------------------------\n";
}

// Display schedule
void showSchedule(HashTable& courses, vector<string>& schedule, CourseGraph& graph);

// Compare the selected course to the current schedule
void printCourseAndCompare(const Course& course, vector<string>& schedule, HashTable& courses, CourseGraph& graph) {
    cout << "\n----------------------------------------------\n";
    cout << "\nSelected Course:\n";
    cout << "Course ID: " << course.courseId << "\nTitle: " << course.title << "\nPrerequisites: ";
    for (const string& pre : course.prerequisites) cout << pre << " ";
    cout << "\n";
    cout << "\n----------------------------------------------\n";
    showSchedule(courses, schedule, graph);
}

// Handle course selection and choices of adding to schedule or comparing to schedule
void handleCourseSelection(HashTable& courses, vector<string>& schedule, CourseGraph& graph) {
    string courseId;
    cout << "\n----------------------------------------------\n";
    cout << "\nEnter course ID: ";
    cin.ignore(); getline(cin, courseId);
    if (!courses.contains(courseId)) {
        cout << "Course not found.\n";
        return;
    }
    Course course = courses.searchCourse(courseId);
    cout << "\nCourse ID: " << course.courseId << "\nTitle: " << course.title << "\nPrerequisites: ";
    for (const string& pre : course.prerequisites) cout << pre << " ";
    cout << "\n";

    int actionChoice;
    cout << "\nWhat would you like to do?\n";
    cout << "1. Add Course to Schedule\n";
    cout << "2. Compare to Schedule\n";
    cout << "Choice: ";
    cin >> actionChoice;

    if (actionChoice == 1) {
        if (prerequisitesMet(course, schedule)) {
            if (find(schedule.begin(), schedule.end(), course.courseId) == schedule.end()) {
                schedule.push_back(course.courseId);
                cout << "Course added to schedule.\n";
            }
            else {
                cout << "Course already in schedule.\n";
            }
        }
        else {
            cout << "You do not meet the prerequisites for this course.\n";
            showRecommended(courses, schedule);
        }
    }
    else if (actionChoice == 2) {
        printCourseAndCompare(course, schedule, courses, graph);
    }
    else {
        cout << "Invalid option.\n";
    }
}

// Display a list of all courses
void displayCourses(const vector<Course>& list) {
    for (const Course& c : list)
        cout << c.courseId << ": " << c.title << endl;
}

// Display course catalog (Topological Order)
void displayTopological(const HashTable& courses, const CourseGraph& graph) {
    vector<string> sorted = graph.topologicalSort();
    cout << "\n----------------------------------------------\n";
    cout << "\nCourse Catalog\n";
    cout << "\nCourses (Topological Order):\n";
    for (const string& id : sorted)
        if (courses.contains(id)) {
            Course c = courses.searchCourse(id);
            cout << c.courseId << ": " << c.title << endl;
        }
    cout << "\n----------------------------------------------\n";
}

// Course Catalog menu for course selection, sorting and returning to main menu
void catalogMenu(HashTable& courses, CourseGraph& graph, vector<string>& schedule) {
    displayTopological(courses, graph);
    showRecommended(courses, schedule);
    int subOption = -1;
    while (subOption != 0) {
        cout << "\nCourse Catalog Options:\n";
        cout << "1. Select a Course\n";
        cout << "2. Sort\n";
        cout << "0. Return to Main Menu\n";
        cout << "Choice: ";
        cin >> subOption;

        switch (subOption) {
        case 1: handleCourseSelection(courses, schedule, graph); break;
        case 2: {
            int sortType = -1;
            cout << "\nSort Options:\n";
            cout << "1. Sort Topologically\n";
            cout << "2. Sort by Department\n";
            cout << "3. Sort by Course Level\n";
            cout << "Choice: ";
            cin >> sortType;
            auto list = courses.getAllCourses();
            if (sortType == 1) displayTopological(courses, graph);
            else if (sortType == 2) {
                sort(list.begin(), list.end(), [](const Course& a, const Course& b) {
                    return a.courseId.substr(0, 4) < b.courseId.substr(0, 4); });
                cout << "\nCourses by Department:\n"; displayCourses(list);
            }
            else if (sortType == 3) {
                sort(list.begin(), list.end(), [](const Course& a, const Course& b) {
                    return a.courseId.substr(4) < b.courseId.substr(4); });
                cout << "\nCourses by Level:\n"; displayCourses(list);
            }
            else cout << "Invalid sort option.\n";
            break;
        }
        case 0: break;
        default: cout << "Invalid input.\n";
        }
    }
}

// Show the user's current course schedule
void showSchedule(HashTable& courses, vector<string>& schedule, CourseGraph& graph) {
    if (schedule.empty()) {
        cout << "\nYour schedule is currently empty.\n";
        cout << "\nWould you like to view the Course Catalog?\n";
        cout << "1. Course Catalog\n";
        cout << "2. Return to Main Menu\n";
        int decision;
        cin >> decision;
        if (decision == 1) catalogMenu(courses, graph, schedule);
        return;
    }

    cout << "\n----------------------------------------------\n";
    cout << "\nYour Current Schedule:\n";
    for (const string& id : schedule) {
        Course c = courses.searchCourse(id);
        cout << c.courseId << ": " << c.title << endl;
    }
    showRecommended(courses, schedule);
}

// Load course info from the CSV file
void loadCourses(const string& filename, HashTable& courses, CourseGraph& graph) {
    ifstream file(filename);
    if (!file.is_open()) {
        cout << "File open error.\n";
        return;
    }
    string line;
    while (getline(file, line)) {
        stringstream ss(line);
        string id, name, prereq; vector<string> prereqs;
        getline(ss, id, ','); getline(ss, name, ',');
        while (getline(ss, prereq, ',')) if (!prereq.empty()) prereqs.push_back(prereq);
        Course c(id, name, prereqs); courses.insertCourse(c);
        for (const string& pre : prereqs) {
            graph.addEdge(pre, id);
            if (!courses.contains(pre))
                courses.insertCourse(Course(pre, "[Placeholder - Missing Course]", {}));
        }
    }
    cout << "\n----------------------------------------------\n";
    cout << "\nGreat! Let's begin course planning.\nWhat would you like to do?\n";
    cout << "\n----------------------------------------------\n";
}

// Main function
int main() {
    HashTable courses;
    CourseGraph graph;
    vector<string> schedule;
    string filename = "resources/CS 300 ABCU_Advising_Program_Input.csv";

    cout << "\n----------------------------------------------\n";
    cout << "Welcome to Advisor Assistance!\n";
    cout << "\nWould you like to begin course planning?\n";
    cout << "1. Begin\n";
    cout << "2. Exit\n";
    cout << "Choice: ";
    int startChoice;
    cin >> startChoice;
    if (startChoice != 1) {
        cout << "Thank you for using Advisor Assistance. Goodbye!\n";
        return 0;
    }

    loadCourses(filename, courses, graph);

    int choice;
    while (true) {
        cout << "\nMain Menu:\n";
        cout << "1. Course Catalog\n";
        cout << "2. Schedule\n";
        cout << "9. Exit\n";
        cout << "Choice: ";
        cin >> choice;

        if (cin.fail()) {
            cin.clear(); cin.ignore(numeric_limits<streamsize>::max(), '\n');
            cout << "Invalid input.\n";
            continue;
        }

        switch (choice) {
        case 1: catalogMenu(courses, graph, schedule); break;
        case 2: showSchedule(courses, schedule, graph); break;
        case 9: cout << "Thank you for using Advisor Assistance. Goodbye!\n"; return 0;
        default: cout << "Invalid choice.\n";
        }
    }
}
