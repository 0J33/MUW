# MUW
CSEN401 Game Project (Marvel - Ultimate War)

Gameplay:
	After both players enter their name, each player gets to choose 3 champions and set one of them as a leader (You cannot choose a champion selected by another player) then you can start the game.
	The objective of the game is to knockout all the other player’s champions. The game is played on a 5x5 board. There are 5 covers placed randomly throughout the board (if a cover is between you and another champion you cannot attack that champion until you destroy the cover). You cannot move to a cell that contains another player or a cover. You can move or do normal attack in only 4 directions (UP, DOWN, RIGHT, LEFT). In the beginning of each round all champions are ordered based on speed (fastest champion starts).

Champion attributes:
	•	Type: (Hero/Villain/AntiHero).
	•	HP: Health Points
	•	Mana: Used for abilities
	•	AP: Action points (Used for moving/Doing a normal attack/Casting abilities)
	•	Speed: The faster the champion the earlier they start their turn.
	•	AR: Attack Range (Champions can only attack within their attack range [range is calculated using Manhattan distance]).

Possible actions per turn:
	•	Move to an empty cell (costs 1 action point).
	•	Do a normal attack (costs 2 action points).
	•	Cast an ability (action point cost/mana cost depend on the ability).
	•	Use Leader Ability (only if champion is the player’s chosen leader).

Attacking:
	•	Heroes: they deal extra damage when attacking villains.
	•	Villains: they deal extra damage when attacking heroes.
	•	Anti-Heroes: when being attacked or attacking a hero or villain, the antihero will always act as the opposite type. If attacking an antihero, damage is calculated normally.

Leader Abilities:
	•	Leaders can use their ability once during the entire game.

Each champion has 3 abilities which they can use throughout the game. Abilities can be damaging, healing, or crowd control (applies an effect). Abilities have different action point cost/mana cost/cool down/cast range. Abilities also have different areas of effect (SINGLE TARGET, TEAM TARGET, SELF TARGET, SURROUND, DIRECTIONAL):
	•	SINGLE TARGET: Applies to only one target as long as they are within cast range.
	•	TEAM TARGET: Applies to the entire team (Current champion’s team if it’s healing or a BUFF effect / Opponent’s team if it’s damaging or a DEBUFF effect).
	•	SELF TARGET: Applies to champion who is casting the ability.
	•	SURROUND: Applies to adjacent cells (No friendly fire).
	•	DIRECTIONAL: Applies to cells in chosen direction as long as they are in cast range (No friendly fire).

Effects:

![image](https://user-images.githubusercontent.com/90639992/192110784-af56baff-d5b8-426b-a9c1-a35b05ad84c5.png)
