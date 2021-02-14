package com.bzyd.tetris;

import java.util.Random;

/**
 * 表示4块格子
 */
public  abstract class Tetromino {
	/**
	 * 方块数组
	 */
	protected Cell[] cells = new Cell[4];

	/**
	 * 旋转状态数据
	 */
	protected State[] states;

	/**
	 * 旋转状态数据序号
	 */
	protected int index = 10000;
	
	protected class State{
		int row0,col0,row1,col1,
		    row2,col2,row3,col3;
		public State(int row0, int col0, int row1, int col1, int row2, int col2,
				int row3, int col3) {
			this.row0 = row0;
			this.col0 = col0;
			this.row1 = row1;
			this.col1 = col1;
			this.row2 = row2;
			this.col2 = col2;
			this.row3 = row3;
			this.col3 = col3;
		}
	}

	/**
	 * 随机产生4块放个
	 */
	public static Tetromino randomOne() {
		Random r = new Random();
		int type = r.nextInt(7);
		switch (type) {
		case 0:return new T();
		case 1:return new I();
		case 2:return new S();
		case 3:return new Z();
		case 4:return new O();
		case 5:return new L();
		case 6:return new J();
		default:return null;
		}
	}

	/**
	 * 下落
	 */
	public void softDrop() {
		for (int i = 0; i < cells.length; i++) {
			cells[i].softDrop();
		}
	}

	/**
	 * 左移
	 */
	public void moveLeft() {
		for (int i = 0; i < cells.length; i++) {
			cells[i].moveLeft();
		}
	}

	/**
	 * 右移
	 */
	public void moveRight() {
		for (int i = 0; i < cells.length; i++) {
			cells[i].moveRight();
		}
	}


	/**
	 * 右旋转
	 */
	public void rotateRight(){
		index++;
		//s = S1->S2->S3->S0->S1->S2...
		State s = states[index%states.length];
		// s=[row0,col0] [row1,col1] 
		//   [row2,col2] [row3,col3]
		//�᣿cells[0]
		Cell o = this.cells[0];
		int row = o.getRow();
		int col = o.getCol();
		//cell[1]
		cells[1].setRow(row + s.row1);
		cells[1].setCol(col + s.col1);
		cells[2].setRow(row + s.row2);
		cells[2].setCol(col + s.col2);
		cells[3].setRow(row + s.row3);
		cells[3].setCol(col + s.col3);
	}

	/**
	 * 左旋转
	 */
	public void rotateLeft(){
		index--;
		//s = S1<-S2<-S3<-S0<-S1<-S2...
		State s = states[index%states.length];
		Cell o = this.cells[0];
		int row = o.getRow();
		int col = o.getCol();
		cells[1].setRow(row + s.row1);
		cells[1].setCol(col + s.col1);
		cells[2].setRow(row + s.row2);
		cells[2].setCol(col + s.col2);
		cells[3].setRow(row + s.row3);
		cells[3].setCol(col + s.col3);
	}
}

/**
 * T形方块
 */
class T extends Tetromino {
	public T() {
		cells[0] = new Cell(0, 4, TetrisGame.T);
		cells[1] = new Cell(0, 3, TetrisGame.T);
		cells[2] = new Cell(0, 5, TetrisGame.T);
		cells[3] = new Cell(1, 4, TetrisGame.T);
		states = new State[4];
		states[0]=new State(0,0,0,-1,0,1,1,0);//S0
		states[1]=new State(0,0,-1,0,1,0,0,-1);//S1
		states[2]=new State(0,0,0,1,0,-1,-1,0);//S2
		states[3]=new State(0,0,1,0,-1,0,0,1);//S3
	}
}

/**
 * I形方块
 */
class I extends Tetromino {
	public I() {
		cells[0] = new Cell(0, 4, TetrisGame.I);
		cells[1] = new Cell(0, 3, TetrisGame.I);
		cells[2] = new Cell(0, 5, TetrisGame.I);
		cells[3] = new Cell(0, 6, TetrisGame.I);
		states = new State[2];
		states[0] = new State(0,0,0,-1,0,1,0,2);
		states[1] = new State(0,0,-1,0,1,0,2,0);
	}
}

/**
 * S形方块
 */
class S extends Tetromino {
	public S() {
		cells[0] = new Cell(1, 4, TetrisGame.S);
		cells[1] = new Cell(1, 3, TetrisGame.S);
		cells[2] = new Cell(0, 4, TetrisGame.S);
		cells[3] = new Cell(0, 5, TetrisGame.S);
		states = new State[2];
		states[0] = new State(0,0,0,-1,-1,0,-1,1);
		states[1] = new State(0,0,-1,0,0,1,1,1);
	}
}

/**
 * Z形方块
 */
class Z extends Tetromino {
	public Z() {
		cells[0] = new Cell(1, 4, TetrisGame.Z);
		cells[1] = new Cell(0, 3, TetrisGame.Z);
		cells[2] = new Cell(0, 4, TetrisGame.Z);
		cells[3] = new Cell(1, 5, TetrisGame.Z);
		states = new State[2];
		states[0] = new State(0,0,-1,-1,-1,0,0,1);
		states[1] = new State(0,0,-1,1,0,1,1,0);
	}
}

/**
 * O形方块
 */
class O extends Tetromino {
	public O() {
		cells[0] = new Cell(0, 4, TetrisGame.O);
		cells[1] = new Cell(0, 5, TetrisGame.O);
		cells[2] = new Cell(1, 4, TetrisGame.O);
		cells[3] = new Cell(1, 5, TetrisGame.O);
		states = new State[2];
		states[0] = new State(0,0,0,1,1,0,1,1);
		states[1] = new State(0,0,0,1,1,0,1,1);
	}
}

/**
 * L形方块
 */
class L extends Tetromino {
	public L() {
		cells[0] = new Cell(0, 4, TetrisGame.L);
		cells[1] = new Cell(0, 3, TetrisGame.L);
		cells[2] = new Cell(0, 5, TetrisGame.L);
		cells[3] = new Cell(1, 3, TetrisGame.L);
		states = new State[4];
		states[0] = new State(0,0,0,1,0,-1,-1,1);
		states[1] = new State(0,0,1,0,-1,0,1,1);
		states[2] = new State(0,0,0,-1,0,1,1,-1);
		states[3] = new State(0,0,-1,0,1,0,-1,-1);
	}
}

/**
 * J形方块
 */
class J extends Tetromino {
	public J() {
		cells[0] = new Cell(0, 4, TetrisGame.J);
		cells[1] = new Cell(0, 3, TetrisGame.J);
		cells[2] = new Cell(0, 5, TetrisGame.J);
		cells[3] = new Cell(1, 5, TetrisGame.J);
		states = new State[4];
		states[0] = new State(0,0,0,-1,0,1,1,1);
		states[1] = new State(0,0,-1,0,1,0,1,-1);
		states[2] = new State(0,0,0,1,0,-1,-1,-1);
		states[3] = new State(0,0,1,0,-1,0,-1,1);
	}
}
