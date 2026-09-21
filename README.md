AnarchyDupes is a Paper/Spigot plugin for Minecraft anarchy servers. It implements configurable item duplication mechanisms with real-time percentage adjustments and an admin management interface.

## Features

- 7 Duplication Mechanics:
  - Item Frame: Triggered by left-clicking (punching) an item out of a frame.
  - Cactus: Triggered by player-dropped items landing on a cactus block.
  - Dispenser: Triggered when dispensing against a solid block.
  - Piston Shulker: Triggered when pushing a piston into a Shulker Box.
  - Donkey: Triggered when removing a saddle from a chested donkey.
  - Explosion: Triggered when exploding container blocks (Chest, Barrel, Shulker Box).
  - Portal: Triggered when pushing Chest Minecarts or Chest Boats through a portal.
- Player Help Command: `/dupes` displays active duplication methods and step-by-step guides.
- Admin Configuration GUI: `/ad config` allows toggling dupes and modifying success probabilities in real time.
- Config Persistence: All GUI changes automatically save to `config.yml`.

## Commands and Permissions

| Command | Aliases | Description | Permission | Default |
|---|---|---|---|---|
| `/dupes` | `/dupe` | Shows active duplication mechanics and guides | None | Everyone |
| `/anarchydupes config` | `/ad config` | Opens the GUI to toggle dupes and modify rates | `anarchydupes.admin` | OP |
