package com.bzyd.tetris;

import java.awt.image.BufferedImage;

/**
 * 格子
 */
public class Cell {
	/**
	 * 行
	 */
	private int row;

	/**
	 * 列
	 */
	private int col;

	/**
	 * 贴图
	 */
	private BufferedImage image;

	public Cell(int row, int col, BufferedImage image) {
		super();
		this.row = row;
		this.col = col;
		this.image = image;
	}

	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public BufferedImage getImage() {
		return image;
	}
	public void setImage(BufferedImage image) {
		this.image = image;
	}


	/**
	 * 往下落
	 */
	public void softDrop() {
		row++;
	}

	/**
	 * 往左移
	 */
	public void moveLeft() {
		col--;
	}

	/**往右移
	 *
	 */
	public void moveRight() {
		col++;
	}

}
