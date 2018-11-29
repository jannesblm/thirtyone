package de.lebk.thirtyone.game.item;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public enum Suit implements SuitType
{
    CLUBS {
        @Override
        public URL getImageURL()
        {
            return this.getClass().getResource("/clubs.png");
        }

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
        public URL getImageURL()
        {
            return this.getClass().getResource("/spades.png");
        }

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
        public URL getImageURL()
        {
            return this.getClass().getResource("/hearts.png");
        }

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
        public URL getImageURL()
        {
            return this.getClass().getResource("/diamonds.png");
        }

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

    public BufferedImage getImage() throws IOException
    {
        return ImageIO.read(this.getImageURL());
    }
}
