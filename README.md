# Assignment 9

Add a multiple undo/redo functionality for your Java application. Implement this functionality using inheritance and polymorphism, an example is illustrated in the attached figure. If you would like to read more about this, check out the _Command_ design pattern. The performed operations (actions) can be memorized in undo/redo stacks. 

Implement undo/redo for _cascade operations_ (remove/update). E.g. for the cars problem: when a car is removed, all related reservations are also removed. When this operation is undone (unsing just one undo), both the car and related reservations are added. When the operation is redone (using just one redo), both the car and related reservations are removed. 
