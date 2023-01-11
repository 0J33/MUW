# Marvel - Ultimate War

CSEN401 Game Project

# Gameplay:

    After both players enter their name, each player gets to choose 3 champions and set one of them as a leader (You cannot choose a champion selected by another player) then you can start the game.

    The objective of the game is to knockout all the other player’s champions. The game is played on a 5x5 board.
	There are 5 covers placed randomly throughout the board (if a cover is between you and another champion you cannot attack that champion until you destroy the cover).
	You cannot move to a cell that contains another player or a cover.
	You can move or do normal attack in only 4 directions (UP, DOWN, RIGHT, LEFT).
	In the beginning of each round all champions are ordered based on speed (fastest champion starts).

# Champion attributes:

    • Type: (Hero/Villain/AntiHero).
	• HP: Health Points
	• Mana: Used for abilities
	• AP: Action points (Used for moving/Doing a normal attack/Casting abilities)
	• Speed: The faster the champion the earlier they start their turn.
	• AR: Attack Range (Champions can only attack within their attack range [range is calculated using Manhattan distance]).

# Possible actions per turn:

    • Move to an empty cell (costs 1 action point).
	• Do a normal attack (costs 2 action points).
	• Cast an ability (action point cost/mana cost depend on the ability).
	• Use Leader Ability (only if champion is the player’s chosen leader).

# Attacking:

    • Heroes: they deal extra damage when attacking villains.
	• Villains: they deal extra damage when attacking heroes.
	• Anti-Heroes: when being attacked or attacking a hero or villain, the antihero will always act as the opposite type. If attacking an antihero, damage is calculated normally.

# Leader Abilities:

    • Leaders can use their ability once during the entire game.

# Abilities:

    Each champion has 3 abilities which they can use throughout the game.
	Abilities can be damaging, healing, or crowd control (applies an effect).
	Abilities have different action point cost/mana cost/cool down/cast range.
	Abilities also have different areas of effect (SINGLE TARGET, TEAM TARGET, SELF TARGET, SURROUND, DIRECTIONAL):

    • SINGLE TARGET: Applies to only one target as long as they are within cast range.
	• TEAM TARGET: Applies to the entire team (Current champion’s team if it’s healing or a BUFF effect / Opponent’s team if it’s damaging or a DEBUFF effect).
	• SELF TARGET: Applies to champion who is casting the ability.
	• SURROUND: Applies to adjacent cells (No friendly fire).
	• DIRECTIONAL: Applies to cells in chosen direction as long as they are in cast range (No friendly fire).

# Effects:

    • Disarm: Target cannot use normal attacks. Target gains a SINGLETARGET damaging ability called "Punch", Mana Cost: 0, Damage: 50, Cooldown: 1, Range: 1, AP Cost: 1.
	• Dodge: Target has a 50% chance of dodging normal attacks. Increase speed by 5%.
	• Embrace: Heals target by 20%.
	• PowerUp: Increase Damage and Heal amounts of all damaging and healing abilities of the target by 20%.
	• Root: Target cannot move.
	• Shield: Block the next attack or damaging ability cast on target. Increase speed by 2%.
	• Shock: Decrease target speed and normal attack damage by 10%. Decrease target's AP by 1.
	• Silence: Target cannot use abilities. Increase target's AP by 2.
	• SpeedUp: Increase speed by 15%.
	• Stun: Target is not allowed to play their turn.

# Screenshots:

`<img width="960" alt="img1" src="https://user-images.githubusercontent.com/90639992/200673253-8a32bd29-272b-49a6-9445-cb3ecc9b575d.png">`
`<img width="960" alt="img2" src="https://user-images.githubusercontent.com/90639992/200673267-c51d18b2-9608-4afb-88f3-4cc055fd8478.png">`
`<img width="960" alt="img3" src="https://user-images.githubusercontent.com/90639992/200673272-a4cc7e6c-cbf6-4b77-be12-4a281e938a93.png">`
