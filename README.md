Original App Design Project - README Template
===

# JoyFlick

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
JoyFlick is an application that will allow users to view and post video game reviews. Users will be able to search for games and see other user reviews for each game. Users will have profiles where all of their reviews will be visible, allowing other users to see which games a specific user likes or dislikes.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Social
- **Mobile:** This application is primarily developed for mobile devices.
- **Story:** JoyFlick is a place to see and post game reviews. Users can determine if a game is worth their time based on these reviews.
- **Market:** The target market for JoyFlick is for casual and hardcore gamers alike. People who want to share their gaming opinions can post reviews, while people who are just curious about if a certain game is worthwhile can look at other reviews.
- **Habit:** JoyFlick would be used every time a new game releases. Usage would vary based on when games release.
- **Scope:** The starting functionality would consist of posting and viewing game reviews, searching for games and users, and viewing user profiles. Expanded functionality could consist of following users and expanding the social aspect, and verifying users with the option to only view verified reviews.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

- [ ] Users can log in, sign up, and log out of the app
- [ ] Users can add a profile picture
- [ ] Users can add a game review post
- [ ] Users can search for games or other users
- [ ] Users can view a detailed page about a game and see user reviews
- [ ] Users can view a feed of recent reviewed games
- [ ] Users can view user profiles with that user's game reviews
- [ ] Users can view and post comments on game reviews

**Optional Nice-to-have Stories**

- [ ] Users can update their credentials in profile settings
- [ ] Users can enable a mode to only view reviews from verified users
- [ ] Users can follow other users

### 2. Screen Archetypes

* Login
   * Users can log in, sign up, and log out of the app
* Signup
   * Users can log in, sign up, and log out of the app
* Main
   * Users can view a feed of recent reviewed games
* Search
   * Users can search for games or other users
* Profile
   * Users can view user profiles with that user's game reviews
   * Users can add a profile picture
* Game
   * Users can view a detailed page about a game and see user reviews
* Post Review
   * Users can add a game review post
* Review Detail
   * Users can view a detailed page about a game and see user reviews
   * Users can view and post comments on game reviews
* Comment
   * Users can view and post comments on game reviews
* Game Selection
   * Users can search for games or other users

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home to Main
* Add to Post Review
* User to Profile

**Flow Navigation** (Screen to Screen)

* Login
   * Main
   * Signup
* Signup
   * Main
   * Login
* Main
   * Search
   * Game
* Search
   * Game
   * Profile
* Profile
   * Review Detail
* Game
   * Post Review
   * Review Detail
* Post Review
   * Game Selection
   * Game
* Review Detail
   * Game
   * Profile
   * Comment
* Comment
   * Review Detail
* Game Selection
   * Post Review

## Wireframes
<img src="https://i.imgur.com/jdOB1HD.png" width=600>

### [BONUS] Digital Wireframes & Mockups
<img src="https://i.imgur.com/2H36I7g.png" width=600>

### [BONUS] Interactive Prototype
<img src="https://i.imgur.com/0c4RxYt.gif" width=349>

## Schema 
[This section will be completed in Unit 9]
### Models
#### Post
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | postId        | Number   | unique id for the user post (default field) |
   | user          | Pointer to User | post user |
   | gameId        | String     | game that the user is posting about |
   | review        | String   | user's review about the game |
   | createdAt     | DateTime | date when post is created (default field) |
#### Comments
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | commentId     | Number   | unique id for the comment (default field) |
   | postId        | String   | id of the post the user is commenting on |
   | user          | Pointer to User   | comment user |
   | comment       | String   | user's comment on a post |
   | createdAt     | DateTime | date when comment is created (default field) |
#### User
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | userId        | Number   | unique id for the user (default field) |
   | username      | String   | name associated with user |
   | password      | String   | user's password |
   | profilePicture | Image   | user's profile picture |
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
