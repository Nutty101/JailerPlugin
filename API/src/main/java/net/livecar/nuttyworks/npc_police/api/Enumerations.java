package net.livecar.nuttyworks.npc_police.api;

public class Enumerations {
    public enum CURRENT_STATUS {
        WANTED, ESCAPED, ARRESTED, JAILED, FREE {
            @Override
            public CURRENT_STATUS next() {
                return null;
            }
        };

        public static boolean contains(String value) {
            for (CURRENT_STATUS ename : CURRENT_STATUS.values()) {
                if (ename.toString().equalsIgnoreCase(value))
                    return true;
            }
            return false;
        }

        public CURRENT_STATUS next() {
            return values()[ordinal() + 1];
        }
    }

    public enum WANTED_LEVEL {
        GLOBAL, NONE, MINIMUM, LOW, MEDIUM, HIGH {
            @Override
            public WANTED_LEVEL next() {
                return WANTED_LEVEL.HIGH;
            }
        };

        public static boolean contains(String value) {
            for (WANTED_LEVEL ename : WANTED_LEVEL.values()) {
                if (ename.toString().equalsIgnoreCase(value))
                    return true;
            }
            return false;
        }

        public WANTED_LEVEL next() {
            if (ordinal() + 1 > values().length)
                return values()[values().length];
            else
                return values()[ordinal() + 1];

        }

        public WANTED_LEVEL previous() {
            if (ordinal() - 1 < 0)
                return values()[values().length];
            else
                return values()[ordinal() - 1];
        }
    }

    public enum WANTED_REASONS {
        THEFT, MURDER, ASSAULT, ESCAPE, PVP, BOUNTY, REGION, PLUGIN, DATABASE, USERCOMMAND {
            @Override
            public WANTED_REASONS next() {
                return null;
            }
        };

        public static boolean contains(String value) {
            for (WANTED_REASONS ename : WANTED_REASONS.values()) {
                if (ename.toString().equalsIgnoreCase(value))
                    return true;
            }
            return false;
        }

        public WANTED_REASONS next() {
            return values()[ordinal() + 1];
        }
    }

    public enum JAILED_GROUPS {
        WANTED, JAILED, ESCAPED {
            @Override
            public JAILED_GROUPS next() {
                return null;
            }
        };

        public static boolean contains(String value) {
            for (JAILED_GROUPS ename : JAILED_GROUPS.values()) {
                if (ename.toString().equalsIgnoreCase(value))
                    return true;
            }
            return false;
        }

        public JAILED_GROUPS next() {
            return values()[ordinal() + 1];
        }
    }

    public enum JAILED_BOUNTY {
        BOUNTY_DAMAGE, BOUNTY_ESCAPED, BOUNTY_MURDER, BOUNTY_PVP, TIMES_JAILED, TIMES_ESCAPED, TIMES_WANTED, TIMES_CELLOUT_NIGHT, TIMES_CELLOUT_DAY, MANUAL, PLUGIN, REGION {
            @Override
            public JAILED_BOUNTY next() {
                return null;
            }
        };

        public static boolean contains(String value) {
            for (JAILED_BOUNTY ename : JAILED_BOUNTY.values()) {
                if (ename.toString().equalsIgnoreCase(value))
                    return true;
            }
            return false;
        }

        public JAILED_BOUNTY next() {
            return values()[ordinal() + 1];
        }
    }

    public enum NOTICE_SETTING {
        ESCAPED, JAILED, MURDER, THEFT {
            @Override
            public NOTICE_SETTING next() {
                return null;
            }
        };

        public static boolean contains(String value) {
            for (NOTICE_SETTING ename : NOTICE_SETTING.values()) {
                if (ename.toString().equalsIgnoreCase(value))
                    return true;
            }
            return false;
        }

        public NOTICE_SETTING next() {
            return values()[ordinal() + 1];
        }
    }

    public enum COMMAND_LISTS {
        NPC_WARNING, NPC_ALERTGUARDS, NPC_NOGUARDS, NPC_MURDERED, PLAYER_WANTED, PLAYER_JAILED, PLAYER_ESCAPED, PLAYER_RELEASED, BOUNTY_MAXIMUM {
            @Override
            public COMMAND_LISTS next() {
                return null;
            }
        };

        public static boolean contains(String value) {
            for (COMMAND_LISTS ename : COMMAND_LISTS.values()) {
                if (ename.toString().equalsIgnoreCase(value))
                    return true;
            }
            return false;
        }

        public COMMAND_LISTS next() {
            return values()[ordinal() + 1];
        }
    }

    public enum STATE_SETTING {
        TRUE, FALSE, NOTSET {
            @Override
            public STATE_SETTING next() {
                return null;
            }
        };

        public static boolean contains(String value) {
            for (STATE_SETTING ename : STATE_SETTING.values()) {
                if (ename.toString().equalsIgnoreCase(value))
                    return true;
            }
            return false;
        }

        public STATE_SETTING next() {
            return values()[ordinal() + 1];
        }
    }

    public enum KICK_TYPE {
        NOTSET, ARREST_SERVER, ARREST_WORLD, CHANGE_SERVER, CHANGE_WORLD {
            @Override
            public KICK_TYPE next() {
                return values()[0];
            }
        };

        public static boolean contains(String value) {
            for (KICK_TYPE ename : KICK_TYPE.values()) {
                if (ename.toString().equalsIgnoreCase(value))
                    return true;
            }
            return false;
        }

        public KICK_TYPE next() {
            return values()[ordinal() + 1];
        }
    }

    public enum WANTED_SETTING {
        NONE, MINIMUM, LOW, MEDIUM, HIGH, LEVELUP, LEVELDOWN {
            @Override
            public WANTED_SETTING next() {
                return null;
            }
        };

        public static boolean contains(String value) {
            for (WANTED_SETTING ename : WANTED_SETTING.values()) {
                if (ename.toString().equalsIgnoreCase(value))
                    return true;
            }
            return false;
        }

        public static WANTED_LEVEL getWantedLevel(WANTED_SETTING setting) {
            return WANTED_LEVEL.valueOf(setting.toString());
        }

        public WANTED_SETTING next() {
            return values()[ordinal() + 1];
        }
    }

    public enum KICK_ACTION {
        WORLD, SERVER, NOACTION
    }

    public enum NPC_AWARDS {
        NONE, TIME, BOUNTY {
            @Override
            public NPC_AWARDS next() {
                return null;
            }
        };

        public static boolean contains(String value) {
            for (NPC_AWARDS ename : NPC_AWARDS.values()) {
                if (ename.toString().equalsIgnoreCase(value))
                    return true;
            }
            return false;
        }

        public static NPC_AWARDS getAwardType(NPC_AWARDS setting) {
            return NPC_AWARDS.valueOf(setting.toString());
        }

        public NPC_AWARDS next() {
            return values()[ordinal() + 1];
        }
    }
}
