Pacman-style Game Application

This project implements a game in the style of Pacman, featuring player-controlled characters navigating a maze while collecting points and avoiding enemies. The game includes various upgrades that appear randomly throughout gameplay, enhancing player abilities.

Key Features:

* Main menu with options for starting a new game, viewing high scores, and exiting.
* Customizable game board size ranging from 10x10 to 100x100 rows/columns.
* Graphical interface implemented using Java Swing components with cohesive design and graphics.
* Use of graphic files for visualizing game elements and animations for character movement.
* Display of score counter, time counter, life counter, and other necessary elements during gameplay.
* Multi-threaded implementation for handling time-related operations using the Thread class.
* Ability to interrupt the game and return to the main menu using the keyboard shortcut Ctrl+Shift+Q.
* High scores persistence using the Serializable interface, allowing saving and loading of player rankings.
* High scores displayed in a scrollable list for easy viewing.

Implementation Details:

* Follows the MVC (Model-View-Controller) programming pattern for clear separation of concerns.
* Complete event handling implemented using the delegated event handling model.
* Exception handling incorporated to display error messages to the user when necessary.
* Scalable application windows to ensure usability across different screen sizes.
* Dialogs utilized for certain windows instead of JFrame class for improved user experience.
