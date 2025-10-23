Document your refactoring choices here. Delete this file if you choose to use a PDF or txt file format instead.
•	Eagle:
    •	In Eagle class constructor method, a useless if-else code block was removed, as both the if condition and else condition were doing the same thing with duplicate code. This duplicate code was made into a new method in Eagle called setNewDirection, and this method was simply called in the constructor rather than in an if-else loop.
    •	In tick() method, parameters game.getPlayer().getX() and game.getPlayer().getY() were defined as variables playerX and player to increase clarity of the distanceFrom() method, and also allow for easier changes to be made if getPlayer() or getX/Y were changed in the future.
    •	Created new method setDirectionAndSpriteTowards() to simplify tick() method. Essentially the same logic was being called from two different conditions with two different variables, so it was simplified with a new method and passing in different parameters for both conditions.
    •	Changed spawnX and spawnY to final variables that aren’t initially assigned to zero (redundant as they’re assigned to x and y in the constructor anyways).
    •	Added inline comments and javadocs for all methods and class
    •	Removed getArt() method as it isn’t used anywhere
•	Enemy:
•	    Tick() method was removed as it just calls super.tick and therefore there’s no need to override. Empty overrides can create confusion about intent of methods.
•	EnemyManager:
    •	Removed if-loop that was unnecessarily checking what type of bird each instance of bird was, while executing the same code no matter the type. If-loop was changed to just one line.
    •	mkP, mkM, mkE methods were renamed to makePigeon, makeMagpie, and makeEagle for increased readability
    •	Error in makePigeon method, spawnX parameter was entered twice when one of them should’ve been spawnY
    •	Defining eagle variable in makeEagle is redundant, rewritten in one line of code
•	TargetPlayerHelper:
    •	New helper class that was created to help with targeting logic in eagle and magpie and remove large amounts of repeated code.
    •	Eagle and Magpie both utilise this helper class
•	Pigeon:
    •	Created new private helper methods, returnToSpawn(), updateReturnedSprite (), getTrackedTarget(), findTilesWithCabbage(), and attackCabbage() to refactor tick method and make it shorter
    •	Added setAttacking method
•	Magpie:
•	    Made attacking variable private, added setAttacking() method so that scarecrow can access it
•	Spawner interface:
•	    As it extends tickable and hasPosition, redeclaring all methods in unnecessary. Methods were removed and only the timer was kept in the interface.
•	Beehive spawner:
    •	Duration parameter is not used anywhere in constructor as the timer was hardcoded, timer in constructor now takes duration as length of timer
    •	Npc adding was incorrectly using npc.add rather than the addNPC method that was created. It was done properly in ScarecrowSpawner but not BeehiveSpawner
•	Eagle spawner:
    •	Tick method violated encapsulation by directly manipulating EnemyManager’s internal state. Fixed by adding an eagleSpawner() method to EnemyManager and calling this in EagleSpawner
    •	Private variables made private
•	BeeHive:
•	    Npcs.add was changed to addNpcs so as to not violate encapsulation
•	Scarecrow:
    •	Pigeon and Magpie were setting their attack status differently in scarecrow interact() method. Magpie was properly using the setAttack() method while Pigeon’s attack status was directly being modified. Fixed by creating setAttack() method in Pigeon class and utilising it in Scarecrow
    •	Removed tick() method as it overrided just to call super, not needed
•	NPC:
    •	Tick method was repeated with different parameters due to inheritance. Fixed by making one tick method call the other
    •	Speed is meant to be a double, but setSpeed was taking in an int value for speed. Fixed to make setSpeed take in a double as a parameter
    •	DistanceFrom methods were doing basically the same thing, made one delegate to the other
•	NPC manager:
    •	Interactable check was replaced with null check, as npcs already implement interactable
    •	Npcs arraylist was made private and added a getter method for it for use in other classes
•	Directable/Expirable
•	    Removed redundant public in front of method names
•	GuardBee:
•	    Added numerous private constant variables regarding targeting
•	    Parameters xCoordinate and yCoordinate were renamed to x and y for consistency
•	Beanworld
•	    TileSelector was made into a stream
•	OverlayBuilder
•	    Shortened spawnToString methods as they repeated code, added a helper method and used it in each case (pigeon, eagle, magpie)
•	    In extractPlayerDetailsFromLine, comment incorrectly said to check if line was 3 instead of 4
•	SpawnerDetails
•	    Repeating all methods is redundant as the interface extends HasPosition, removed all methods except for getDuration()
•	Worldbuilder
•	    Constructor was made private and now throws AssertionError if attempting to instantiate it
•	JavaBeanWorld:
•	    Reduced length of constructor method using various helper methods
•	    readAllReader() method wasn’t closing file after reading it, implemented try()


•	Javadocs were added for all classes and methods that did not already have them.
•	Throughout the project, numerous variables were named xCoordinate or yCoordinate or something similar (first letter lower case as it signifies an axis). Though this formatting is clearer to read, CheckStyle required me to change them to xcoordinate and ycoordinate. 



