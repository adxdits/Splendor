package fr.uge.splendor.model;

public enum UserAction {
    CHOOSE_ACTION, // Action to choose the next action
    TAKE_TOKENS, // Action to take tokens from stacks
    BUY_CARD, // Action to buy a card from the board or from the player's borrowed cards
    BORROW_CARD, // Action to borrow a card from the board
    RETURN_TOKENS, // Action to return tokens to the stacks when the player has too many tokens
}
