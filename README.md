[![NPC Police Spigot Link](http://www.livecar.net/random/NPCPolice_logo.png)](https://www.spigotmc.org/resources/npc-police-being-re-wrote.9553/) 

## About the plugin:
  NPC Police provides servers a system for implementing a judical system. It provides a structured jail system, NPC based police force, incarceration system, bounties, and general mayham deterrent.

## Features
- Base settings configurable at many levels.
  - Server (Config.yml), World, Regions, Jail, and per NPC
- Customizable Bounties
  - based on damage delt, murders of NPCs / Players, PVP, Escapes
  - Add or subtract bounties based on status, (Wanted, In Jail, Escaped, in cell at night / day)
- Define your own bounty to time served
  - Global, per World, or per jail!
- Customizable message system
  - Limit alerts / notices based on distance to the activity, or jail
  - Set the delay or time it will take to reach each player based on distance (News can travel fast, or slow, you decide)
- Define what NPCs are protected globally, Regions, or per NPC
- Customizable commands for many events
  - Globally or per World:
    - NPC Warnings, Alerting Guards, Alerting (No guards in range), NPC Murders, Players becoming (Wanted, Arrested, Escaped, Released)
  - Per Jail
    - Arrested, Escaped, Released
- Each jail can be customized
  - Jails name
  - Multiple cell locations
  - Bounty per second spent
    - Out of a cell during the day or night
    - In the jail
  - Bounty per PVP damage in jail
- This system also extends some of your other plugins feature sets
  - Worldguard (More flags available to support customization of your worlds)
    - Set the bountys for PVP, NPC Damage, NPC Murders, Time spent Escaped or Wanted
    - Set NPC defaults (Does not override settings on an NPC directly, just at the server or world level)
  - PlaceholderAPI
    - Offers several variables to display elsewhere.  
      - Users: Bounty, TotalBounty. Current Status, Prior Status, Current JailName, Last assault/Murder/Arrested date or time in hh:mm:ss, Times Arrested/Escaped/Murders
      - Global:  Most wanted user (Bounty, Status, Name), Closest Jail
  - BetonQuest
    - Provides extra Events, Objectives, and Conditions to build even more in depth quests/stories
  - LeaderHeads
    - Users love nothing more than looking at who is the worst or best criminal across your network (MySql, or Server via SQLite)
    - Top Current Bounties, Latest/Most Arrests, Latest/Most Escapes, Most Murders, Highest total bounties
  - Jobs Reborn
    - IN PROGRESS:  Add's a new job of bounty hunter. This will allow users to tag on and hunt down players with a bounty. This way they are not penalized for attacking players with a bounty on their heads. Once they get the player within 1 life point, the player is arrested.
  
## Required Plugins:
- [Citizens2 - Spigot](https://www.spigotmc.org/resources/citizens.13811/)  (NPC Creation)  **Read the citizens page for the free version!**   
- [Vault - Bukkit](http://dev.bukkit.org/bukkit-plugins/vault/)  (Economy plugin)     
- [WorldGuard - Spigot](http://wiki.sk89q.com/wiki/WorldGuard)  (Region and protection plugin)     
- [Sentinel - Spigot](https://www.spigotmc.org/resources/sentinel.22017/)  (NPC guard and attack mechanics plugin)     

## Spigot Resource Link:
**Spigot Link:** https://www.spigotmc.org/resources/npc-police-being-re-wrote.9553/
