# Revolve: Personal Academic Workload Tracker

## Software Requirements Specification

**Version:** 0.1  
**Date:** April 16, 2025  
**Developer:** Jai Dutta

---

## 1. Introduction

### 1.1 Project Overview

Revolve is a minimalist web application I am building to track my academic commitments and help stay on top of coursework. The app features a full-screen calendar with cards representing different academic activities that can be flipped when completed.

### 1.2 Project Description

Revolve is a simplistic, minimal web app that tracks academic calendars and helps students keep on top of incomplete work. It features a full-screen-width calendar with cards representing lectures, tutorials, workshops, and assignment work. As a user completes an item of work, they log in and flip the card, which then grays out for that week. Unflipped cards at week's end automatically move to a backlog section. The backlog displays the total time value of contained work items. Users can also add assignments with time allocations (e.g., 2 hours per week for a specific course assignment). The calender is setup once and then "revolves", e.g. only one week is ever displayed, with the same items on it each week. This aims to simply aid students in tracking what they have and have not completed each week.

---

## 2. Problem Statement

Students can struggle to effectively manage their academic workload across multiple courses, particularly when balancing various commitments like lectures, tutorials, workshops, and assignments. Without a dedicated system to track completion and prioritise unfinished work, they can:

1. Lose track of which course activities I've completed in a given week
2. Fail to recognise accumulated backlog until it becomes overwhelming
3. Have difficulty visualising my total time commitments across all courses
4. Lack clear indicators of which missed activities should be prioritised for catch-up

Revolve aims to solve these challenges by providing a simple, visual academic calendar system that helps track weekly academic commitments, clearly shows what remains unfinished, and manages accumulated backlog with time-based metrics to guide prioritization decisions.

---

## 3. Features & Requirements

### 3.1 Core Functionality

- [ ] Weekly calendar view
- [ ] Activity cards (lectures, tutorials, workshops, assignments)
- [ ] Card "flipping" mechanism to mark completion
- [ ] Automatic backlog generation for unfinished items
- [ ] Time tracking for work items

### 3.2 User Interface

- [ ] Minimalist, clean design
- [ ] Full-width calendar display
- [ ] Visual differentiation between completed/incomplete items
- [ ] Backlog section with time metrics

### 3.3 Technical Requirements

- [ ] Web-based application
- [ ] Basic user authentication
- [ ] Data persistence for user activities
- [ ] Responsive design for multiple devices

---

## 4. Development Plan

### 4.1 Technology Stack

- Frontend: React
- Backend: Java Spring Boot
- Database: PostgreSQL
- Cloud deployment: AWS
- CI/CD: AWS CodePipeline + CodeBuild + CodeDeploy

### 4.2 Development Phases

1. Design UI mockups and user flow
2. Implement core calendar functionality
3. Add activity card system
4. Develop backlog tracking
5. Implement user authentication
6. Testing and refinement

### 4.3 Timeline

#### Phase 1 - Frontend Development

- **Week 1** | 14th April 
	- Project commences.
	- SRS outline written. 
- **Week 2** | 21nd April
	- Setup React project structure.
	- Design UI mockup
	- Implement basic UI components (nav bar, layout)
- **Week 3** | 28th April
	- Iterate UI mockup
	- Create form for adding cards
	- Implement calendar components
	- Implement card components
- **Week 4** | 5th May
	- Implement backlog components
	- Testing with mock data calls
#### Phase 2 - Backend Development
- **Week 5** | 12th May
	- 

---

## 5. Additional Notes

- Personal project with possible collaboration from select friends
- Non-commercial use
- Focus on simplicity and usability over complex features

---

### 6. Future Enhancements:

