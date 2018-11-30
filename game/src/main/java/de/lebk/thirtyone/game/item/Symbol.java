/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lebk.thirtyone.game.item;

/**
 *
 * @author BlumeJannes
 */
public enum Symbol implements SymbolType {
    
    NUMBER {
        @Override
        public int getRank() {
           return 0;
        }
        
    },
    
    JACK {
        @Override
        public int getRank() {
            return 1;
        }
    },
    
    QUEEN {
        @Override
        public int getRank() {
           return 2;
        }
    },
    
    KING {
        @Override
        public int getRank() {
            return 3;
        }
    };
    
}
