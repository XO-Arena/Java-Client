/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import enums.PlayerSymbol;
import models.Board;

/**
 *
 * @author mohannad
 */
public class BoardDTO {
    private PlayerSymbol[][] cells;
    private String winCode;
    
    public BoardDTO() {}

    public BoardDTO(PlayerSymbol[][] cells) {
        this.cells = cells;
    }
    
    public BoardDTO(PlayerSymbol[][] cells, String winCode) {
        this.cells = cells;
        this.winCode = winCode;
    }
    
    public static BoardDTO fromModel(Board board) {
        return new BoardDTO(
                board.getCells(),
                board.getWinCode()
        );
    }
    
    public PlayerSymbol[][] getCells() {
        return cells;
    }

    public void setCells(PlayerSymbol[][] cells) {
        this.cells = cells;
    }

    public String getWinCode() {
        return winCode;
    }

    public void setWinCode(String winCode) {
        this.winCode = winCode;
    }
}
