package de.lebk.thirtyone.game.item;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public enum Suit implements SuitType
{
    CLUBS {
        @Override
        public int getRank()
        {
            return 3;
        }

        @Override
        public String toString()
        {
            return "Clubs";
        }
    },

    SPADES {
        @Override
        public int getRank()
        {
            return 2;
        }

        @Override
        public String toString()
        {
            return "Spades";
        }
    },

    HEARTS {
        @Override
        public int getRank()
        {
            return 1;
        }

        @Override
        public String toString()
        {
            return "Hearts";
        }
    },

    DIAMONDS {
        @Override
        public int getRank()
        {
            return 0;
        }

        @Override
        public String toString()
        {
            return "Diamonds";
        }
    };
}
