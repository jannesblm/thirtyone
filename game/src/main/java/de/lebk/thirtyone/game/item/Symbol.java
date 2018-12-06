package de.lebk.thirtyone.game.item;

public enum Symbol implements SymbolType
{
    NUMBER {
        @Override
        public int getRank()
        {
            return 0;
        }
    },
    JACK {
        @Override
        public int getRank()
        {
            return 1;
        }
    },
    QUEEN {
        @Override
        public int getRank()
        {
            return 2;
        }
    },
    KING {
        @Override
        public int getRank()
        {
            return 3;
        }
    };
}
